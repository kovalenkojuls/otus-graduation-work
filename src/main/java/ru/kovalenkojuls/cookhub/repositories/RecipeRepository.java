package ru.kovalenkojuls.cookhub.repositories;

import org.springframework.data.repository.CrudRepository;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.enums.RecipeCategory;

import java.util.List;

public interface RecipeRepository extends CrudRepository<Recipe, Integer> {
    List<Recipe> findByCategory(RecipeCategory category);
}
