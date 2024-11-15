package ru.kovalenkojuls.cookhub.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.repositories.UserRepository;

import java.util.stream.Collectors;

/**
 * Сервис для управления аутентификацией пользователя.
 *
 * Этот класс реализует интерфейс {@link UserDetailsService} и предоставляет
 * функциональность для загрузки информации о пользователе по имени.
 */
@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Загружает пользователя по его имени пользователя.
     *
     * @param username Имя пользователя, по которому будет выполнен поиск.
     * @return {@link UserDetails} объект, содержащий информацию о пользователе и его ролях.
     * @throws UsernameNotFoundException Если пользователь с указанным именем не найден.
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found");
        }

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.name()))
                        .collect(Collectors.toList())
        );
    }
}
