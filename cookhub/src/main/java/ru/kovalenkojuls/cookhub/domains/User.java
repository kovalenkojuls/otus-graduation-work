package ru.kovalenkojuls.cookhub.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;
import ru.kovalenkojuls.cookhub.domains.enums.UserRole;

import java.util.Set;

@Entity
@Table(name = "cookhub_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Пароль не может быть пустым")
    private String password;

    @Column(name = "active")
    private boolean active;

    @Column(name = "email")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некоррекный формат")
    private String email;

    @Column(name = "activationCode")
    private String activationCode;

    @Column(name = "role")
    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "cookhub_user_role", joinColumns = @JoinColumn(name = "cookhub_user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;
}
