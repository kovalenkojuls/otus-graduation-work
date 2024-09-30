package ru.kovalenkojuls.cookhub.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CookhubMailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void send(String emailTo, String subject, String templateName, Map<String, Object> templateVariables) {
        try {
            MimeMessage mailMessage = mailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mailMessage, true);

            mimeMessageHelper.setFrom(emailFrom);
            mimeMessageHelper.setTo(emailTo);
            mimeMessageHelper.setSubject(subject);

            Context context = new Context();
            context.setVariables(templateVariables);

            String htmlContent = templateEngine.process(templateName, context);
            mimeMessageHelper.setText(htmlContent, true);

            mailSender.send(mailMessage);
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
}
