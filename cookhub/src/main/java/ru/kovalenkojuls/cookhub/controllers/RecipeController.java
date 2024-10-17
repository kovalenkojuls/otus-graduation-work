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

        model.addAttribute("currentUser", userService.getAuthorizedUser(userDetails));
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

        User currentUser = userService.getAuthorizedUser(userDetails);
        recipe.setAuthor(currentUser);
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

        model.addAttribute("currentUser", userService.getAuthorizedUser(userDetails));
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

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recipes", currentUser.getRecipes());
        if (!currentUser.equals(recipe.getAuthor())) {
            return "recipesListByUser";
        }

        recipeService.update(text, category, file, recipe);
        return "recipesListByUser";
    }

    @GetMapping("user/{userId}")
    public String getUserRecipes(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId,
            Model model
    ) {
        User user = userService.findById(userId).orElseThrow();
        User currentUser = userService.getAuthorizedUser(userDetails);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", user);
        model.addAttribute("recipes", user.getRecipes());
        return "recipesListByUser";
    }
}
