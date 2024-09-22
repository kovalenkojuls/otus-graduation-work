package ru.kovalenkojuls.cookhub.domains;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.kovalenkojuls.cookhub.enums.RecipeCategory;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "recipe")
public class Recipe {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    @Column(name = "id")
    private Integer id;

    @Column(name = "text")
    private String text;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    private RecipeCategory category;

    public Recipe(String text, RecipeCategory category) {
        this.text = text;
        this.category = category;
    }
}
