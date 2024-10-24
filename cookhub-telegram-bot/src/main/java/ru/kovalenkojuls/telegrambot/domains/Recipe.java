package ru.kovalenkojuls.telegrambot.domains;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kovalenkojuls.telegrambot.domains.enums.RecipeCategory;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "text", columnDefinition = "TEXT")
    private String text;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private RecipeCategory category;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "filename")
    private String filename;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    public Recipe(String text, RecipeCategory category, User author) {
        this.text = text;
        this.category = category;
        this.author = author;
    }
}
