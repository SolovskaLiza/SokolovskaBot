package org.example;

import org.example.PetController.PetControllerFactory;
import org.example.View.BotView;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.MessageAutoDeleteTimerChanged;
import org.telegram.telegrambots.meta.api.objects.Update;


public class Bot extends TelegramLongPollingBot
{

    private final MessageAutoDeleteTimerChanged messageAutoDeleteTimerChanged = new MessageAutoDeleteTimerChanged(10);
    public String getBotUsername() {
        return "sokolovska_bot";
    }

    @Override
    public String getBotToken() {
            return System.getenv.get("BOT_TOKEN");
    }

    public void onUpdateReceived(Update update) {
        Long userId;
        if(update.hasCallbackQuery()) {
            userId = update.getCallbackQuery().getFrom().getId();
            PetControllerFactory.workWithController(userId, update);
        }
        if(update.hasMessage()){
            userId = update.getMessage().getChatId();
            if(update.getMessage().isCommand()) workWithCommand(update);
            else  {
                if(PetControllerFactory.livingControllers.containsKey(userId)){
                    PetControllerFactory.workWithController(userId, update);
                }
                else BotView.deleteMessage(userId,update.getMessage().getMessageId());
            }
        }
    }

    private void workWithCommand(Update update) {
        Message message = update.getMessage();
        Long userID = message.getChatId();
        switch (update.getMessage().getText()){
            case "/start" -> BotView.sendText(userID,"start");
            case "/create" -> PetControllerFactory.getPetCreator(userID);
            case "/feed" -> PetControllerFactory.getEatController(userID);
        }
        BotView.deleteMessage(userID,message.getMessageId());
    }
}
