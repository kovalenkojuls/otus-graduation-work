package ru.kovalenkojuls.cookhub.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.repositories.RecipeRepository;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
class RecipeServiceTest {

    @Autowired
    private RecipeService recipeService;

    @MockBean
    private RecipeRepository recipeRepository;

    @Test
    void testFindById() {
        Long recipeId = 1L;
        Recipe recipe = new Recipe();
        recipe.setId(recipeId);
        when(recipeRepository.findById(recipeId)).thenReturn(Optional.of(recipe));

        Optional<Recipe> result = recipeService.findById(recipeId);

        assertTrue(result.isPresent());
        assertEquals(recipeId, result.get().getId());
    }

    @Test
    void testGetRecipesByCategory() {
        RecipeCategory category = RecipeCategory.SALADS;
        List<Recipe> recipes = Collections.singletonList(new Recipe());
        when(recipeRepository.findByCategory(category)).thenReturn(recipes);

        Iterable<Recipe> result = recipeService.getRecipesByCategory(category);
        assertEquals(recipes, result);
    }

    @Test
    void testSaveRecipe() throws IOException {
        Recipe recipe = new Recipe();
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> {
            Recipe saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        recipeService.save(recipe);

        verify(recipeRepository, times(1)).save(recipe);
        assertNotNull(recipe.getId());
    }

    @Test
    void testSave_RecipeWithFile() throws IOException {
        Recipe recipe = new Recipe();
        MultipartFile file = new MockMultipartFile("test.jpg", "test.jpg", "image/jpeg", "testdata".getBytes());
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> {
            Recipe saved = i.getArgument(0);
            saved.setId(1L);
            return saved;
        });

        recipeService.save(recipe, file);

        verify(recipeRepository, times(1)).save(recipe);
        assertNotNull(recipe.getId());
        assertNotNull(recipe.getFilename());
    }

    @Test
    void testUpdate() throws IOException {
        Recipe recipe = new Recipe();
        recipe.setId(1L);
        String newText = "newtext";
        RecipeCategory newCategory = RecipeCategory.SOUPS;
        MultipartFile newFile = new MockMultipartFile("new.jpg", "new.jpg", "image/jpeg", "new data".getBytes());
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(i -> i.getArgument(0));

        recipeService.update(newText, newCategory, newFile, recipe);

        assertEquals(newText, recipe.getText());
        assertEquals(newCategory, recipe.getCategory());
        assertNotNull(recipe.getFilename());
        verify(recipeRepository, times(1)).save(recipe);
    }
}
