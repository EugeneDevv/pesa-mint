package com.eugenedevv.pesamint.service.impl;

/**
 * Created by Eugene Devv on 22 Oct, 2023
 */

import com.eugenedevv.pesamint.dto.EmailDetails;
import com.eugenedevv.pesamint.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender javaEmailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;
    @Override
    public void sendEmailAlert(EmailDetails emailDetails) {

        try{
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom(senderEmail);
            mailMessage.setTo(emailDetails.getRecipient());
            mailMessage.setText(emailDetails.getMessageBody());
            mailMessage.setSubject(emailDetails.getSubject());

            javaEmailSender.send(mailMessage);
            System.out.println("Mail sent successfully");
        } catch (MailException e){
            throw new RuntimeException(e);
        }
    }
}
