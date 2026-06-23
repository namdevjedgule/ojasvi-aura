package com.ojasvi.ecommerce.Controller;

import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.NewsletterSubscriber;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.NewsletterService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/newsletter")
public class AdminNewsletterController {
	
	@Autowired
	private NewsletterService newsletterService;

    @GetMapping
    public String newsletterPage(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", admin);
        
        model.addAttribute("subscribers", newsletterService.getAllSubscribers());

        model.addAttribute("campaigns", newsletterService.getCampaigns());

        model.addAttribute("totalSubscribers", newsletterService.getTotalSubscribers());

        model.addAttribute("newSubscribersThisMonth", newsletterService.getNewSubscribersThisMonth());

        model.addAttribute("campaignsSent", newsletterService.getCampaignsSent());

        model.addAttribute("unsubscribedCount", newsletterService.getUnsubscribedCount());

        return "admin/newsletter";
    }
    
    @PostMapping("/send")
    public String sendCampaign(
            @RequestParam String recipientType,
            @RequestParam String subject,
            @RequestParam String message,
            RedirectAttributes redirectAttributes
    ) {

        try {

            newsletterService.sendCampaign(
                    recipientType,
                    subject,
                    message
            );

            redirectAttributes.addFlashAttribute(
                    "success",
                    "Campaign sent successfully."
            );

        } catch (Exception e) {

            redirectAttributes.addFlashAttribute(
                    "error",
                    e.getMessage()
            );
        }

        return "redirect:/admin/newsletter";
    }

    @PostMapping("/remove/{id}")
    public String removeSubscriber(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes
    ) {

        newsletterService.removeSubscriber(id);

        redirectAttributes.addFlashAttribute(
                "success",
                "Subscriber removed successfully."
        );

        return "redirect:/admin/newsletter";
    }
    
    @GetMapping("/export")
    public void exportSubscribers(HttpServletResponse response)
            throws IOException {

        response.setContentType("text/csv");

        response.setHeader(
                "Content-Disposition",
                "attachment; filename=subscribers.csv"
        );

        CSVPrinter csvPrinter = new CSVPrinter(
                response.getWriter(),
                CSVFormat.DEFAULT.withHeader(
                        "Email",
                        "Subscribed At",
                        "Status"
                )
        );

        for (NewsletterSubscriber sub : newsletterService.getAllSubscribers()) {

            csvPrinter.printRecord(
                    sub.getEmail(),
                    sub.getSubscribedAt(),
                    sub.getIsActive() ? "Subscribed" : "Unsubscribed"
            );
        }

        csvPrinter.flush();
    }
}
