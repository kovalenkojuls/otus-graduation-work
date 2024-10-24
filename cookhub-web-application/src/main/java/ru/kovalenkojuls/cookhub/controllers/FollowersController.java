package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.services.UserService;

@Controller
@RequestMapping("/users")
@AllArgsConstructor
public class FollowersController {

    private final UserService userService;

    @GetMapping("follow/{user}")
    public String followToUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable User user,
            Model model
    ) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        userService.addFollowing(currentUser, user);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recipes", user.getRecipes());
        return "recipesListByUser";
    }

    @GetMapping("unfollow/{user}")
    public String unFollowFromUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable User user,
            Model model
    ) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        userService.removeFollowing(currentUser, user);

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recipes", user.getRecipes());
        return "recipesListByUser";
    }

    @GetMapping("followers")
    public String allFollowers(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User currentUser = userService.getAuthorizedUser(userDetails);
        model.addAttribute("currentUser", currentUser);
        return "followers";
    }
}
