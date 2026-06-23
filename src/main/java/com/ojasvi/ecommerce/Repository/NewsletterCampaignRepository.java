package com.ojasvi.ecommerce.Repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.NewsletterCampaign;

@Repository
public interface NewsletterCampaignRepository extends JpaRepository<NewsletterCampaign, Long> {

    long countByStatus(String status);

    List<NewsletterCampaign> findTop10ByOrderBySentAtDesc();
}
