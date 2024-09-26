package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.view.RedirectView;
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

    @GetMapping("/")
    public String home() {
        return "home";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/main")
    public String main(@RequestParam(required = false) RecipeCategory category, Model model) {
        Iterable<Recipe> recipes = recipeService.getRecipesByCategory(category);
        model.addAttribute("recipes", recipes);

        return "main";
    }

    @PostMapping("/main")
    public RedirectView add(
            @RequestParam String text,
            @RequestParam RecipeCategory category,
            @RequestParam("file") MultipartFile file,
            Model model) throws IOException {

        Recipe recipe = new Recipe(text, category, userService.getCurrentUser());
        recipeService.save(recipe, file);

        return new RedirectView("/main", true);
    }
}