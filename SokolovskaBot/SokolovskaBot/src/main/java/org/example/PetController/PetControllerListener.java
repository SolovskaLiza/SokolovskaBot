package org.example.PetController;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class PetControllerListener {
    PetControllerListener() {
        listen();
    }

    void listen() {
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(1);
        executorService.scheduleAtFixedRate(PetControllerFactory::removeDeadControllers, 10, 10, TimeUnit.SECONDS);
    }
}
