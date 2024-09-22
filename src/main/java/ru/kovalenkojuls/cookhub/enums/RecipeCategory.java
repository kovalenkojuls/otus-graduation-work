package ru.kovalenkojuls.cookhub.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RecipeCategory {
    SOUPS("Супы"),
    SALADS("Салаты"),
    DESSERTS("Десерты"),
    MAIN_COURSES("Основные блюда");

    private final String displayName;
}
