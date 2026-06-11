package com.ojasvi.ecommerce.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ojasvi.ecommerce.Entity.WebsiteSetting;
import com.ojasvi.ecommerce.Repository.WebsiteSettingRepository;

@RestController
@RequestMapping("/api")
public class WebsiteSettingController {

    @Autowired
    private WebsiteSettingRepository websiteSettingRepository;

    @GetMapping("/settings")
    public WebsiteSetting getSettings() {
        return websiteSettingRepository.findAll().get(0);
    }

}
