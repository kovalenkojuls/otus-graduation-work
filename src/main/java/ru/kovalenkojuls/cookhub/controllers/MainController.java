package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;
import ru.kovalenkojuls.cookhub.domains.Recipe;
import ru.kovalenkojuls.cookhub.domains.RecipeCategory;
import ru.kovalenkojuls.cookhub.repositories.RecipeRepository;
import ru.kovalenkojuls.cookhub.services.UserService;

@Controller
@AllArgsConstructor
public class MainController {
    private final RecipeRepository recipeRepository;
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
    public String main(Model model) {
        Iterable<Recipe> recipes = recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "main";
    }

    @PostMapping("/main")
    public RedirectView add(@RequestParam String text, @RequestParam RecipeCategory category, Model model) {
        Recipe recipe = new Recipe(text, category);
        recipeRepository.save(recipe);
        return new RedirectView("/main", true);
    }

    @PostMapping("/main/filter")
    public String filter(@RequestParam(required = false) RecipeCategory category, Model model) {
        Iterable<Recipe> recipes =
                category != null ? recipeRepository.findByCategory(category) : recipeRepository.findAll();
        model.addAttribute("recipes", recipes);
        return "main";
    }
}