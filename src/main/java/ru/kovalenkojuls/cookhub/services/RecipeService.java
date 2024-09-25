package ru.kovalenkojuls.cookhub.services;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.repositories.RecipeRepository;

@Service
@AllArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;

    public Iterable<Recipe> getRecipesByCategory(RecipeCategory category) {
        if (category != null) {
            return recipeRepository.findByCategory(category);
        } else {
            return recipeRepository.findAll();
        }
    }

    public void save(Recipe recipe) {
        recipeRepository.save(recipe);
    }
}
