package ru.kovalenkojuls.emailservice.services;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.kovalenkojuls.emailservice.exceptions.EmailException;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class CookhubMailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String emailFrom;

    public void send(String emailTo, String subject, String templateName, Map<String, Object> templateVariables) {
        MimeMessage mailMessage = createMimeMessage(emailTo, subject, templateName, templateVariables);
        mailSender.send(mailMessage);
    }

    private MimeMessage createMimeMessage(String emailTo, String subject, String templateName, Map<String, Object> templateVariables) {
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

            return mailMessage;
        } catch (MessagingException e) {
            log.error("Ошибка при создании сообщения: {}", e.getMessage(), e);
            throw new EmailException("Ошибка при создании письма", e);
        }
    }
}