package com.ojasvi.ecommerce.Controller;

import java.io.File;
import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.ojasvi.ecommerce.Entity.StoreSettings;
import com.ojasvi.ecommerce.Entity.User;
import com.ojasvi.ecommerce.Repository.UserRepository;
import com.ojasvi.ecommerce.Service.StoreSettingsService;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin/settings")
public class AdminSettingsController {
	
	@Autowired
    private StoreSettingsService storeSettingsService;

    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder =
            new BCryptPasswordEncoder();

    @GetMapping
    public String settings(HttpSession session, Model model) {

        User admin = (User) session.getAttribute("user");

        if (admin == null) {
            return "redirect:/login";
        }
        
        model.addAttribute("user", admin);
        
        model.addAttribute("settings",
        		storeSettingsService.getSettings());

        return "admin/settings";
    }
    
    @PostMapping("/general")
    public String saveGeneral(
            @ModelAttribute StoreSettings form,
            RedirectAttributes ra) {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        settings.setStoreName(form.getStoreName());
        settings.setTagline(form.getTagline());
        settings.setStoreEmail(form.getStoreEmail());
        settings.setPhone(form.getPhone());
        settings.setCurrency(form.getCurrency());
        settings.setDescription(form.getDescription());
        settings.setGstNumber(form.getGstNumber());

        settings.setAddressLine1(form.getAddressLine1());
        settings.setCity(form.getCity());
        settings.setState(form.getState());
        settings.setPincode(form.getPincode());
        settings.setCountry(form.getCountry());

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "General settings updated successfully.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/branding")
    public String saveBranding(
            @RequestParam(required = false)
            MultipartFile logoFile,

            @RequestParam(required = false)
            MultipartFile faviconFile,

            RedirectAttributes ra)
            throws IOException {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        String uploadDir =
                System.getProperty("user.dir")
                + File.separator
                + "uploads"
                + File.separator
                + "settings"
                + File.separator;

        File dir = new File(uploadDir);

        if (!dir.exists()) {
            dir.mkdirs();
        }

        if (logoFile != null &&
                !logoFile.isEmpty()) {

            String logoName =
                    System.currentTimeMillis()
                    + "_" +
                    logoFile.getOriginalFilename();

            logoFile.transferTo(
                    new File(uploadDir + logoName));

            settings.setLogoPath(
                    "/uploads/settings/" + logoName);
        }

        if (faviconFile != null &&
                !faviconFile.isEmpty()) {

            String faviconName =
                    System.currentTimeMillis()
                    + "_" +
                    faviconFile.getOriginalFilename();

            faviconFile.transferTo(
                    new File(uploadDir + faviconName));

            settings.setFaviconPath(
                    "/uploads/settings/" + faviconName);
        }

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "Branding updated successfully.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/contact")
    public String saveContact(
            @ModelAttribute StoreSettings form,
            RedirectAttributes ra) {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        settings.setSupportEmail(
                form.getSupportEmail());

        settings.setSupportPhone(
                form.getSupportPhone());

        settings.setInstagram(
                form.getInstagram());

        settings.setFacebook(
                form.getFacebook());

        settings.setWhatsapp(
                form.getWhatsapp());

        settings.setSupportHours(
                form.getSupportHours());

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "Contact settings updated.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/seo")
    public String saveSeo(
            @ModelAttribute StoreSettings form,
            RedirectAttributes ra) {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        settings.setMetaTitle(
                form.getMetaTitle());

        settings.setMetaDescription(
                form.getMetaDescription());

        settings.setMetaKeywords(
                form.getMetaKeywords());

        settings.setGoogleAnalyticsId(
                form.getGoogleAnalyticsId());

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "SEO settings updated.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/notifications")
    public String saveNotifications(

            @RequestParam(defaultValue = "false")
            boolean notifyNewOrder,

            @RequestParam(defaultValue = "false")
            boolean notifyOrderCancel,

            @RequestParam(defaultValue = "false")
            boolean notifyLowStock,

            @RequestParam(defaultValue = "false")
            boolean notifyNewCustomer,

            @RequestParam(defaultValue = "false")
            boolean notifyPaymentFailed,

            RedirectAttributes ra) {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        settings.setNotifyNewOrder(
                notifyNewOrder);

        settings.setNotifyOrderCancel(
                notifyOrderCancel);

        settings.setNotifyLowStock(
                notifyLowStock);

        settings.setNotifyNewCustomer(
                notifyNewCustomer);

        settings.setNotifyPaymentFailed(
                notifyPaymentFailed);

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "Notification settings updated.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/profile")
    public String updateProfile(
            @RequestParam String fullName,
            @RequestParam String mobile,
            HttpSession session,
            RedirectAttributes ra) {

        User user =
                (User) session.getAttribute("user");

        user.setFullName(fullName);
        user.setMobile(mobile);

        userRepository.save(user);

        session.setAttribute("user", user);

        ra.addFlashAttribute("success",
                "Profile updated successfully.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/change-password")
    public String changePassword(

            @RequestParam String currentPassword,

            @RequestParam String newPassword,

            @RequestParam String confirmPassword,

            HttpSession session,

            RedirectAttributes ra) {

        User user =
                (User) session.getAttribute("user");

        if (!passwordEncoder.matches(
                currentPassword,
                user.getPassword())) {

            ra.addFlashAttribute("error",
                    "Current password is incorrect.");

            return "redirect:/admin/settings";
        }

        if (!newPassword.equals(confirmPassword)) {

            ra.addFlashAttribute("error",
                    "Passwords do not match.");

            return "redirect:/admin/settings";
        }

        user.setPassword(
                passwordEncoder.encode(newPassword));

        userRepository.save(user);

        ra.addFlashAttribute("success",
                "Password updated successfully.");

        return "redirect:/admin/settings";
    }

    @PostMapping("/maintenance")
    public String toggleMaintenance(
            RedirectAttributes ra) {

        StoreSettings settings =
        		storeSettingsService.getSettings();

        settings.setMaintenanceMode(
                !settings.getMaintenanceMode());

        storeSettingsService.save(settings);

        ra.addFlashAttribute("success",
                "Maintenance mode updated.");

        return "redirect:/admin/settings";
    }
    
    @PostMapping("/clear-otps")
    public String clearOtps(RedirectAttributes ra) {

        ra.addFlashAttribute("success",
                "All OTPs cleared.");

        return "redirect:/admin/settings";
    }
    
    @PostMapping("/clear-image-cache")
    public String clearImageCache(
            RedirectAttributes ra) {

        ra.addFlashAttribute("success",
                "Image cache cleared.");

        return "redirect:/admin/settings";
    }
}
