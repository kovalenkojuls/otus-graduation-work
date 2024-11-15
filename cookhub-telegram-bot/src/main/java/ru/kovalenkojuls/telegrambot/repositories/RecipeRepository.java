package ru.kovalenkojuls.telegrambot.repositories;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import ru.kovalenkojuls.telegrambot.domains.Recipe;
import ru.kovalenkojuls.telegrambot.domains.enums.RecipeCategory;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {
    @Query("select r from Recipe r where category = :category order by random() limit 1")
    Recipe findRandomRecipeByCategory(@Param("category") RecipeCategory category);
}
