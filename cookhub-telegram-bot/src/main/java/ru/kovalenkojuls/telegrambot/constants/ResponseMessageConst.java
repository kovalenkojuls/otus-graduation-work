package ru.kovalenkojuls.telegrambot.constants;

public class ResponseMessageConst {
    public static final String MESSAGE_START = "Привет! Я CookHub бот. Выберите одну из категорий, чтоб я отправил Вам рецепт";
    public static final String MESSAGE_ERROR = "Я пока не понимаю, напиши /start.";
    public static final String MESSAGE_RECIPE = "<b>Рецепт:</b> <i>%s</i>\n" +
            "<b>Категория:</b> %s\n" +
            "<b>Автор:</b> %s (подписчиков %s, подписок %s)\n" +
            "<b>Дата создания:</b> %s\n";
}
