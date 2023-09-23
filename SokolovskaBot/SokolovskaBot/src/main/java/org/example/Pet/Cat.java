package org.example.Pet;

import lombok.Data;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

@Data
public class Cat extends Pet {
    private static final String emoji = "\uD83D\uDC08\u200D⬛";

    @Override
    int getSecondsCooldown() {
        return 60;
    }

    private static Set<String> naturePreferences = new HashSet<>();
    static {
        Collections.addAll(naturePreferences,
                "🐁", "🐀", "🐡", "🐠", "🐟", "🐦", "🐥", "🐹", "🐭", "🕊️", "🐿️");
    }
    private Map<String, TYPE_OF_FOOD> preferences = Pet.makePreferences(naturePreferences);
    @Override
    public String getEmoji() {
        return emoji;
    }

    @Override
    public String getCooldownText() {
        if(cooldown == null)return "Готов!";
        if(cooldown.isBefore(LocalDateTime.now().minusSeconds(60)))return "Готов!";
        return getCooldownDuration();
    }

    @Override
    public TYPE_OF_FOOD eatChosenFood(String emojiFood) {
        TYPE_OF_FOOD typeOfFood = preferences.get(emojiFood);
        switch (typeOfFood){

            case DELICACY -> {
                weight = weight*1.22;
            }
            case NUTRITIOUS -> {
                weight = weight*1.11;
            }
            case AVERAGE -> {
                weight = weight*1.055;
            }
            case TERRIBLE -> {
                weight = weight*0.87;
            }
        }
        cooldown = LocalDateTime.now();
        return typeOfFood;
    }
}
