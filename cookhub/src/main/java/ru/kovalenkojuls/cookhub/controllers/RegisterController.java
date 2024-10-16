package ru.kovalenkojuls.cookhub.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import ru.kovalenkojuls.cookhub.domains.User;
import ru.kovalenkojuls.cookhub.domains.dto.RecaptchaResponseDto;
import ru.kovalenkojuls.cookhub.services.UserService;
import java.util.Collections;

@Controller
@RequestMapping("/register")
@RequiredArgsConstructor
public class RegisterController {

    private final UserService userService;
    private final Validator validator;
    private final RestTemplate restTemplate;

    @Value("${recaptcha.key}")
    private String recaptchaKey;

    @Value("${recaptcha.secret}")
    private String recaptchaSecret;

    @Value("${recaptcha.url}")
    private String recaptchaUrl;

    @GetMapping
    public String register(Model model) {
        model.addAttribute("recaptchaKey", recaptchaKey);
        return "register";
    }

    @PostMapping
    public String registerUser(
            @RequestParam String passwordRepeat,
            @RequestParam("g-recaptcha-response") String recaptchaResponse,
            User newUser,
            BindingResult bindingResult,
            Model model) {

        if (
                errorPasswordRepeatNotEqualsPassword(newUser, passwordRepeat, model) ||
                errorValidEntityFields(newUser, bindingResult, model) ||
                errorUserAlreadyExist(newUser, model) ||
                errorCheckRecaptcha(recaptchaResponse, model)
        ) {
            model.addAttribute("user", newUser);
            model.addAttribute("recaptchaKey", recaptchaKey);
            return "/register";
        }

        userService.registerUser(newUser);
        model.addAttribute("successMessage", "Пользователь успешно зарегистрирован. Подтвердите почту.");

        return "/login";
    }

    @GetMapping("activate/{activationCode}")
    public String activate(@PathVariable String activationCode, Model model) {
        boolean isActivated = userService.activateUser(activationCode);
        if (isActivated) {
            model.addAttribute("successMessage", "Почта успешно подтверждена.");
        } else {
            model.addAttribute("errorMessage", "Ошибка подтверждения почты.");
        }
        return "login";
    }

    private boolean errorPasswordRepeatNotEqualsPassword(User newUser,String passwordRepeat, Model model) {
        if (newUser.getPassword() != null && !newUser.getPassword().equals(passwordRepeat)) {
            model.addAttribute("passwordRepeatError", "Пароли не спадают.");
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

    private boolean errorCheckRecaptcha(String recaptchaResponse, Model model) {
        RecaptchaResponseDto recaptchaResponseDto = restTemplate.postForObject(
                String.format("%s?secret=%s&response=%s", recaptchaUrl, recaptchaSecret, recaptchaResponse),
                Collections.EMPTY_LIST,
                RecaptchaResponseDto.class
        );

        if (!recaptchaResponseDto.isSuccess()) {
            model.addAttribute("recaptchaError", "Подвердите, что вы не робот.");
            return true;
        }
        return false;
    }
}
