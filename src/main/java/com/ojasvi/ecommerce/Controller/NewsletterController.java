package com.ojasvi.ecommerce.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Service.NewsletterService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class NewsletterController {

    private final NewsletterService newsletterService;

    @PostMapping("/newsletter/subscribe")
    public String subscribe(
            @RequestParam String email,
            RedirectAttributes redirectAttributes
    ) {

        try {

            newsletterService.subscribe(email);

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Thank you for subscribing."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/";
    }
    
    @GetMapping("/newsletter/unsubscribe/{id}")
    public String unsubscribe(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        newsletterService.removeSubscriber(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "You have been unsubscribed successfully."
        );

        return "redirect:/";
    }
}
