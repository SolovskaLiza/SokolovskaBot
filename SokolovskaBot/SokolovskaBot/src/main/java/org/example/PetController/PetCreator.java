package org.example.PetController;

import lombok.Data;
import org.example.Exception.ExceptionMaster;
import org.example.Pet.Cat;
import org.example.Pet.Dog;
import org.example.Pet.Parrot;
import org.example.View.BotView;
import org.example.Pet.Pet;
import org.example.View.Keyboard;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;


import javax.validation.constraints.NotNull;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Data
public class PetCreator implements PetController {

    private Update currentUpdate;

    private final Long id; //user id

    public Long getId() {
        return id;
    }

    private Message menu;

    private Message exceptionMessage;

    private StringBuilder keyboardText = new StringBuilder("""
            \uD83D\uDD24Ім'я:
            ⚖️Вага:\s кг
            """);
    private LocalDateTime updateTime = LocalDateTime.now();

    private Pet pet;

    private Step step = Step.CHOOSE_PET;

    PetCreator(long id) {
        menu = BotView.sendMenu(id, "Яку тваринку створимо?", Keyboard.makeKeyboardForCreator(this));
        this.id = id;
    }

    @Override
    public void execute() {
        setUpdateTime(LocalDateTime.now());
        if (currentUpdate.hasCallbackQuery()) {
            workWithCallback();
        } else {
            if (!new ExceptionMaster(this).checkCreateExceptions()) return;
            fillPetInfo();
        }
        creatorAction();
    }

    private void fillPetInfo() {
        step = step.nextStep();
        Message message = currentUpdate.getMessage();
        String textMessage = message.getText();
        switch (step) {
            case GET_PHOTO_CHOOSE_NAME -> {
                pet.setPathToPhoto(Path.of(BotView.savePhoto(message.getPhoto(), id+" "+pet.getName()+" "+currentUpdate.getUpdateId())));
            }
            case GET_NAME_CHOOSE_WEIGHT -> {
                pet.setName(textMessage);
                changeInfo(textMessage, Step.GET_NAME_CHOOSE_WEIGHT);
            }
            case GET_WEIGHT_CHOOSE_TO_FINISH -> {
                pet.setWeight(Integer.parseInt(textMessage));
                changeInfo(textMessage, Step.GET_WEIGHT_CHOOSE_TO_FINISH);
            }
        }
    }

    private void creatorAction() {
        if (currentUpdate.hasMessage()) BotView.deleteMessage(id, currentUpdate.getMessage().getMessageId());
        String menuText = "";
        switch (step) {
            case CHOOSE_PET -> {
                BotView.deleteMessage(id, getMenu().getMessageId());
                menu = BotView.sendMenu(id, "Яку тваринку створимо?", Keyboard.makeKeyboardForCreator(this));
            }
            case GET_PHOTO_CHOOSE_NAME -> {
                BotView.deleteMessage(id, getMenu().getMessageId());
                menu = BotView.sendPhoto(id, pet.getPathToPhoto(),
                        keyboardText + "\n\n✍\uD83C\uDFFBОберіть ім'я для вашого вихованця",
                        Keyboard.makeKeyboardForCreator(this)
                );
            }
            case COMPLETE -> {
                BotView.deleteMessage(id,getMenu().getMessageId());
                PetControllerFactory.usersPets.add(new Pair<>(id,pet));
                System.out.println(PetControllerFactory.usersPets);
               menu = BotView.sendText(id,"✅Вихованця створено!");
            }
            default -> {
                switch (step){
                    case GET_PET_CHOOSE_PHOTO -> menuText = "\n\n✍\uD83C\uDFFBВідправте фото вашого вихованця...";
                    case GET_NAME_CHOOSE_WEIGHT -> menuText ="\n\n✍\uD83C\uDFFBСкільки зараз важить ваш вихованець?";
                    case GET_WEIGHT_CHOOSE_TO_FINISH -> menuText ="\n\n✍\uD83C\uDFFBЗавершити створення вихованця?";
                }
                BotView.changeMenuForPetController(this,keyboardText+menuText,Keyboard.makeKeyboardForCreator(this));
            }
        }
    }

