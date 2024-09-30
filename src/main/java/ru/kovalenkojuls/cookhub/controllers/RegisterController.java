package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;
import ru.kovalenkojuls.cookhub.services.UserService;

@Controller
@AllArgsConstructor
public class RegisterController {
    private final UserService userService;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public RedirectView registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String email,
            RedirectAttributes redirectAttributes) {

        if (userService.findByUsername(username) != null) {
            redirectAttributes.addFlashAttribute("errorMessage", "Пользователь с таким именем уже зарегистрирован.");
            return new RedirectView("/register", true);
        }
        userService.registerUser(username, password, email);
        redirectAttributes.addFlashAttribute("successMessage", "Пользователь успешно зарегистрирован. Подтвердите почту.");

        return new RedirectView("/login", true);
    }

    @GetMapping("/activate/{activationCode}")
    public String activate(@PathVariable String activationCode, Model model) {
        boolean isActivated = userService.activateUser(activationCode);
        if (isActivated) {
            model.addAttribute("successMessage", "Почта успешно подтверждена.");
        } else {
            model.addAttribute("errorMessage", "Ошибка подтверждения почты.");
        }
        return "login";
    }
}
