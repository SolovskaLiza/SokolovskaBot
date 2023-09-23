package org.example.PetController;

import org.telegram.telegrambots.meta.api.objects.Update;

class PetControllerCaller extends Thread{
    private Long UserId;
    private Update currentUpdateForController;
    PetControllerCaller(Long UserId, Update currentUpdateForController){
        this.UserId=UserId;
        this.currentUpdateForController = currentUpdateForController;
    }
    @Override
    public void run() {
      PetController petController = PetControllerFactory.livingControllers.get(UserId);
      petController.setCurrentUpdate(currentUpdateForController);
      petController.execute();
    }
}
