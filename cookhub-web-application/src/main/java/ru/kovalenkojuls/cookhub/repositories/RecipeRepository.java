package ru.kovalenkojuls.cookhub.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;

public interface RecipeRepository extends CrudRepository<Recipe, Long> {

    Page<Recipe> findAll(Pageable pageable);

    Page<Recipe> findByCategory(RecipeCategory category, Pageable pageable);

    Page<Recipe> findByAuthor(User author, Pageable pageable);
}
