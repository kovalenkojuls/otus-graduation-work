package ru.kovalenkojuls.cookhub.controllers;

import org.springframework.validation.BindingResult;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;
import java.util.stream.Collectors;

public class ControllerUtils {
    public static Map<String, String> getMapErrors(BindingResult bindingResult) {
        return bindingResult
                .getFieldErrors()
                .stream()
                .collect(
                        Collectors.groupingBy(
                                fieldError -> fieldError.getField() + "Error",
                                Collectors.mapping(
                                        fieldError -> fieldError.getDefaultMessage() != null ? fieldError.getDefaultMessage() : "Неизвестная ошибка",
                                        Collectors.joining("; ")
                                )
                        )
                );
    }

    public static UriComponents getUriComponents(RedirectAttributes redirectAttributes, String referer) {
        UriComponents components = UriComponentsBuilder.fromHttpUrl(referer).build();
        components.getQueryParams()
                .entrySet()
                .forEach(pair -> redirectAttributes.addAttribute(pair.getKey(), pair.getValue()));
        return components;
    }
}
