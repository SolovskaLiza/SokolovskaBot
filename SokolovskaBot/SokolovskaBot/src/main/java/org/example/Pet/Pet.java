package org.example.Pet;

import lombok.Data;

import java.nio.file.Path;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Data public abstract class Pet {
    String name;
    double weight;
    Path pathToPhoto;
    LocalDateTime cooldown;
    abstract int getSecondsCooldown();

    abstract public String getEmoji();

    abstract public String getCooldownText();
    public abstract TYPE_OF_FOOD eatChosenFood(String emojiFood);

    String getCooldownDuration() {
        Duration duration = Duration.between(cooldown.plusSeconds(getSecondsCooldown()),LocalDateTime.now()).abs();
        long totalSeconds = duration.toSeconds();
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        return minutes + ":" + seconds;
    }

    public enum TYPE_OF_FOOD{
        DELICACY,
        NUTRITIOUS,
        AVERAGE,
        TERRIBLE
    }
    public static Map<String,TYPE_OF_FOOD> makePreferences(Set<String> naturePreferences) {
        Map<String,TYPE_OF_FOOD> preferences = new HashMap<>();
        naturePreferences.forEach(p -> preferences.put(p,TYPE_OF_FOOD.DELICACY));
        List<String> shuffleEmojis = EmojiMaster.getShuffleEmojis();
        shuffleEmojis.removeAll(naturePreferences);

        shuffleEmojis.subList(0,22).forEach(e -> preferences.put(e,TYPE_OF_FOOD.NUTRITIOUS));

        shuffleEmojis.subList(22,62).forEach(e -> preferences.put(e,TYPE_OF_FOOD.AVERAGE));

        shuffleEmojis.subList(62,shuffleEmojis.size()).forEach(e -> preferences.put(e,TYPE_OF_FOOD.TERRIBLE));
        return preferences;
    }
}
