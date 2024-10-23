package ru.kovalenkojuls.cookhub.controllers;

import org.springframework.validation.BindingResult;

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
}
