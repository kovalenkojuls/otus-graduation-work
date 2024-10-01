package ru.kovalenkojuls.cookhub.services;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.dto.EmailDTO;

@Service
@AllArgsConstructor
public class KafkaProducerService {
    private final KafkaTemplate<String, EmailDTO> kafkaTemplate;

    public void sendMessage(String topic, EmailDTO emailDTO) {
        kafkaTemplate.send(topic, emailDTO);
    }
}