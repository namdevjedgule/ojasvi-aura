package com.ojasvi.ecommerce.Controller;

import java.time.LocalTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.ShippingConfig;
import com.ojasvi.ecommerce.Entity.ShippingZone;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Service.ShippingService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/shipping")
public class AdminShippingController {
	
	@Autowired
	private ShippingService shippingService;

    @GetMapping
    public String shipping(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", admin);
        
        model.addAttribute(
                "shippingConfig",
                shippingService.getConfig());

        model.addAttribute(
                "shippingZones",
                shippingService.getAllZones());

        return "admin/shipping";
    }
    
    @PostMapping("/update-method")
    public String updateMethod(
            @RequestParam String methodKey,
            @RequestParam(required = false) String enabled,
            @RequestParam Double charge,
            @RequestParam Double freeAbove,
            RedirectAttributes ra) {

        ShippingConfig config = shippingService.getConfig();

        boolean isEnabled = enabled != null;

        if ("standard".equals(methodKey)) {

            config.setStandardEnabled(isEnabled);
            config.setStandardCharge(charge);
            config.setStandardFreeAbove(freeAbove);

        } else if ("express".equals(methodKey)) {

            config.setExpressEnabled(isEnabled);
            config.setExpressCharge(charge);
            config.setExpressFreeAbove(freeAbove);
        }

        shippingService.saveConfig(config);

        ra.addFlashAttribute(
                "success",
                "Shipping method updated.");

        return "redirect:/admin/shipping";
    }
    
    @PostMapping("/update-cod")
    public String updateCod(
            @RequestParam(required = false)
            String codEnabled,
            @RequestParam Double codCharge,
            @RequestParam(required = false) Double codMaxOrder,
            RedirectAttributes ra) {

        ShippingConfig config =
                shippingService.getConfig();

        config.setCodEnabled(
                codEnabled != null);

        config.setCodCharge(
                codCharge);

        config.setCodMaxOrder(
                codMaxOrder);

        shippingService.saveConfig(config);

        ra.addFlashAttribute(
                "success",
                "COD settings updated.");

        return "redirect:/admin/shipping";
    }
    
    @PostMapping("/update-general")
    public String updateGeneral(

            @RequestParam Double freeShippingThreshold,

            @RequestParam String deliveryMessage,

            @RequestParam LocalTime cutoffTime,

            RedirectAttributes ra) {

        ShippingConfig config =
                shippingService.getConfig();

        config.setFreeShippingThreshold(
                freeShippingThreshold);

        config.setDeliveryMessage(
                deliveryMessage);

        config.setCutoffTime(
                cutoffTime);

        shippingService.saveConfig(config);

        ra.addFlashAttribute(
                "success",
                "General shipping settings updated.");

        return "redirect:/admin/shipping";
    }
    
    @PostMapping("/zones/save")
    public String saveZone(
            @ModelAttribute ShippingZone zone,
            RedirectAttributes ra) {

        if (zone.getZoneName() == null ||
            zone.getZoneName().isBlank()) {

            ra.addFlashAttribute(
                    "error",
                    "Zone name required");

            return "redirect:/admin/shipping";
        }

        shippingService.saveZone(zone);

        ra.addFlashAttribute(
                "success",
                "Shipping zone saved successfully.");

        return "redirect:/admin/shipping";
    }
    
    @PostMapping("/zones/delete/{id}")
    public String deleteZone(
            @PathVariable Long id,
            RedirectAttributes ra) {

        shippingService.deleteZone(id);

        ra.addFlashAttribute(
                "success",
                "Zone deleted successfully.");

        return "redirect:/admin/shipping";
    }
}
