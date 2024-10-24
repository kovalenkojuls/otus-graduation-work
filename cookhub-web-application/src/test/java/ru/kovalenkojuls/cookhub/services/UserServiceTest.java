package ru.kovalenkojuls.cookhub.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.UserRole;
import ru.kovalenkojuls.cookhub.repositories.UserRepository;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@SpringBootTest
public class UserServiceTest {
    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private KafkaProducerService kafkaProducerService;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @Test
    void testFindAll() {
        List<User> expectedUsers = List.of(new User(), new User());
        when(userRepository.findAll()).thenReturn(expectedUsers);
        List<User> actualUsers = userService.findAll();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void testFindById() {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));

        Optional<User> foundUser = userService.findById(userId);

        assertTrue(foundUser.isPresent(), "Ожидается, что пользователь будет найден");
        assertEquals(existingUser, foundUser.get(), "Проверка, что найден нужный пользователь");
    }

    @Test
    void testFindByUsername() {
        String username = "testuser";
        User existingUser = new User();
        existingUser.setUsername(username);
        when(userRepository.findByUsername(username)).thenReturn(existingUser);

        User foundUser = userService.findByUsername(username);

        assertEquals(existingUser, foundUser, "Ожидается, что найденный пользователь совпадает с ожидаемым");
    }

    @Test
    void testRegisterUser() {
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setPassword("password");
        newUser.setEmail("test@cookhub.ru");

        User savedUser = new User();
        savedUser.setId(1L);
        savedUser.setUsername("testuser");
        savedUser.setPassword("encodedpassword");
        savedUser.setEmail("test@cookhub.ru");
        savedUser.setActive(true);
        savedUser.setActivationCode(UUID.randomUUID().toString());
        savedUser.setRoles(Collections.singleton(UserRole.USER));

        when(passwordEncoder.encode(anyString())).thenReturn("encodedpassword");
        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        userService.registerUser(newUser);

        verify(passwordEncoder, times(1)).encode("password");
        verify(userRepository, times(1)).save(any(User.class));
        verify(kafkaProducerService, times(1)).sendEmailEventToKafka(newUser);

        assertEquals("encodedpassword", newUser.getPassword());
        assertTrue(newUser.isActive());
        assertNotNull(newUser.getActivationCode());
        assertEquals(Collections.singleton(UserRole.USER), newUser.getRoles());
    }

    @Test
    void testUpdateUser() {
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setUsername("oldusername");
        existingUser.setEmail("old@cookhub.ru");
        existingUser.setRoles(new HashSet<>(Set.of(UserRole.USER)));

        String newUsername = "newusername";
        String newEmail = "new@cookhub.ru";
        Map<String, String> form = new HashMap<>();
        form.put("USER", "1");
        form.put("ADMIN", "1");

        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        userService.updateUser(existingUser, newUsername, newEmail, form);

        assertEquals(newUsername, existingUser.getUsername());
        assertEquals(newEmail, existingUser.getEmail());
        assertEquals(Set.of(UserRole.USER, UserRole.ADMIN), existingUser.getRoles());
        verify(userRepository, times(1)).save(existingUser);
    }

    @Test
    void testActivateUser() {
        String activationCode = "activationCode";
        User user = new User();
        user.setId(1L);
        user.setActivationCode(activationCode);

        when(userRepository.findByActivationCode(activationCode)).thenReturn(user);
        when(userRepository.save(user)).thenReturn(user);

        boolean result = userService.activateUser(activationCode);

        assertTrue(result);
        assertNull(user.getActivationCode());
        verify(userRepository, times(1)).save(user);
    }
}
