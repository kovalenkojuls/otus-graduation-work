package ru.kovalenkojuls.mailsender.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.mailsender.domains.dto.EmailDto;

@Service
@AllArgsConstructor
public class EmailListener {
    private final CookhubMailSender mailSender;

    @KafkaListener(
            topics = "send-mail-event",
            groupId = "email-consumer-group",
            properties = {"spring.json.value.default.type=ru.kovalenkojuls.mailsender.domains.dto.EmailDto"},
            concurrency = "5"
    )
    public void listen(EmailDto emailDTO) {
        System.out.println("Received Email DTO: " + emailDTO);
        sendEmail(emailDTO);
    }

    private void sendEmail(EmailDto emailDTO) {
        mailSender.send(
                emailDTO.getEmailTo(),
                emailDTO.getSubject(),
                emailDTO.getTemplateName(),
                emailDTO.getTemplateVariables()
        );
    }
}