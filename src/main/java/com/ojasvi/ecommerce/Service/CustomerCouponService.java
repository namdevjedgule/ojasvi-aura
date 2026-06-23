package com.ojasvi.ecommerce.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.math.RoundingMode;
import java.time.LocalDateTime;

import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.DTO.ApplyCouponRequest;
import com.ojasvi.ecommerce.DTO.ApplyCouponResponse;
import com.ojasvi.ecommerce.DTO.CouponValidationResponse;
import com.ojasvi.ecommerce.DTO.CustomerCouponDto;
import com.ojasvi.ecommerce.Entity.Coupon;
import com.ojasvi.ecommerce.Repository.CouponRepository;
import com.ojasvi.ecommerce.Repository.CustomerCouponRepository;

@Service
@RequiredArgsConstructor
public class CustomerCouponService {

	@Autowired
    private CustomerCouponRepository customerCouponRepository;
	
	@Autowired
	private CouponRepository couponRepository;

	public Map<String, Object> getCustomerCoupons(Long customerId) {

        List<CustomerCouponDto> coupons = getAvailableCoupons(customerId);

        long expiringSoon = coupons.stream()
                .filter(c -> c.getDaysLeft() != null)
                .filter(c -> c.getDaysLeft() >= 0)
                .filter(c -> c.getDaysLeft() <= 7)
                .count();

        Map<String, Object> map = new HashMap<>();

        map.put("coupons", coupons);
        map.put("availableCoupons", coupons.size());

        map.put("usedCoupons", 0);
        map.put("totalSaved", BigDecimal.ZERO);
        map.put("expiringSoon", expiringSoon);
        map.put("usedCouponsList", List.of());

        return map;
    }

    public List<CustomerCouponDto> getAvailableCoupons(Long customerId) {

        return couponRepository
                .findByDeletedFalseAndExpiryDateAfter(LocalDateTime.now())
                .stream()
                .map(this::convertCoupon)
                .toList();
    }

    public List<CustomerCouponDto> getUsedCoupons(Long customerId) {

        return List.of();
    }

    public CustomerCouponDto getCouponDetails(
            Long customerId,
            String code) {

        Coupon coupon = couponRepository
                .findByCodeIgnoreCaseAndDeletedFalse(code)
                .orElseThrow(() ->
                        new RuntimeException("Coupon not found"));

        return convertCoupon(coupon);
    }

    public CouponValidationResponse validateCoupon(
            Long customerId,
            String code,
            BigDecimal cartTotal) {

        Coupon coupon = couponRepository
                .findByCodeIgnoreCaseAndDeletedFalse(code)
                .orElseThrow(() ->
                        new RuntimeException("Invalid coupon code"));

        CouponValidationResponse response =
                new CouponValidationResponse();

        if (coupon.getExpiryDate() != null
                && coupon.getExpiryDate().isBefore(LocalDateTime.now())) {

            response.setValid(false);
            response.setMessage("Coupon expired");

            return response;
        }

        if (coupon.getStartDate() != null
                && coupon.getStartDate().isAfter(LocalDateTime.now())) {

            response.setValid(false);
            response.setMessage("Coupon is not active yet");

            return response;
        }

        if (coupon.getMinOrderAmount() != null
                && cartTotal.compareTo(coupon.getMinOrderAmount()) < 0) {

            response.setValid(false);
            response.setMessage(
                    "Minimum order amount is ₹"
                            + coupon.getMinOrderAmount());

            return response;
        }

        if (coupon.getMaxUses() != null
                && coupon.getUsedCount() >= coupon.getMaxUses()) {

            response.setValid(false);
            response.setMessage("Coupon usage limit exceeded");

            return response;
        }

        BigDecimal discount =
                calculateDiscount(coupon, cartTotal);

        response.setValid(true);
        response.setDiscount(discount);
        response.setMessage("Coupon applied successfully");

        return response;
    }

    public ApplyCouponResponse applyCoupon(
            Long customerId,
            ApplyCouponRequest request) {

        CouponValidationResponse validation =
                validateCoupon(
                        customerId,
                        request.getCouponCode(),
                        request.getCartTotal()
                );

        if (!validation.isValid()) {
            throw new RuntimeException(validation.getMessage());
        }

        Coupon coupon = couponRepository
                .findByCodeIgnoreCaseAndDeletedFalse(
                        request.getCouponCode()
                )
                .orElseThrow(() ->
                        new RuntimeException("Coupon not found"));

        ApplyCouponResponse response =
                new ApplyCouponResponse();

        response.setCouponCode(coupon.getCode());

        response.setDiscount(validation.getDiscount());

        response.setFinalAmount(
                request.getCartTotal()
                        .subtract(validation.getDiscount()));

        response.setMessage("Coupon applied successfully");

        return response;
    }

    public void removeCoupon(
            Long customerId,
            String couponCode) {

        // remove from session/cart if required
    }

    public void trackCopyEvent(
            Long customerId,
            String code) {

        // optional analytics
    }

    private BigDecimal calculateDiscount(
            Coupon coupon,
            BigDecimal cartTotal) {

        BigDecimal discount;

        switch (coupon.getDiscountType()) {

            case PERCENT:

                discount = cartTotal
                        .multiply(coupon.getDiscountValue())
                        .divide(
                                BigDecimal.valueOf(100),
                                2,
                                RoundingMode.HALF_UP
                        );

                if (coupon.getMaxDiscountAmount() != null) {

                    discount = discount.min(
                            coupon.getMaxDiscountAmount());
                }

                break;

            case FLAT:

                discount = coupon.getDiscountValue();
                break;

            default:

                discount = BigDecimal.ZERO;
        }

        return discount.min(cartTotal);
    }

    private CustomerCouponDto convertCoupon(Coupon coupon) {

        CustomerCouponDto dto = new CustomerCouponDto();

        dto.setCode(coupon.getCode());
        dto.setTitle(coupon.getTitle());
        dto.setDescription(coupon.getDescription());

        dto.setDiscountType(coupon.getDiscountType());
        dto.setDiscountValue(coupon.getDiscountValue());

        dto.setMinOrderAmount(coupon.getMinOrderAmount());

        dto.setExpiryDate(coupon.getExpiryDate());

        if (coupon.getExpiryDate() != null) {

            long daysLeft = ChronoUnit.DAYS.between(
                    LocalDate.now(),
                    coupon.getExpiryDate().toLocalDate()
            );

            dto.setDaysLeft(daysLeft);
        }

        return dto;
    }
}
