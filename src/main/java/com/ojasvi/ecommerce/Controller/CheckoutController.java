package com.ojasvi.ecommerce.Controller;

import java.math.BigDecimal;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.ojasvi.ecommerce.Entity.Cart;
import com.ojasvi.ecommerce.Entity.Order;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Security.SessionUtil;
import com.ojasvi.ecommerce.Service.CartService;
import com.ojasvi.ecommerce.Service.CheckoutService;

import jakarta.servlet.http.HttpSession;

@Controller
public class CheckoutController {

    @Autowired
    private CartService cartService;

    @Autowired
    private CheckoutService checkoutService;

    @Autowired
    private HttpSession session;

    @GetMapping("/checkout")
    public String checkout(Model model) {

        User user = SessionUtil.getLoggedInUser(session);

        if (user == null) {
            return "redirect:/login";
        }

        Cart cart = cartService.getCartByUser(user);

        if (cart.getTotalItems() == null || cart.getTotalItems() == 0) {
            return "redirect:/cart";
        }

        BigDecimal shippingCharge = BigDecimal.ZERO;
        BigDecimal couponDiscount = BigDecimal.ZERO;
        BigDecimal codCharge = BigDecimal.ZERO;

        BigDecimal grandTotal =
                cart.getTotalAmount()
                        .add(shippingCharge)
                        .add(codCharge)
                        .subtract(couponDiscount);

        model.addAttribute("user", user);
        model.addAttribute("cart", cart);
        model.addAttribute("cartItems", cartService.getCartItems(user));

        model.addAttribute("totalAmount", cart.getTotalAmount());
        model.addAttribute("shippingCharge", shippingCharge);
        model.addAttribute("couponDiscount", couponDiscount);
        model.addAttribute("codCharge", codCharge);
        model.addAttribute("grandTotal", grandTotal);

        return "checkout";
    }

    @PostMapping("/checkout/place-order")
    @ResponseBody
    public Map<String, Object> placeOrder() {

        User user = SessionUtil.getLoggedInUser(session);

        if (user == null) {
            return Map.of(
                    "success", false,
                    "message", "Please login first"
            );
        }

        Order order = checkoutService.placeOrder(user);

        return Map.of(
                "success", true,
                "message", "Order placed successfully",
                "redirectUrl",
                "/order-success/" + order.getOrderNumber()
        );
    }
    
    @GetMapping("/order-success/{orderNumber}")
    public String orderSuccess(
            @PathVariable String orderNumber,
            Model model) {

        model.addAttribute("orderNumber", orderNumber);

        return "order-success";
    }
}
