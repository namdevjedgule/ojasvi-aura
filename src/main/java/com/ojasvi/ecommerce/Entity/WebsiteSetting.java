package com.ojasvi.ecommerce.Entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "website_settings")
@Getter
@Setter
public class WebsiteSetting extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String websiteName;

    private String logo;

    private String supportEmail;

    private String supportMobile;

    private String address;

    private String facebookUrl;

    private String instagramUrl;

    private String pinterestUrl;

    private String youtubeUrl;

    private String twitterUrl;
}
