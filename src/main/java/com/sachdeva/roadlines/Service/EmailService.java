/*
package com.sachdeva.roadlines.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

	private final JavaMailSender javaMailSender;

	@Value("${spring.mail.properties.mail.smtp.from}")
	private String fromEmail;

	public void sendWelcomeEmail(String toEmail, String name) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Welcome to Our Platform");
		message.setText(
				"Hello " + name + ",\n\nThanks for registering with us!\n\nRegards, \nSparrow Development World! ");

		javaMailSender.send(message);

	}

	// to send a reset OTP to email
	public void sendResetOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Password Reset OTP");
		message.setText("Your OTP for resetting your password is " + otp
				+ ". \nUse this OTP to proceed with reseting your password.");

		javaMailSender.send(message);
	}

	// sendOTP to email for (account verification)
	public void sendOtpEmail(String toEmail, String otp) {
		SimpleMailMessage message = new SimpleMailMessage();

		message.setFrom(fromEmail);
		message.setTo(toEmail);
		message.setSubject("Account Verification OTP");
		message.setText("Your OTP is " + otp + ". Verify your account using this OTP.");

		javaMailSender.send(message);

	}
}

*/
// Directly using BRAVO API service for avoid This will solve the SMTP timeout issue permanently.

package com.sachdeva.roadlines.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    @Value("${brevo.api.key}")
    private String apiKey;

    @Value("${brevo.from.email}")
    private String fromEmail;

    @Value("${brevo.from.name}")
    private String fromName;

    private final RestTemplate restTemplate = new RestTemplate();

    // Internal method to send email via Brevo API
    private void sendEmail(String toEmail, String subject, String text) {
        String url = "https://api.brevo.com/v3/smtp/email";

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of("name", fromName, "email", fromEmail));
        payload.put("to", List.of(Map.of("email", toEmail)));
        payload.put("subject", subject);
        payload.put("textContent", text);

        HttpHeaders headers = new HttpHeaders();
        headers.set("api-key", apiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(payload, headers);

        try {
            restTemplate.postForEntity(url, request, String.class);
        } catch (Exception ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to send email via Brevo API: " + ex.getMessage());
        }
    }

    // Send Welcome Email
    public void sendWelcomeEmail(String toEmail, String name) {
        String subject = "Welcome to Our Platform";
        String text = "Hello " + name + ",\n\nThanks for registering with us!\n\nRegards, Sparrow Development World!";
        sendEmail(toEmail, subject, text);
    }

    // Send Reset OTP Email
    public void sendResetOtpEmail(String toEmail, String otp) {
        String subject = "Password Reset OTP";
        String text = "Your OTP for resetting your password is " + otp + ". Use this OTP to reset your password.";
        sendEmail(toEmail, subject, text);
    }

    // Send Account Verification OTP
    public void sendOtpEmail(String toEmail, String otp) {
        String subject = "Account Verification OTP";
        String text = "Your OTP is " + otp + ". Verify your account using this OTP.";
        sendEmail(toEmail, subject, text);
    }
}
