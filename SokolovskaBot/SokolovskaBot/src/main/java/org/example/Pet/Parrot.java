package org.example.Pet;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
public class Parrot extends Pet {
    private static final String emoji = "\uD83E\uDD9C";
    private static Set<String> naturePreferences = new HashSet<>(12);
    static {
        Collections.addAll(naturePreferences,
                "🍇", "🍒", "🍿", "🐛", "🐞", "🍓", "🌽", "🍎", "🍐","🍏","🍑","🥭");
    }
    private Map<String, TYPE_OF_FOOD> preferences = Pet.makePreferences(naturePreferences);

    @Override
    int getSecondsCooldown() {
        return 40;
    }

    @Override
    public String getEmoji() {
        return emoji;
    }

    @Override
    public String getCooldownText() {
        if(cooldown == null)return "Готов!";
        if(cooldown.isBefore(LocalDateTime.now().minusSeconds(40)))return "Готов!";
        return getCooldownDuration();
    }
    @Override
    public TYPE_OF_FOOD eatChosenFood(String emojiFood) {
        TYPE_OF_FOOD typeOfFood = preferences.get(emojiFood);
        switch (typeOfFood){

            case DELICACY -> {
                weight = weight*1.14365;
            }
            case NUTRITIOUS -> {
                weight = weight*1.053456;
            }
            case AVERAGE -> {
                weight = weight*1.022345;
            }
            case TERRIBLE -> {
                weight = weight*0.952345;
            }
        }
        cooldown = LocalDateTime.now();
        return typeOfFood;
    }
}
