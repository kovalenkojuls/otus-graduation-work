package ru.kovalenkojuls.cookhub.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Table(name = "cookhub_user")
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @ElementCollection(targetClass = UserRole.class, fetch = FetchType.EAGER)
    @CollectionTable(name = "cookhub_user_role", joinColumns = @JoinColumn(name = "cookhub_user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roles;
}
