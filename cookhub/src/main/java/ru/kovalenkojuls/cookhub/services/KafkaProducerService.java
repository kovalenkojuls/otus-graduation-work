package ru.kovalenkojuls.cookhub.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.dto.EmailDto;

@Service
@AllArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, EmailDto> kafkaTemplate;

    public void sendMessage(String topic, EmailDto emailDTO) {
        kafkaTemplate.send(topic, emailDTO);
    }
}