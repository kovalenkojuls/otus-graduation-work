package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.enums.RecipeCategory;
import ru.kovalenkojuls.cookhub.services.RecipeService;
import ru.kovalenkojuls.cookhub.services.UserService;
import java.io.IOException;

@Controller
@AllArgsConstructor
public class MainController {
    private final RecipeService recipeService;
    private final UserService userService;
    private final Validator validator;

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/main")
    public String main(
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(required = false) RecipeCategory category, Model model
    ) {

        model.addAttribute("currentUserId", userService.getAuthorizedUser(userDetails).getId());
        model.addAttribute("recipes", recipeService.getRecipesByCategory(category));
        return "main";
    }

    @PostMapping("/main")
    public String add(
            @AuthenticationPrincipal UserDetails userDetails,
            Recipe recipe,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult,
            Model model) throws IOException {

        recipe.setAuthor(userService.getAuthorizedUser(userDetails));

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
        return "main";
    }
}