package com.ojasvi.ecommerce.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SupportController {

    @GetMapping("/shipping")
    public String shipping() {
        return "shipping";
    }

    @GetMapping("/returns")
    public String returns() {
        return "returns";
    }

    @GetMapping("/faq")
    public String faq() {
        return "faq";
    }

    @GetMapping("/privacy-policy")
    public String privacyPolicy() {
        return "privacy";
    }

    @GetMapping("/terms-conditions")
    public String termsConditions() {
        return "terms";
    }
}
