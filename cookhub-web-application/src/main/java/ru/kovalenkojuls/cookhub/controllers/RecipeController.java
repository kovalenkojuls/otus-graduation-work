package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
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
            @RequestParam(required = false) RecipeCategory category,
            @PageableDefault(sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        Iterable<Recipe> page = recipeService.findRecipesByCategory(category, currentUser, pageable);

        model.addAttribute("page", page);
        model.addAttribute("url", "/recipes");
        model.addAttribute("currentUser", currentUser);
        return "recipesListAll";
    }

    @GetMapping("user/{userId}")
    public String getRecipesListByUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable Long userId,
            @PageableDefault(sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) {
        User user = userService.findById(userId).orElseThrow();
        User currentUser = userService.getAuthorizedUser(userDetails);
        Iterable<Recipe> page = recipeService.findRecipesByAuthor(user, currentUser, pageable);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("user", user);
        model.addAttribute("url", "/user/" + userId);
        model.addAttribute("page", page);
        return "recipesListByUser";
    }

    @PostMapping
    public String saveRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            Recipe recipe,
            @RequestParam("file") MultipartFile file,
            BindingResult bindingResult,
            @PageableDefault(sort = { "createdAt" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model
    ) throws IOException {

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

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("url", "/recipes");
        model.addAttribute("page", recipeService.findRecipesByCategory(null, currentUser, pageable));
        return "recipesListAll";
    }

    @GetMapping("{recipe}")
    public String getFormForUpdateRecipe(
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
            @RequestParam(name = "file", required = false) MultipartFile file
    ) throws IOException {

        Recipe recipe = recipeService.findById(recipeId).orElseThrow();
        User currentUser = userService.getAuthorizedUser(userDetails);

        if (!currentUser.equals(recipe.getAuthor())) {
            throw new RuntimeException("Нет прав на изменение рецепта");
        }

        recipeService.update(text, category, file, recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/{id}/like")
    public String likeRecipe(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable(name = "id") Recipe recipe,
            RedirectAttributes redirectAttributes,
            @RequestHeader(required = false) String referer
    ) {

        User currentUser = userService.getCurrentUser();
        recipeService.like(recipe, currentUser);

        UriComponents components = ControllerUtils.getUriComponents(redirectAttributes, referer);
        return "redirect:" + components.getPath();
    }
}
