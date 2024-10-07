package ru.kovalenkojuls.cookhub.controllers;

import lombok.AllArgsConstructor;
import org.apache.kafka.common.network.Mode;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.services.UserService;

import java.util.Map;

@Controller
@AllArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final Validator validator;

    @GetMapping("/register")
    public String register() {
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            User newUser,
            BindingResult bindingResult,
            Model model) {

        if (
                errorPasswordRepeatNotEqualsPassword(newUser, model) ||
                errorValidEntityFields(newUser, bindingResult, model) ||
                errorUserAlreadyExist(newUser, model)
        ) {
            model.addAttribute("user", newUser);
            return "/register";
        }

        userService.registerUser(newUser);
        model.addAttribute("successMessage", "Пользователь успешно зарегистрирован. Подтвердите почту.");

        return "/login";
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

    private boolean errorPasswordRepeatNotEqualsPassword(User newUser, Model model) {
        if (newUser.getPassword() != null && !newUser.getPassword().equals(newUser.getPasswordRepeat())) {
            model.addAttribute("passwordError", "Пароли не спадают.");
            return true;
        }
        return false;
    }

    private boolean errorValidEntityFields(User newUser, BindingResult bindingResult, Model model) {
        validator.validate(newUser, bindingResult);
        if (bindingResult.hasErrors()) {
            model.mergeAttributes(ControllerUtils.getMapErrors(bindingResult));
            return true;
        }
        return false;
    }

    private boolean errorUserAlreadyExist(User newUser, Model model) {
        if (userService.findByUsername(newUser.getUsername()) != null) {
            model.addAttribute("usernameError", "Пользователь с таким именем уже зарегистрирован.");
            return true;
        }
        return false;
    }
}
