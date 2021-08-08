package com.example.gettour_api.services;

import com.example.gettour_api.services.interfaces.EmailService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final static Logger LOGGER = LoggerFactory.getLogger(EmailServiceImpl.class);
    private final JavaMailSender mailSender;

    /**
     This method is used to send mail to users to confirm their email or reset the password
     */

    @Override
    @Async
    public void send(String to, String email, String status) {
        try{
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(email, true);
            helper.setTo(to);
            if(status.equals("email")) helper.setSubject("Confirm your email");
            if(status.equals("password")) helper.setSubject("Reset your password");
            helper.setFrom("gettourbot@gmail.com");
            mailSender.send(mimeMessage);
        }catch (MessagingException e){
            LOGGER.error("Failed to send an email", e);
            throw new IllegalStateException("Failed to send an email");
        }
    }
}