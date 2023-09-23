package org.example.Pet;

import java.util.*;

public class EmojiMaster {
    private final static List<String> allEmojis= new ArrayList<>(139);
    static {
        Collections.addAll(allEmojis,
                "🐵", "🐒", "🦍", "🦧", "🐶", "🐕", "🦮", "🐕‍🦺", "🐩", "🐺", "🦊", "🦝", "🐱", "🐈", "🐈‍⬛", "🦁", "🐯", "🐅", "🐆", "🐴", "🐎", "🦄", "🦓", "🦌", "🐮", "🐂", "🐃", "🐄", "🐷", "🐖", "🐗", "🐏", "🐑", "🐐", "🐪", "🐫", "🦙", "🦒", "🐘", "🦏", "🦛", "🐭", "🐁", "🐀", "🐹", "🐰", "🐇", "🐿️", "🦔", "🦇", "🐻", "🐻‍❄️", "🐨", "🐼", "🦥", "🦦", "🦨", "🦘", "🦡",  "🦃", "🐔", "🐓", "🐣", "🐤", "🐥", "🐦", "🐧", "🕊️", "🦅", "🦆", "🦢", "🦉", "🦩", "🦚", "🦜", "🐦‍⬛",  "🐸", "🐊", "🐢", "🦎", "🐍", "🐲", "🐉", "🦕", "🦖", "🐳", "🐋", "🐬", "🐟", "🐠", "🐡", "🦈", "🐙", "🐚", "🐌", "🦋", "🐛", "🐜", "🐝", "🐞", "🦗", "🕷️", "🦂", "🦟", "🍇", "🍈", "🍉", "🍊", "🍋", "🍌", "🍍", "🥭", "🍎", "🍏", "🍐", "🍑", "🍒", "🍓", "🥝", "🍅", "🥥", "🥑", "🍆", "🥔", "🥕", "🌽", "🌶️", "🥒", "🥬", "🥦", "🧄", "🧅", "🥜", "🌰"

        );
    }

    public static List<String> getShuffleEmojis(){
        ArrayList<String> shuffleList = new ArrayList<>(allEmojis);
        Collections.shuffle(shuffleList, new Random());
        return shuffleList;
    }
    public static Set<String> getShuffleValuesFromList(int count){
        Random random = new Random();
        Set<Integer> uniqueNumbers = new HashSet<>(3);
        Set<String> uniqueEmojis = new HashSet<>(3);

        while (uniqueNumbers.size() < 3) {
            int randomNumber = random.nextInt(134);
            uniqueNumbers.add(randomNumber);
        }

        for (int number : uniqueNumbers) {
            uniqueEmojis.add(allEmojis.get(number));
        }
        return uniqueEmojis;
    }
}
