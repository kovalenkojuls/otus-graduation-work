package ru.kovalenkojuls.cookhub.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.repositories.RecipeRepository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис для управления рецептами.
 *
 * Этот класс предоставляет функциональность для работы с рецептами,
 * включая их сохранение и получение по идентификатору и категории.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Найти рецепт по его идентификатору.
     *
     * @param id Идентификатор рецепта.
     * @return {@link Optional} рецепт с указанным идентификатором, если найден, иначе {@link Optional#empty()}.
     */
    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }

    /**
     * Получить рецепты по категории.
     *
     * @param category Категория рецептов. Если {@code null}, возвращаются все рецепты.
     * @return Итератор рецептов по указанной категории.
     */
    public Iterable<Recipe> getRecipesByCategory(RecipeCategory category) {
        return (category != null) ? recipeRepository.findByCategory(category) : recipeRepository.findAll();
    }

    /**
     * Сохранить рецепт.
     *
     * @param recipe Рецепт для сохранения.
     * @throws IOException Если произошла ошибка при сохранении.
     */
    public void save(Recipe recipe) throws IOException {
        Recipe savedRecipe = recipeRepository.save(recipe);
        log.info("Рецепт с id={} сохранён", savedRecipe.getId());
    }

    /**
     * Сохранить рецепт вместе с файлом изображения.
     *
     * @param recipe Рецепт для сохранения.
     * @param file   Изображение рецепта в формате {@link MultipartFile}.
     * @throws IOException Если произошла ошибка при сохранении.
     */
    public void save(Recipe recipe, MultipartFile file) throws IOException {
        String resultFileName = saveFile(file);
        recipe.setFilename(resultFileName);
        Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("Рецепт с id={} сохранён", savedRecipe.getId());
    }

    /**
     * Сохранить файл на диск.
     *
     * @param file Файл для сохранения, в формате {@link MultipartFile}.
     * @return Название сохраненного файла.
     * @throws IOException Если произошла ошибка при сохранении файла.
     */
    public String saveFile(MultipartFile file) throws IOException {
        String resultFileName = null;
        if (file != null && !file.getOriginalFilename().isEmpty()) {
            File uploadFolder = new File(uploadPath);
            if (!uploadFolder.exists()) {
                uploadFolder.mkdir();
            }

            resultFileName = UUID.randomUUID().toString() + "." + file.getOriginalFilename();
            file.transferTo(new File(uploadPath + "/" + resultFileName));

            log.info("Файл {} сохранён", uploadPath + "/" + resultFileName);
        }

        return resultFileName;
    }

    public void update(String text, RecipeCategory category, MultipartFile file, Recipe recipe) throws IOException {
        recipe.setText(text);
        recipe.setCategory(category);
        if (file != null && !file.isEmpty()) {
            String filename = saveFile(file);
            recipe.setFilename(filename);
        }
        save(recipe);
    }
}
