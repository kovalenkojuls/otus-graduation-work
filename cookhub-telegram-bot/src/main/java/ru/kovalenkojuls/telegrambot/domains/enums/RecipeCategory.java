package ru.kovalenkojuls.telegrambot.domains.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public enum RecipeCategory {
    SOUPS("Супы"),
    SALADS("Салаты"),
    DESSERTS("Десерты"),
    MAIN_COURSES("Основные блюда");

    private final String displayName;

    private static final Map<String, RecipeCategory> CATEGORY_BY_DISPLAY_NAME =
            Arrays.stream(values()).collect(Collectors.toMap(RecipeCategory::getDisplayName, e -> e));

    public static RecipeCategory fromDisplayName(String displayName) {
        return CATEGORY_BY_DISPLAY_NAME.get(displayName);
    }
}
