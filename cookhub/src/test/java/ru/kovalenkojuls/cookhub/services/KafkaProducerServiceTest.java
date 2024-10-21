package ru.kovalenkojuls.cookhub.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.dto.EmailDto;

import java.util.Map;
import java.util.UUID;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@SpringBootTest
class KafkaProducerServiceTest {

    @Autowired
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private KafkaTemplate<String, EmailDto> kafkaTemplate;

    @Test
    void testSendEmailEventToKafka() {
        User newUser = new User();
        newUser.setEmail("test@cookhub.ru");
        newUser.setActivationCode(UUID.randomUUID().toString());

        kafkaProducerService.sendEmailEventToKafka(newUser);

        EmailDto expectedEmailDto = new EmailDto(
                newUser.getEmail(),
                "Подтвердите регистрацию в CookHub",
                "activateEmail",
                Map.of("activateLink", String.format("http://localhost:8080/register/activate/%s", newUser.getActivationCode()))
        );

        verify(kafkaTemplate, times(1)).send("send-mail-event", expectedEmailDto);
    }
}

