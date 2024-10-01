package ru.kovalenkojuls.mailsender.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.mailsender.domains.dto.EmailDTO;

@Service
@AllArgsConstructor
public class EmailListener {
    private final CookhubMailSender mailSender;

    @KafkaListener(
            topics = "send-mail-event",
            groupId = "email-consumer-group",
            properties = {"spring.json.value.default.type=ru.kovalenkojuls.mailsender.domains.dto.EmailDTO"},
            concurrency = "5"
    )
    public void listen(EmailDTO emailDTO) {
        System.out.println("Received Email DTO: " + emailDTO);
        sendEmail(emailDTO);
    }

    private void sendEmail(EmailDTO emailDTO) {
        mailSender.send(
                emailDTO.getEmailTo(),
                emailDTO.getSubject(),
                emailDTO.getTemplateName(),
                emailDTO.getTemplateVariables()
        );
    }
}