package ru.kovalenkojuls.cookhub.domains;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.kovalenkojuls.cookhub.domains.enums.UserRole;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "cookhub_user")
@Getter
@Setter
@EqualsAndHashCode(of = "id")
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    @NotBlank(message = "Имя пользователя не может быть пустым")
    @Size(min = 4, max = 20, message = "Имя пользователя должно быть от 4 до 20 символов")
    private String username;

    @Column(name = "password")
    @NotBlank(message = "Пароль не может быть пустым")
    @Size(min = 8, message = "Пароль должен быть не менее 8 символов")
    @Pattern(regexp = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[!@#$%^&*()_+-=]).*$",
            message = "Пароль должен содержать хотя бы одну цифру, одну строчную букву, одну заглавную латинские буквы и один спецсимвол")
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

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recipe> recipes;

    @ManyToMany
    @JoinTable(
            name = "cookhub_user_follower",
            joinColumns = { @JoinColumn(name = "following_id") },
            inverseJoinColumns = { @JoinColumn(name = "follower_id") }
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "cookhub_user_follower",
            joinColumns = { @JoinColumn(name = "follower_id") },
            inverseJoinColumns = { @JoinColumn(name = "following_id") }
    )
    private Set<User> followings = new HashSet<>();

}
