package ru.kovalenkojuls.cookhub.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.dto.EmailDto;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.UserRole;
import ru.kovalenkojuls.cookhub.repositories.UserRepository;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final KafkaProducerService kafkaProducerService;

    @Value("${cookhub.domain}")
    private String appDomain;

    @Value("${cookhub.port}")
    private String appPort;

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void registerUser(User newUser) {
        User newUserUpdated = setFieldsForNewUser(newUser);
        User savedUser = userRepository.save(newUserUpdated);
        log.info("Пользователь с id={} зарегистрирован", savedUser.getId());

        if (!savedUser.getEmail().isEmpty()) {
            sendEmailEventToKafka(newUser);
        }
    }

    public User getCurrentUser() {
        String username = extractUsernameFromSecurityContext();
        return (username != null) ? findByUsername(username) : null;
    }

    public void updateUser(User user, String username, String email, Map<String, String> form) {
        user.setUsername(username);
        user.setEmail(email);
        updateRoles(user, form);
        User updatedUser = userRepository.save(user);

        log.info("Пользователь с id={} обновлён", updatedUser.getId());
    }

    public boolean activateUser(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    private void sendEmailEventToKafka(User newUser) {
        EmailDto emailDTO = new EmailDto(
                newUser.getEmail(),
                "Подтвердите регистрацию в CookHub",
                "activateEmail",
                Map.of("activateLink", String.format("%s:%s/activate/%s", appDomain, appPort, newUser.getActivationCode()))
        );
        kafkaProducerService.sendMessage("send-mail-event", emailDTO);
    }

    private void updateRoles(User user, Map<String, String> form) {
        user.getRoles().clear();
        user.getRoles().addAll(
                Arrays.stream(UserRole.values())
                        .filter(role -> form.containsKey(role.name()))
                        .toList()
        );
    }

    private String extractUsernameFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    private User setFieldsForNewUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setActivationCode(UUID.randomUUID().toString());
        newUser.setRoles(Collections.singleton(UserRole.USER));
        newUser.setActive(true);
        return newUser;
    }
}
