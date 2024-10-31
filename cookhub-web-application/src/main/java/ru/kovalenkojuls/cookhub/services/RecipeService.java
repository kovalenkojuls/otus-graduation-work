package ru.kovalenkojuls.cookhub.services;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.repositories.RecipeRepository;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Сервис для управления рецептами.
 *
 * Этот класс предоставляет функциональность для работы с рецептами.
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
     * @param pageable Объект Pageable для постраничной выборки.
     * @return Итератор рецептов по указанной категории.
     */
    public Iterable<Recipe> findRecipesByCategory(RecipeCategory category, User currentUser, Pageable pageable) {
        Iterable<Recipe> page = (category != null) ?
                recipeRepository.findByCategory(category, pageable) : recipeRepository.findAll(pageable);
        page.forEach(recipe -> {recipe.setMeLiked(currentUser);});
        return page;
    }

    /**
     * Возвращает список рецептов, принадлежащих пользователю с заданным ID.
     *
     * @param author автор рецепта.
     * @param pageable Объект Pageable для постраничной выборки.
     * @return Список рецептов.
     */
    public Iterable<Recipe> findRecipesByAuthor(User author, User currentUser, Pageable pageable) {
        Iterable<Recipe> page = recipeRepository.findByAuthor(author, pageable);
        page.forEach(recipe -> {recipe.setMeLiked(currentUser);});
        return page;
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

    /**
     * Добавляет или удаляет лайк рецепта для авторизованного пользователя.
     *
     * @param recipe Рецепт, который нужно лайкнуть или удалить лайк.
     * @param currentUser Текущий авторизованный пользователь.
     */
    public void like(Recipe recipe, User currentUser) {
        Set<User> likes = recipe.getLikes();
        if (likes.contains(currentUser)) {
            likes.remove(currentUser);
        } else {
            likes.add(currentUser);
        }

        recipeRepository.save(recipe);
    }
}
