package ru.kovalenkojuls.telegrambot.domains;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

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
    private String username;

    @Column(name = "password")
    private String password;

    @Column(name = "active")
    private boolean active;

    @Column(name = "email")
    private String email;

    @Column(name = "activationCode")
    private String activationCode;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Recipe> recipes;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cookhub_user_follower",
            joinColumns = { @JoinColumn(name = "following_id") },
            inverseJoinColumns = { @JoinColumn(name = "follower_id") }
    )
    private Set<User> followers = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "cookhub_user_follower",
            joinColumns = { @JoinColumn(name = "follower_id") },
            inverseJoinColumns = { @JoinColumn(name = "following_id") }
    )
    private Set<User> followings = new HashSet<>();

}