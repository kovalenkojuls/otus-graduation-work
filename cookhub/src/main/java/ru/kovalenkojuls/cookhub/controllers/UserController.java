package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.services.UserService;
import java.util.Map;

@Controller
@RequestMapping("/users")
@PreAuthorize("hasAuthority('ADMIN')")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    public String userList(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        model.addAttribute("currentUser", userService.getAuthorizedUser(userDetails));
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @GetMapping("{id}")
    public String editUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") User user,
            Model model
    ) {
        model.addAttribute("currentUser", userService.getAuthorizedUser(userDetails));
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping("{id}")
    public String updateUser(
            @AuthenticationPrincipal UserDetails userDetails,
            @PathVariable("id") User user,
            @RequestParam Map<String, String> form,
            @RequestParam String username,
            @RequestParam String email,
            Model model
    ) {

        userService.updateUser(user, username, email, form);
        model.addAttribute("currentUser", userService.getAuthorizedUser(userDetails));
        model.addAttribute("users", userService.findAll());
        return "userList";
    }
}
