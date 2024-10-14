package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.services.RecipeService;
import ru.kovalenkojuls.cookhub.services.UserService;

import java.io.IOException;

@Controller
@AllArgsConstructor
public class RecipeController {
    private final RecipeService recipeService;
    private final UserService userService;

    @GetMapping("/edit-recipe/{recipe}")
    public String updateRecipe(
            @PathVariable Recipe recipe,
            Model model
    ) {
        model.addAttribute("category", recipe.getCategory().getDisplayName());
        model.addAttribute("recipe", recipe);
        return "editRecipe";
    }

    @PostMapping("/edit-recipe")
    public String updateRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam Long recipeId,
            @RequestParam String text,
            @RequestParam RecipeCategory category,
            @RequestParam(name = "file", required = false) MultipartFile file,
            Model model
    ) throws IOException {

        Recipe recipe = recipeService.findById(recipeId).orElseThrow();
        User currentUser = userService.getAuthorizedUser(userDetails);

        model.addAttribute("currentUserId", currentUser.getId());
        model.addAttribute("recipes", currentUser.getRecipes());
        model.addAttribute("username", currentUser.getUsername());

        if (!currentUser.equals(recipe.getAuthor())) {
            return "recipesListByUser";
        }

        recipe.setText(text);
        recipe.setCategory(category);
        if (file != null && !file.isEmpty()) {
            String filename = recipeService.saveFile(file);
            recipe.setFilename(filename);
        }
        recipeService.save(recipe);

        return "recipesListByUser";
    }

    @GetMapping("/user-recipes/{userId}")
    public String getUserRecipes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId,
            Model model
    ) {
        User user = userService.findById(userId).orElseThrow();
        Long currentUserId = userService.getAuthorizedUser(userDetails).getId();
        model.addAttribute("currentUserId", currentUserId);
        model.addAttribute("recipes", user.getRecipes());
        model.addAttribute("username", user.getUsername());
        return "recipesListByUser";
    }
}
