package com.ojasvi.ecommerce.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.ojasvi.ecommerce.Entity.User;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromMail;

    public void sendCustomerWelcomeEmail(User customer) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromMail);

        message.setTo(customer.getEmail());

        message.setSubject("Welcome to Ojasvi - Your Account Has Been Created");

        message.setText(
                "Dear " + customer.getFullName() + ",\n\n"

                + "Welcome to Ojasvi!\n\n"

                + "Your account has been created successfully.\n\n"

                + "You can now:\n"

                + "• Browse premium home linen\n"

                + "• Add products to wishlist\n"

                + "• Place orders\n"

                + "• Track deliveries\n\n"

                + "Thank you for choosing Ojasvi.\n\n"

                + "Regards,\n"

                + "Team Ojasvi"
        );

        mailSender.send(message);
    }

    public void sendAdminNewCustomerEmail(String adminEmail,
                                          User customer) {

        SimpleMailMessage message = new SimpleMailMessage();

        message.setFrom(fromMail);

        message.setTo(adminEmail);

        message.setSubject("New Customer Registration");

        message.setText(

                "Hello Admin,\n\n"

                + "A new customer has registered.\n\n"

                + "Customer Details\n\n"

                + "Name : " + customer.getFullName() + "\n"

                + "Email : " + customer.getEmail() + "\n"

                + "Mobile : " + customer.getMobile() + "\n\n"

                + "Please login to Admin Panel.\n\n"

                + "Regards,\n"

                + "Ojasvi System"

        );

        mailSender.send(message);

    }

}
