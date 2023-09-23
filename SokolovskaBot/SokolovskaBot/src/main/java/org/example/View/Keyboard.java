package org.example.View;

import org.example.PetController.PetCreator;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.*;

public class Keyboard {
    public final static InlineKeyboardButton cat = InlineKeyboardButton.builder().text("\uD83D\uDC31").callbackData("cat").build();
    public final static InlineKeyboardButton dog = InlineKeyboardButton.builder().text("\uD83D\uDC36").callbackData("dog").build();
    public final static InlineKeyboardButton parrot = InlineKeyboardButton.builder().text("\uD83E\uDD9C").callbackData("parrot").build();
    public final static InlineKeyboardButton back = InlineKeyboardButton.builder().text("\uD83D\uDD19").callbackData("back").build();
    public final static InlineKeyboardButton finish = InlineKeyboardButton.builder().text("✅").callbackData("finish").build();
    public final static InlineKeyboardButton next = InlineKeyboardButton.builder().text("▶\uFE0F").callbackData("next").build();
    public final static InlineKeyboardButton feed = InlineKeyboardButton.builder().text("\uD83E\uDDB4").callbackData("feed").build();



    public static InlineKeyboardMarkup makeKeyboardForCreator(PetCreator petCreator) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        switch (petCreator.getStep()) {
            case CHOOSE_PET -> Collections.addAll(buttons, cat,dog,parrot);

            case GET_PET_CHOOSE_PHOTO -> {
                buttons.add(back);
                if(petCreator.getPet().getPathToPhoto()!=null)buttons.add(next);
            }
            case GET_PHOTO_CHOOSE_NAME -> {
                buttons.add(back);
                if(petCreator.getPet().getName()!=null)buttons.add(next);
            }
            case GET_NAME_CHOOSE_WEIGHT -> {
                buttons.add(back);
                if(petCreator.getPet().getWeight()!=0)buttons.add(next);
            }

            case GET_WEIGHT_CHOOSE_TO_FINISH -> Collections.addAll(buttons, back,finish);
        }
        return InlineKeyboardMarkup.builder().keyboardRow(buttons).build();
    }
    public static InlineKeyboardMarkup makeKeyboardFromButtons(InlineKeyboardButton ... buttons){
        List<InlineKeyboardButton> buttonList = Arrays.asList(buttons);
        return InlineKeyboardMarkup.builder().keyboardRow(buttonList).build();
    }

    public static InlineKeyboardMarkup makeKeyboardFromEmojis(Set<String> emojis) {
        List<InlineKeyboardButton> buttons = new ArrayList<>();
        for(String emoji : emojis){
            buttons.add(InlineKeyboardButton.builder().text(emoji).callbackData(emoji).build());
        }
        return InlineKeyboardMarkup.builder().keyboardRow(buttons).build();
    }
}
