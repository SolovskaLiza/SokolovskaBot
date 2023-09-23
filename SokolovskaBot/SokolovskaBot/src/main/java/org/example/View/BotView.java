package org.example.View;

import org.example.Bot;
import org.example.PetController.PetController;
import org.example.PetController.PetCreator;
import org.example.RegisterBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.groupadministration.GetChat;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageReplyMarkup;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;

public class BotView {
    private static final Bot bot = RegisterBot.myBot;
    public static Message sendText(Long who, String what) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).parseMode("HTML").build();    //Message content
        try {
            return bot.execute(sm);//Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }
    public static Message sendMenu(Long who, String what,InlineKeyboardMarkup editKeyboard ) {
        SendMessage sm = SendMessage.builder()
                .chatId(who.toString()) //Who are we sending a message to
                .text(what).parseMode("HTML").replyMarkup(editKeyboard).build();    //Message content
        try {
            return bot.execute(sm);//Actually sending the message
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);      //Any error will be printed here
        }
    }

    public static void deleteMessage(Long chatId, int messageID) { //Метод для удаления сообщений
        DeleteMessage deleteMessage = new DeleteMessage(chatId.toString(), messageID);
        try {
            bot.execute(deleteMessage);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String savePhoto(List<PhotoSize> photoSizeList, String name) {
        PhotoSize largestPhoto = photoSizeList.stream().max(Comparator.comparingInt(PhotoSize::getFileSize)).orElse(null); //выбираем фото с лучшим качеством
        GetFile getFile = new GetFile();
        assert largestPhoto != null;
        getFile.setFileId(largestPhoto.getFileId());
        try {
            org.telegram.telegrambots.meta.api.objects.File file = bot.execute(getFile); // скачиваем файл
            String url = "https://api.telegram.org/file/bot" + bot.getBotToken() + "/" + file.getFilePath(); // url к фото
            try (InputStream in = new URL(url).openStream()) {
                Path target = Path.of("src/Photos/" + name + ".jpg");
                Files.copy(in, target, StandardCopyOption.REPLACE_EXISTING); //копируем файл
                return target.toString(); //возвращаем путь
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static Message sendPhoto(long id, Path URL, String caption, InlineKeyboardMarkup keyboardMarkup) {
        SendPhoto sendPhoto = new SendPhoto();
        sendPhoto.setChatId(id);
        sendPhoto.setPhoto(new InputFile(URL.toFile()));
        sendPhoto.setCaption(caption);
        sendPhoto.setReplyMarkup(keyboardMarkup);
        sendPhoto.setParseMode("HTML");
        try {
            Message response = bot.execute(sendPhoto);
            return response;
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static String getUsername(Long id) {
        GetChat getChat = new GetChat(id.toString());
        try {
            return bot.execute(getChat).getUserName();
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public static void closeKeyboard(CallbackQuery callbackQuery){
        AnswerCallbackQuery close = AnswerCallbackQuery.builder()       //Создаем AnswerCallbackQuery чтоб не было вечной загрузки на кнопке
                .callbackQueryId(callbackQuery.getId()).build();
        try {
            bot.execute(close);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public static void changeMenuForPetController(PetController petController, String text, InlineKeyboardMarkup keyboardMarkup){
        Long userId = petController.getId();
        int menuId = petController.getMenu().getMessageId();
        if(petController.getMenu().hasPhoto())changeMenuWithPhoto(userId,menuId,text,keyboardMarkup);
        else changeMenuWithoutPhoto(userId,menuId,text,keyboardMarkup);
    }
    private static void changeMenuWithoutPhoto(Long userID, int KeyboardID,String text, InlineKeyboardMarkup keyboardMarkup){
        EditMessageText newTxt = EditMessageText.builder()
                .chatId(userID)
                .messageId(KeyboardID).text(text).parseMode("HTML").build();
        EditMessageReplyMarkup newKb = EditMessageReplyMarkup.builder()
                .chatId(userID).messageId(KeyboardID).build();

        newKb.setReplyMarkup(keyboardMarkup);
        try{
            bot.execute(newTxt);
            bot.execute(newKb);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private static void changeTextMessage(Long userID, int KeyboardID,String text){
        EditMessageText newTxt = EditMessageText.builder()
                .chatId(userID)
                .messageId(KeyboardID).text(text).parseMode("HTML").build();
        try{
            bot.execute(newTxt);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    private static void changeMenuWithPhoto(Long userID, int KeyboardID,String text, InlineKeyboardMarkup keyboardMarkup) {
        EditMessageCaption editMessageCaption = new EditMessageCaption();
        editMessageCaption.setChatId(userID);
        editMessageCaption.setMessageId(KeyboardID);
        editMessageCaption.setCaption(text);
        editMessageCaption.setReplyMarkup(keyboardMarkup);
        editMessageCaption.setParseMode("HTML");
        try {
            bot.execute(editMessageCaption);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}

