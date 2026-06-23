package com.ojasvi.ecommerce.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.NewsletterCampaign;
import com.ojasvi.ecommerce.Entity.NewsletterSubscriber;
import com.ojasvi.ecommerce.Repository.NewsletterCampaignRepository;
import com.ojasvi.ecommerce.Repository.NewsletterSubscriberRepository;

import jakarta.mail.internet.MimeMessage;

@Service
public class NewsletterService {

	@Autowired
	private NewsletterSubscriberRepository subscriberRepository;

	@Autowired
	private NewsletterCampaignRepository campaignRepository;

	@Autowired
	private JavaMailSender mailSender;

	@Value("${app.base-url}")
	private String baseUrl;

	@Value("${app.logo-url}")
	private String logoUrl;

	@Value("${spring.mail.username}")
	private String fromEmail;

	public void subscribe(String email) {

		NewsletterSubscriber subscriber;

		Optional<NewsletterSubscriber> optional = subscriberRepository.findByEmail(email);

		if (optional.isPresent()) {

			subscriber = optional.get();

			subscriber.setIsActive(true);
			subscriber.setUnsubscribedAt(null);
			subscriber.setSubscribedAt(LocalDateTime.now());

		} else {

			subscriber = new NewsletterSubscriber();

			subscriber.setEmail(email);
			subscriber.setSubscribedAt(LocalDateTime.now());
		}

		subscriber = subscriberRepository.save(subscriber);

		try {

			String unsubscribeUrl = baseUrl + "/newsletter/unsubscribe/" + subscriber.getId();

			sendWelcomeEmail(subscriber.getEmail(), unsubscribeUrl);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	private void sendWelcomeEmail(String to, String unsubscribeUrl) throws Exception {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

		helper.setTo(to);
		helper.setFrom(fromEmail);
		helper.setSubject("Welcome to Ojasvi Aura ✨");

		String content = """
				<div style="
				    max-width: 600px;
				    margin: 20px auto;
				    border: 1px solid #f0f0f0;
				    border-radius: 16px;
				    overflow: hidden;
				    background-color: #ffffff;
				    font-family: Arial, Helvetica, sans-serif;
				    color: #333333;
				">

				    <div style="
				        background: #5C3013;
				        padding: 20px 16px;
				        text-align: center;
				    ">

				        <img
				            src="%s"
				            alt="Ojasvi Aura"
				            style="
				                max-width: 60px;
				                height: auto;
				                margin-bottom: 9px;
				            ">

				        <div style="
				            color: #ffffff;
				            font-size: 9px;
				            letter-spacing: 2px;
				            text-transform: uppercase;
				        ">
				            Bringing Life into your Sanctuary.
				        </div>

				    </div>

				    <div style="
				        padding: 32px 32px;
				        line-height: 1.8;
				    ">

				        <h2 style="
				            color: #AE7F4D;
				            margin-top: 0;
				            margin-bottom: 14px;
				        ">
				            Thank you for subscribing! ✨
				        </h2>

				        <p>
				            Welcome to the <strong>Ojasvi Aura</strong> community.
				        </p>

				        <p>
				            As a subscriber, you'll enjoy exclusive access to:
				        </p>

				        <ul style="padding-left: 20px;">

				            <li style="margin-bottom: 10px;">
				                New collection launches
				            </li>

				            <li style="margin-bottom: 8px;">
				                Exclusive offers and subscriber-only discounts
				            </li>

				            <li style="margin-bottom: 8px;">
				                Home styling inspiration and décor ideas
				            </li>

				            <li style="margin-bottom: 8px;">
				                Seasonal trends and curated recommendations
				            </li>

				        </ul>

				        <p>
				            We're excited to be part of your journey in creating spaces filled with warmth, comfort, and timeless elegance.
				        </p>

				        <div style="
				            text-align: center;
				            margin: 30px 0;
				        ">

				            <a href="%s"
				               style="
				                   display: inline-block;
				                   background-color: #C5A47E;
				                   color: #ffffff;
				                   text-decoration: none;
				                   padding: 14px 28px;
				                   border-radius: 8px;
				                   font-weight: bold;
				               ">
				                Explore Our Collection
				            </a>

				        </div>

				        <p style="margin-top: 32px;">
				            Warm regards,
				        </p>

				        <p>

				            <strong style="
				                font-size: 16px;
				                color: #222222;
				            ">
				                Team Ojasvi Aura
				            </strong>

				            <br>

				            <span style="
				                display: inline-block;
				                margin-top: 6px;
				                color: #C5A47E;
				                font-style: italic;
				                font-size: 14px;
				                letter-spacing: 0.5px;
				            ">
				                Bringing Life into your Sanctuary.
				            </span>

				        </p>

				    </div>

				    <div style="
				        background-color: #faf7f3;
				        padding: 20px;
				        text-align: center;
				        color: #888888;
				        font-size: 12px;
				        border-top: 1px solid #f0f0f0;
				    ">

				        © 2026 Ojasvi Aura. All rights reserved.

				        <br><br>

				        You're receiving this email because you subscribed to updates from Ojasvi Aura.

				        <br><br>

				        <a href="%s"
				           style="
				               color: #AE7F4D;
				               text-decoration: none;
				           ">
				            Unsubscribe
				        </a>

				    </div>

				</div>
				"""
				.formatted(logoUrl, baseUrl, unsubscribeUrl);

		helper.setText(content, true);

		mailSender.send(mimeMessage);
	}

	public void removeSubscriber(Long id) {

		NewsletterSubscriber subscriber = subscriberRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("Subscriber not found"));

		subscriber.setIsActive(false);
		subscriber.setUnsubscribedAt(LocalDateTime.now());

		subscriberRepository.save(subscriber);
	}

	public void sendCampaign(String recipientType, String subject, String message) {

		List<NewsletterSubscriber> subscribers;

		if ("active".equalsIgnoreCase(recipientType)) {
			subscribers = subscriberRepository.findByIsActiveTrue();
		} else {
			subscribers = subscriberRepository.findAll();
		}

		for (NewsletterSubscriber subscriber : subscribers) {

			if (Boolean.FALSE.equals(subscriber.getIsActive())) {
				continue;
			}

			sendEmail(subscriber.getEmail(), subject, message);
		}

		NewsletterCampaign campaign = new NewsletterCampaign();

		campaign.setSubject(subject);
		campaign.setMessage(message);
		campaign.setRecipientType(recipientType);
		campaign.setRecipientCount(subscribers.size());
		campaign.setStatus("SENT");
		campaign.setSentAt(LocalDateTime.now());

		campaignRepository.save(campaign);
	}

	private void sendEmail(String to, String subject, String content) {

		MimeMessage mimeMessage = mailSender.createMimeMessage();

		try {

			MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

			helper.setTo(to);
			helper.setSubject(subject);

			helper.setText(content, true);

			mailSender.send(mimeMessage);

		} catch (Exception e) {
			throw new RuntimeException("Failed to send email: " + e.getMessage());
		}
	}

	public List<NewsletterSubscriber> getAllSubscribers() {

		return subscriberRepository.findAll(Sort.by(Sort.Direction.DESC, "subscribedAt"));
	}

	public List<NewsletterCampaign> getCampaigns() {

		return campaignRepository.findTop10ByOrderBySentAtDesc();
	}

	public long getTotalSubscribers() {

		return subscriberRepository.count();
	}

	public long getUnsubscribedCount() {

		return subscriberRepository.countByIsActiveFalse();
	}

	public long getCampaignsSent() {

		return campaignRepository.countByStatus("SENT");
	}

	public long getNewSubscribersThisMonth() {

		LocalDateTime start = LocalDate.now().withDayOfMonth(1).atStartOfDay();

		LocalDateTime end = LocalDateTime.now();

		return subscriberRepository.countBySubscribedAtBetween(start, end);
	}
}
