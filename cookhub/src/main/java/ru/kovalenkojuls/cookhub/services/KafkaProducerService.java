package ru.kovalenkojuls.cookhub.services;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.dto.EmailDto;

import java.util.Map;

/**
 * Сервис для отправки сообщений в Kafka.
 *
 * Этот класс предоставляет функциональность для отправки объектов типа {@link EmailDto} в указанные темы Kafka.
 */
@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    @Value("${cookhub.domain}")
    private String appDomain;

    @Value("${cookhub.port}")
    private String appPort;

    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    /**
     * Отправляет событие в Kafka для подтверждения регистрации пользователя.
     *
     * @param newUser Новый пользователь, которому будет отправлено подтверждение регистрации.
     */
    public void sendEmailEventToKafka(User newUser) {
        EmailDto emailDTO = new EmailDto(
                newUser.getEmail(),
                "Подтвердите регистрацию в CookHub",
                "activateEmail",
                Map.of("activateLink", String.format("%s:%s/activate/%s", appDomain, appPort, newUser.getActivationCode()))
        );
        kafkaTemplate.send("send-mail-event", emailDTO);
    }
}
