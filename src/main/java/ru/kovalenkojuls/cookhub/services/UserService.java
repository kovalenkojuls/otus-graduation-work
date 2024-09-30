package ru.kovalenkojuls.cookhub.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
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
    private final CookhubMailSender mailSender;

    @Value("${cookhub.domain}")
    private String appDomain;

    @Value("${cookhub.port}")
    private String appPort;

    public void registerUser(String username, String password, String email) {
        User newUser = new User();

        newUser.setUsername(username);
        newUser.setPassword(passwordEncoder.encode(password));
        newUser.setEmail(email);
        newUser.setActivationCode(UUID.randomUUID().toString());
        newUser.setRoles(Collections.singleton(UserRole.USER));
        newUser.setActive(true);

        User savedUser = userRepository.save(newUser);
        log.info("Пользователь с id={} зарегистрирован", savedUser.getId());

        if (!newUser.getEmail().isEmpty()) {
            mailSender.send(
                    newUser.getEmail(),
                    "Подтвердите регистрацию в CookHub",
                    "activateEmail",
                    Map.of("activateLink", String.format("%s:%s/activate/%s", appDomain, appPort, newUser.getActivationCode()))
            );
        }
    }

    public User getCurrentUser() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            String username = ((UserDetails) principal).getUsername();
            return findByUsername(username);
        } else {
            return null;
        }
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public void updateUser(User user, String username, Map<String, String> form, String email) {
        user.setUsername(username);
        user.setEmail(email);
        user.getRoles().clear();
        user.getRoles().addAll(
                Arrays.stream(UserRole.values())
                        .filter(role -> form.containsKey(role.name()))
                        .toList()
        );
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
}
