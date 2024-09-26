package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.view.RedirectView;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.services.UserService;
import java.util.Map;

@Controller
@RequestMapping("/user")
@PreAuthorize("hasAuthority('ADMIN')")
@AllArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping
    public String userList(Model model) {
        model.addAttribute("users", userService.findAll());
        return "userList";
    }

    @GetMapping("{id}")
    public String editUser(@PathVariable("id") User user, Model model) {
        model.addAttribute("user", user);
        return "editUser";
    }

    @PostMapping()
    public RedirectView updateUser(
            @RequestParam("id") User user,
            @RequestParam Map<String, String> form,
            @RequestParam String username) {

        userService.updateUser(user, username, form);
        return new RedirectView("/user", true);
    }
}
