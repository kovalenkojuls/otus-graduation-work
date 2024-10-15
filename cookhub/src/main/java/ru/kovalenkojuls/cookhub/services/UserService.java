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

/**
 * Сервис для управления пользователями.
 *
 * Этот класс отвечает за операции с пользователями, включая их регистрацию,
 * обновление, активацию и работу с текущими пользователями.
 */
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

    /**
     * Получить список всех пользователей.
     *
     * @return Список пользователей.
     */
    public List<User> findAll() {
        return userRepository.findAll();
    }

    /**
     * Найти пользователя по его идентификатору.
     *
     * @param id Идентификатор пользователя.
     * @return {@link Optional} пользователь с указанным идентификатором, если найден, иначе {@link Optional#empty()}.
     */
    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    /**
     * Найти пользователя по его имени.
     *
     * @param username Имя пользователя.
     * @return Пользователь, если найден.
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    /**
     * Зарегистрировать нового пользователя.
     *
     * @param newUser Новый пользователь для регистрации.
     */
    public void registerUser(User newUser) {
        User newUserUpdated = setFieldsForNewUser(newUser);
        User savedUser = userRepository.save(newUserUpdated);
        log.info("Пользователь с id={} зарегистрирован", savedUser.getId());

        if (!savedUser.getEmail().isEmpty()) {
            kafkaProducerService.sendEmailEventToKafka(newUser);
        }
    }

    /**
     * Получить текущего авторизованного пользователя.
     *
     * @return Текущий пользователь или null, если не авторизован.
     */
    public User getCurrentUser() {
        String username = extractUsernameFromSecurityContext();
        return (username != null) ? findByUsername(username) : null;
    }

    /**
     * Обновить информацию о пользователе.
     *
     * @param user    Пользователь для обновления.
     * @param username Новое имя пользователя.
     * @param email   Новый адрес электронной почты.
     * @param form    мапа с ролями пользователя.
     */
    public void updateUser(User user, String username, String email, Map<String, String> form) {
        user.setUsername(username);
        user.setEmail(email);
        updateRoles(user, form);
        User updatedUser = userRepository.save(user);

        log.info("Пользователь с id={} обновлён", updatedUser.getId());
    }

    /**
     * Активировать пользователя по коду активации.
     *
     * @param activationCode Код активации.
     * @return true, если пользователь активирован успешно; false в противном случае.
     */
    public boolean activateUser(String activationCode) {
        User user = userRepository.findByActivationCode(activationCode);
        if (user == null) {
            return false;
        }

        user.setActivationCode(null);
        userRepository.save(user);
        return true;
    }

    /**
     * Получить авторизованного пользователя из контекста безопасности.
     *
     * @param userDetails Данные пользователя для извлечения.
     * @return Пользователь, если найден.
     */
    public User getAuthorizedUser(UserDetails userDetails) {
        return findByUsername(userDetails.getUsername());
    }

    /**
     * Обновить роли пользователя согласно предоставленным данным.
     *
     * @param user Пользователь, роли которого нужно обновить.
     * @param form Карта с ролями пользователя.
     */
    private void updateRoles(User user, Map<String, String> form) {
        user.getRoles().clear();
        user.getRoles().addAll(
                Arrays.stream(UserRole.values())
                        .filter(role -> form.containsKey(role.name()))
                        .toList()
        );
    }

    /**
     * Извлечь имя пользователя из контекста безопасности.
     *
     * @return Имя пользователя, если найдено; null в противном случае.
     */
    private String extractUsernameFromSecurityContext() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UserDetails) {
            return ((UserDetails) principal).getUsername();
        }
        return null;
    }

    /**
     * Установить необходимые поля для нового пользователя.
     *
     * @param newUser Новый пользователь.
     * @return Обновленный экземпляр пользователя.
     */
    private User setFieldsForNewUser(User newUser) {
        newUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        newUser.setActivationCode(UUID.randomUUID().toString());
        newUser.setRoles(Collections.singleton(UserRole.USER));
        newUser.setActive(true);
        return newUser;
    }
}
