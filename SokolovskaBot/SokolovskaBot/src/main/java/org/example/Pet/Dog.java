package org.example.Pet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.*;

@Data public class Dog extends Pet {
    private static final String emoji = "\uD83D\uDC15";
    private static Set<String> naturePreferences = new HashSet<>(12);
    static {
        Collections.addAll(naturePreferences,
                "🐓", "🐂", "🐑", "🦆", "🐰", "🦃","🐄","🐑","🥩","\uD83D\uDC02","\uD83D\uDC16", "🐇");
    }
    private Map<String, TYPE_OF_FOOD> preferences = Pet.makePreferences(naturePreferences);

    @Override
    int getSecondsCooldown() {
        return 120;
    }

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
                weight = weight*1.289;
            }
            case NUTRITIOUS -> {
                weight = weight*1.176;
            }
            case AVERAGE -> {
                weight = weight*1.089;
            }
            case TERRIBLE -> {
                weight = weight*0.799;
            }
        }
        cooldown = LocalDateTime.now();
        return typeOfFood;
    }
}
