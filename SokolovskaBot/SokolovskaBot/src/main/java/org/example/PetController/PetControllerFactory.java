package org.example.PetController;

import org.example.Pet.Cat;
import org.example.Pet.Pet;
import org.example.View.BotView;
import org.glassfish.grizzly.utils.Pair;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PetControllerFactory{
    public final static ConcurrentHashMap<Long, PetController> livingControllers = new ConcurrentHashMap<>();
    public final static CopyOnWriteArrayList<Pair<Long, Pet>> usersPets = new CopyOnWriteArrayList<>();
    static {
        Pet cat = new Cat();
        cat.setName("JOpich");
        cat.setWeight(54);
        cat.setPathToPhoto(Path.of("src/Photos/401967970.jpg"));
        usersPets.add(new Pair<>(401967970L, cat));
    }
    private static final ExecutorService executorService = Executors.newCachedThreadPool();
    private static final PetControllerListener listener = new PetControllerListener();
    public static void workWithController(Long id, Update currentUpdate){
        if(livingControllers.containsKey(id)) {
            executorService.execute(new PetControllerCaller(id, currentUpdate));
        }
    }
    public static void getPetCreator(Long userID){
        PetCreator instance = new PetCreator(userID);
        deletePreviousPetControllerMenu(userID);
        livingControllers.put(userID,instance);
    }

    private static void deletePreviousPetControllerMenu(Long userID) {
        PetController previousPetController = livingControllers.get(userID);
        if(previousPetController != null){
            int previousMenuId = previousPetController.getMenu().getMessageId();
            BotView.deleteMessage(userID,previousMenuId);
        }
    }

    public static void getEatController(Long userID) {
        EatController instance = new EatController(userID);
        deletePreviousPetControllerMenu(userID);
        livingControllers.put(userID,instance);
    }
    static void removeDeadControllers(){
        for(Long controller_userId : livingControllers.keySet()){
            PetController petController = livingControllers.get(controller_userId);
            if(
                    petController.getUpdateTime().isBefore(
                            LocalDateTime.now().minusSeconds(30)
                    )
            ){
                BotView.deleteMessage(controller_userId,petController.getMenu().getMessageId());
                livingControllers.remove(controller_userId);
            }
        }
    }
}
