package com.ojasvi.ecommerce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.Coupon;
import com.ojasvi.ecommerce.Repository.CouponRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CouponService {

	@Autowired
	private CouponRepository couponRepository;

	public Coupon save(Coupon coupon) {
		return couponRepository.save(coupon);
	}

	public List<Coupon> getAllCoupons() {
		return couponRepository.findAll();
	}

	public Coupon getById(Long id) {
		return couponRepository.findById(id).orElse(null);
	}

	public void delete(Long id) {
		couponRepository.deleteById(id);
	}

	public long getTotalCoupons() {
		return couponRepository.count();
	}

	public long getActiveCoupons() {
		return couponRepository.findAll().stream().filter(c -> Boolean.TRUE.equals(c.getIsActive())).count();
	}

	public long getTotalUses() {
		return couponRepository.findAll().stream().mapToLong(c -> c.getUsedCount() == null ? 0 : c.getUsedCount())
				.sum();
	}

	public BigDecimal getTotalSavings() {
		return BigDecimal.ZERO;
	}

	public Coupon findByCode(String code) {

		if (code == null || code.trim().isEmpty()) {
			return null;
		}

		return couponRepository.findByCodeIgnoreCase(code.trim()).orElse(null);
	}
}
