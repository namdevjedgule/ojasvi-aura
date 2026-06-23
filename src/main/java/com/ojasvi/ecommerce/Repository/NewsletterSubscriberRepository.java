package com.ojasvi.ecommerce.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.ojasvi.ecommerce.Entity.NewsletterSubscriber;

@Repository
public interface NewsletterSubscriberRepository extends JpaRepository<NewsletterSubscriber, Long> {

    Optional<NewsletterSubscriber> findByEmail(String email);

    List<NewsletterSubscriber> findByIsActiveTrue();

    long countByIsActiveTrue();

    long countByIsActiveFalse();

    long countBySubscribedAtBetween(
            LocalDateTime start,
            LocalDateTime end
    );
}
