package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.services.RecipeService;
import ru.kovalenkojuls.cookhub.services.UserService;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
@RequestMapping("/recipes")
@AllArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final UserService userService;
    private final Validator validator;

    @GetMapping
    public String getRecipesList(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) RecipeCategory category, Model model
    ) {

        model.addAttribute("currentUserId", userService.getAuthorizedUser(userDetails).getId());
        model.addAttribute("recipes", recipeService.getRecipesByCategory(category));
        return "recipesListAll";
    }

    @PostMapping
    public String saveRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            Recipe recipe,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult,
            Model model) throws IOException {

        recipe.setAuthor(userService.getAuthorizedUser(userDetails));
        recipe.setCreatedAt(LocalDateTime.now());

        validator.validate(recipe, bindingResult);
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getMapErrors(bindingResult));
            if (recipe.getCategory() != null) {
                model.addAttribute("category", recipe.getCategory().getDisplayName());
            }
        } else {
            recipeService.save(recipe, file);
        }

        model.addAttribute("recipes", recipeService.getRecipesByCategory(null));
        return "recipesListAll";
    }

    @GetMapping("{recipe}")
    public String getFormForEditRecipe(
            @PathVariable Recipe recipe,
            Model model
    ) {
        model.addAttribute("category", recipe.getCategory().getDisplayName());
        model.addAttribute("recipe", recipe);
        return "editRecipe";
    }

    @PostMapping("{recipeId}")
    public String updateRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long recipeId,
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

    @GetMapping("user/{userId}")
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
