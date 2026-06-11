package com.ojasvi.ecommerce.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.WebsiteSetting;

@Repository
public interface WebsiteSettingRepository extends JpaRepository<WebsiteSetting, Long> {

}
