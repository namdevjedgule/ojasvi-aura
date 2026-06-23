package com.ojasvi.ecommerce.Controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.ojasvi.ecommerce.DTO.ApplyCouponRequest;
import com.ojasvi.ecommerce.DTO.RemoveCouponRequest;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.AccessValidator;
import com.ojasvi.ecommerce.Service.CustomerCouponService;

import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/customer/coupons")
@RequiredArgsConstructor
public class CustomerCouponController {

    private final CustomerCouponService customerCouponService;

    @GetMapping
    public String customerCoupon(HttpSession session,
                                 Model model) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            return "redirect:/login";
        }

        if (!AccessValidator.isCustomer(user)) {
            return "redirect:/admin-dashboard";
        }

        model.addAttribute("user", user);
        
        model.addAllAttributes(
                customerCouponService.getCustomerCoupons(
                        user.getId()
                )
        );

        return "customer/customer-coupons";
    }

    @GetMapping("/data")
    @ResponseBody
    public ResponseEntity<?> getCoupons(
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.getCustomerCoupons(
                        user.getId()
                )
        );
    }

    @GetMapping("/available")
    @ResponseBody
    public ResponseEntity<?> getAvailableCoupons(
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.getAvailableCoupons(
                        user.getId()
                )
        );
    }

    @GetMapping("/used")
    @ResponseBody
    public ResponseEntity<?> getUsedCoupons(
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.getUsedCoupons(
                        user.getId()
                )
        );
    }

    @GetMapping("/code/{code}")
    @ResponseBody
    public ResponseEntity<?> getCouponDetails(
            @PathVariable String code,
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.getCouponDetails(
                        user.getId(),
                        code
                )
        );
    }

    @GetMapping("/validate")
    @ResponseBody
    public ResponseEntity<?> validateCoupon(
            @RequestParam String code,
            @RequestParam BigDecimal cartTotal,
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.validateCoupon(
                        user.getId(),
                        code,
                        cartTotal
                )
        );
    }

    @PostMapping("/apply")
    @ResponseBody
    public ResponseEntity<?> applyCoupon(
            @RequestBody ApplyCouponRequest request,
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        return ResponseEntity.ok(
                customerCouponService.applyCoupon(
                        user.getId(),
                        request
                )
        );
    }

    @PostMapping("/remove")
    @ResponseBody
    public ResponseEntity<?> removeCoupon(
            @RequestBody RemoveCouponRequest request,
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        customerCouponService.removeCoupon(
                user.getId(),
                request.getCouponCode()
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Coupon removed successfully"
                )
        );
    }

    @PostMapping("/copy")
    @ResponseBody
    public ResponseEntity<?> trackCopyEvent(
            @RequestParam String code,
            HttpSession session) {

        User user = getLoggedInCustomer(session);

        customerCouponService.trackCopyEvent(
                user.getId(),
                code
        );

        return ResponseEntity.ok(
                Map.of(
                        "success", true,
                        "message", "Coupon copy tracked"
                )
        );
    }

    private User getLoggedInCustomer(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new RuntimeException("Please login to continue.");
        }

        if (!AccessValidator.isCustomer(user)) {
            throw new RuntimeException("Access denied.");
        }

        return user;
    }
}
