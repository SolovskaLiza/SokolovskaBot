package org.example.PetController;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.time.LocalDateTime;

public interface PetController {
    LocalDateTime getUpdateTime();

    Message getMenu();
    Long getId();

    void execute();

    void setCurrentUpdate(Update currentUpdateForController);
}
