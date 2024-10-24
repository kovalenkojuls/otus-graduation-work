package ru.kovalenkojuls.telegrambot.services;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.groupadministration.SetChatPhoto;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.kovalenkojuls.telegrambot.constants.ResponseMessageConst;
import ru.kovalenkojuls.telegrambot.domains.Recipe;
import ru.kovalenkojuls.telegrambot.domains.enums.RecipeCategory;
import ru.kovalenkojuls.telegrambot.repositories.RecipeRepository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Сервис для работы с Telegram ботом.
 */
@Service
@Slf4j
@Getter
@Setter
@RequiredArgsConstructor
public class TelegramBotService extends TelegramLongPollingBot {

    private final RecipeRepository recipeRepository;

    @Value("${telegrambot.username}")
    private String botUsername;

    @Value("${telegrambot.token}")
    private String botToken;

    @Value("${upload.path}")
    private String uploadPath;

    /**
     * Метод обработки событий от Telegram бота.
     * @param update Объект события.
     */
    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String messageText = update.getMessage().getText();
            long chatId = update.getMessage().getChatId();

            try {
                handleCommand(chatId, messageText);
            } catch (TelegramApiException | FileNotFoundException e) {
                log.error("Ошибка при обработке события: {}", e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Обработка команды от пользователя.
     * @param chatId ID чата.
     * @param messageText Текст сообщения.
     * @throws TelegramApiException Исключение Telegram API.
     * @throws FileNotFoundException Исключение при отсутствии файла.
     */
    private void handleCommand(long chatId, String messageText) throws TelegramApiException, FileNotFoundException {
        log.debug("Обработка команды: {} от чата {}", messageText, chatId);

        boolean isCategoryFound = Arrays.stream(RecipeCategory.values())
                .anyMatch(category -> category.getDisplayName().equals(messageText));

        if (isCategoryFound) {
            sendRecipe(chatId, messageText);
        } else if (messageText.startsWith("/start")) {
            sendRecipesKeyboard(chatId);
        } else {
            sendText(chatId, ResponseMessageConst.MESSAGE_ERROR);
        }
    }

    /**
     * Отправка рецепта пользователю.
     * @param chatId ID чата.
     * @param messageText Текст сообщения.
     * @throws TelegramApiException Исключение Telegram API.
     * @throws FileNotFoundException Исключение при отсутствии файла.
     */
    private void sendRecipe(long chatId, String messageText) throws TelegramApiException, FileNotFoundException {
        log.debug("Отправка рецепта категории {} в чат {}", messageText, chatId);

        SendPhoto photo = new SendPhoto();
        photo.setChatId(chatId);

        RecipeCategory recipeCategory = RecipeCategory.fromDisplayName(messageText);
        Recipe recipe = recipeRepository.findRandomRecipeByCategory(recipeCategory);

        String htmlMessage = String.format(
                ResponseMessageConst.MESSAGE_RECIPE,
                recipe.getText(),
                recipe.getCategory(),
                recipe.getAuthor().getUsername(),
                recipe.getAuthor().getFollowers().size(),
                recipe.getAuthor().getFollowings().size(),
                recipe.getCreatedAt().format(DateTimeFormatter.ofPattern("dd MMMM yyyy г., HH:mm"))
        );

        String fileName = recipe.getFilename() == null ? "no_photo.jpg" : recipe.getFilename();
        photo.setPhoto(new InputFile(new FileInputStream(new File(uploadPath + "/" + fileName)), fileName));
        photo.setCaption(htmlMessage);
        photo.setParseMode(ParseMode.HTML);

        execute(photo);
    }

    /**
     * Отправка клавиатуры с категориями рецептов.
     * @param chatId ID чата.
     * @throws TelegramApiException Исключение Telegram API.
     */
    private void sendRecipesKeyboard(long chatId) throws TelegramApiException {
        log.debug("Отправка клавиатуры с категориями в чат {}", chatId);

        SendMessage message = new SendMessage();
        message.setChatId(chatId);

        message.setText(ResponseMessageConst.MESSAGE_START);

        ReplyKeyboardMarkup keyboardMarkup = createRecipeKeyboard();
        message.setReplyMarkup(keyboardMarkup);

        execute(message);
    }

    /**
     * Отправка текстового сообщения.
     * @param chatId ID чата.
     * @param text Текст сообщения.
     * @throws TelegramApiException Исключение Telegram API.
     */
    private void sendText(long chatId, String text) throws TelegramApiException {
        log.debug("Отправка текста {} в чат {}", text, chatId);
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);

        execute(message);
    }

    /**
     * Создает клавиатуру с кнопками для каждой категории рецептов.
     * @return ReplyKeyboardMarkup объект клавиатуры.
     */
    private ReplyKeyboardMarkup createRecipeKeyboard() {
        ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardMarkup.setResizeKeyboard(true);

        List<KeyboardRow> keyboardRows = new ArrayList<>();
        for (RecipeCategory recipeCategory : RecipeCategory.values()) {
            KeyboardRow row = new KeyboardRow();
            row.add(recipeCategory.getDisplayName());
            keyboardRows.add(row);
        }

        keyboardMarkup.setKeyboard(keyboardRows);
        return keyboardMarkup;
    }
}
