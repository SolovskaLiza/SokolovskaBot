package org.example.PetController;

import lombok.Data;
import org.example.Exception.ExceptionMaster;
import org.example.Pet.EmojiMaster;
import org.example.Pet.Pet;
import org.example.View.BotView;
import org.example.View.Keyboard;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Data
public class EatController implements PetController {

    private Message exceptionMessage;

    private Map<Short, Pet> petsOfThisUser;

    private Long id;
    private Pet selectedPet;
    private Message menu;
    private LocalDateTime updateTime;
    private Update currentUpdate;
    private WAITED_INPUT waitedInput = WAITED_INPUT.MESSAGE;

    public EatController(Long id) {
        this.id = id;
        petsOfThisUser = getUserPets(id);
        sendListOfUserPets();
    }

    private void sendListOfUserPets() {
        StringBuilder listOfPets = new StringBuilder();
        for (Short number : petsOfThisUser.keySet()) {
            Pet p = petsOfThisUser.get(number);
            listOfPets.append(number).append(" - ").append(p.getEmoji()).append(p.getName()).append(" ⏳").append(p.getCooldownText()).append("\n");
        }
        menu = BotView.sendText(id, listOfPets + "\n\nНапишіть цифру вихованця, якого ви бажаєте нагудувати...");
    }

    @Override
    public LocalDateTime getUpdateTime() {
        return null;
    }


    @Override
    public void execute() {
        setUpdateTime(LocalDateTime.now());
        if (!new ExceptionMaster(this).checkEatControllerExceptions()) return;
        if (waitedInput == WAITED_INPUT.MESSAGE) workWithMessage();
        else workWithCallBack();
    }

    private void workWithMessage() {

        String numberOfPet = currentUpdate.getMessage().getText();
        selectedPet = petsOfThisUser.get(
                Short.valueOf(numberOfPet)
        );
        if (currentUpdate.hasMessage()) BotView.deleteMessage(id, currentUpdate.getMessage().getMessageId());
        showInfoAboutPet();
    }

    private void showInfoAboutPet() {
        waitedInput = WAITED_INPUT.CALLBACK;
        BotView.deleteMessage(id, getMenu().getMessageId());
        InlineKeyboardMarkup keyboardMarkup;
        if(selectedPet.getCooldownText().equals("Готов!"))keyboardMarkup = Keyboard.makeKeyboardFromButtons(Keyboard.back, Keyboard.feed);
        else keyboardMarkup = Keyboard.makeKeyboardFromButtons(Keyboard.back);
        menu = BotView.sendPhoto(id, selectedPet.getPathToPhoto(),

                "\uD83D\uDD24" + selectedPet.getName() + "\n" +
                        "⚖️" + String.format("%.3f",selectedPet.getWeight()) + "\n"
                        + "⏳" + selectedPet.getCooldownText(),

                keyboardMarkup);
    }


    private void workWithCallBack() {
        CallbackQuery callbackQuery = currentUpdate.getCallbackQuery();
        BotView.closeKeyboard(callbackQuery);
        String buttonName = callbackQuery.getData();
        switch (buttonName) {
            case "feed" -> {
                sendFoods();
            }
            case "back","finish" -> {
                waitedInput = WAITED_INPUT.MESSAGE;
                sendListOfUserPets();
            }
            default -> feedPetByChosenEmoji(buttonName);
        }
    }

    private void feedPetByChosenEmoji(String buttonName) {
        double weightBeforeMeal = selectedPet.getWeight();
        Pet.TYPE_OF_FOOD typeOfFood = selectedPet.eatChosenFood(buttonName);
        double weightAfterMeal = selectedPet.getWeight();
        double weightDifference = Math.abs(weightAfterMeal - weightBeforeMeal);
        String textMenu = "";
        switch (typeOfFood) {
            case DELICACY -> {
                textMenu = String.format("\uD83E\uDD29Браво! %s у захваті.\n\nВін набрав %.3f кг маси", selectedPet.getName(), weightDifference);
            }
            case NUTRITIOUS -> {
                textMenu = String.format("\uD83E\uDD73Непогано! %s сподобалось.\n\nВін набрав %.3f кг маси", selectedPet.getName(), weightDifference);
            }
            case AVERAGE -> {
                textMenu = String.format("\uD83D\uDE35\u200D\uD83D\uDCAB%s спробував, але йому не дуже сподобаолось .\n\nВін набрав усього %.3f кг маси", selectedPet.getName(), weightDifference);
            }
            case TERRIBLE -> {
                textMenu = String.format("\uD83E\uDD2EНа жаль, %s не став це їсти, цього разу він залишився голодний.\n\nВін схуднув на %.3f кг маси", selectedPet.getName(), weightDifference);
            }
        }
        menu = BotView.sendPhoto(id, selectedPet.getPathToPhoto(),

                "\uD83D\uDD24" + selectedPet.getName() + "\n" +
                        "⚖️" + String.format("%.3f",selectedPet.getWeight()) + "\n\n"
                        + textMenu, Keyboard.makeKeyboardFromButtons(Keyboard.finish));
    }

    private void sendFoods() {
        System.out.println("feed");
        Set<String> emojis = EmojiMaster.getShuffleValuesFromList(3);
        System.out.println(emojis);
        InlineKeyboardMarkup keyboardMarkup = Keyboard.makeKeyboardFromEmojis(emojis);
        BotView.changeMenuForPetController(this, "Перевіремо, як ви знаєте свого вихованця?" + "\n" + "Оберіть для нього страву...", keyboardMarkup);
    }

    @Override
    public void setCurrentUpdate(Update currentUpdateForController) {
        currentUpdate = currentUpdateForController;
    }

    private static Map<Short, Pet> getUserPets(Long userId) {
        Map<Short, Pet> userPets = new HashMap<>();
        short i = 1;
        for (Pair<Long, Pet> pair : PetControllerFactory.usersPets) {
            if (pair.getFirst().equals(userId)) {
                userPets.put(i, pair.getSecond());
                i++;
            }
        }
        return userPets;
    }

    public enum WAITED_INPUT {
        MESSAGE, CALLBACK
    }
}