    private void workWithCallback() {
        CallbackQuery callbackQuery = currentUpdate.getCallbackQuery();
        BotView.closeKeyboard(callbackQuery);
        String buttonName = callbackQuery.getData();
        if (buttonName.equals("back")) {
            step = step.previousStep();
            return;
        } else
            step = step.nextStep();

        switch (callbackQuery.getData()) {
            case "next" -> {
               creatorAction();
            }
            case "cat" -> {
                changeInfo("\uD83D\uDC08\u200D⬛", Step.GET_PET_CHOOSE_PHOTO);
                pet = new Cat();
            }
            case "dog" -> {
                changeInfo("\uD83D\uDC15", Step.GET_PET_CHOOSE_PHOTO);
                pet = new Dog();
            }
            case "parrot" -> {
                changeInfo("\uD83E\uDD9C", Step.GET_PET_CHOOSE_PHOTO);
                pet = new Parrot();
            }
        }
    }

    public void changeInfo(@NotNull String s, Step step) { // метод для изменения основного текста, с помощью регулярных выражений
        Pattern pattern = null;
        String temp = null;
        switch (step) {
            case GET_PET_CHOOSE_PHOTO -> {
                temp ="🔤";
                pattern = Pattern.compile("(.*\n🔤)");
            }
            case GET_NAME_CHOOSE_WEIGHT -> {

                temp = "Ім'я: ";
                pattern = Pattern.compile("(Ім'я:.*)");
            }
            case GET_WEIGHT_CHOOSE_TO_FINISH -> {

                temp = "Вага: ";
                pattern = Pattern.compile("(Вага:.*)");
            }
        }
        Matcher matcher = pattern.matcher(keyboardText);
        if (matcher.find()) {
            keyboardText = new StringBuilder(keyboardText.toString().replaceAll(matcher.group(1), (temp + s)));
        }
    }

    public enum Step {
        CHOOSE_PET,
        GET_PET_CHOOSE_PHOTO,
        GET_PHOTO_CHOOSE_NAME,
        GET_NAME_CHOOSE_WEIGHT,
        GET_WEIGHT_CHOOSE_TO_FINISH,
        COMPLETE;

        public Step nextStep() {
            switch (this) {
                case CHOOSE_PET -> {
                    return GET_PET_CHOOSE_PHOTO;
                }
                case GET_PET_CHOOSE_PHOTO -> {
                    return GET_PHOTO_CHOOSE_NAME;
                }
                case GET_PHOTO_CHOOSE_NAME -> {
                    return GET_NAME_CHOOSE_WEIGHT;
                }
                case GET_NAME_CHOOSE_WEIGHT -> {
                    return GET_WEIGHT_CHOOSE_TO_FINISH;
                }
                case GET_WEIGHT_CHOOSE_TO_FINISH -> {
                    return COMPLETE;
                }
                default -> {
                    return null;
                }
            }
        }

        public Step previousStep() {
            switch (this) {
                case COMPLETE -> {
                    return GET_WEIGHT_CHOOSE_TO_FINISH;
                }
                case GET_WEIGHT_CHOOSE_TO_FINISH -> {
                    return GET_NAME_CHOOSE_WEIGHT;
                }
                case GET_NAME_CHOOSE_WEIGHT -> {
                    return GET_PHOTO_CHOOSE_NAME;
                }
                case GET_PHOTO_CHOOSE_NAME -> {
                    return GET_PET_CHOOSE_PHOTO;
                }
                case GET_PET_CHOOSE_PHOTO -> {
                    return CHOOSE_PET;
                }
                default -> {
                    return null;
                }
            }
        }
    }
}
