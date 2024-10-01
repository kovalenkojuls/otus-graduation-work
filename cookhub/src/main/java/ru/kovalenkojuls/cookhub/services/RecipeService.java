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
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecipeService {
    private final RecipeRepository recipeRepository;

    @Value("${upload.path}")
    private String uploadPath;

    public Iterable<Recipe> getRecipesByCategory(RecipeCategory category) {
        return (category != null) ? recipeRepository.findByCategory(category) : recipeRepository.findAll();
    }

    public void save(Recipe recipe, MultipartFile file) throws IOException {
        String resultFileName = saveFile(file);
        recipe.setFilename(resultFileName);
        Recipe savedRecipe = recipeRepository.save(recipe);

        log.info("Рецепт с id={} сохранён", savedRecipe.getId());
    }

    private String saveFile(MultipartFile file) throws IOException {
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
}
