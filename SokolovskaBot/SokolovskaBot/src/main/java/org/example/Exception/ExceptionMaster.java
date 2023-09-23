package org.example.Exception;

import org.example.PetController.EatController;
import org.example.PetController.PetCreator;
import org.example.View.BotView;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ExceptionMaster {
    private Update currentUpdate;
    private PetCreator petCreator;
    private EatController eatController;

    public void setExceptionMessage(Message exceptionMessage) {
        if(petCreator != null) petCreator.setExceptionMessage(exceptionMessage);
        else eatController.setExceptionMessage(exceptionMessage);
    }

    private Message exceptionMessage;
    private Long userId;

    public ExceptionMaster(EatController eatController) {
        currentUpdate = eatController.getCurrentUpdate();
        this.eatController = eatController;
        exceptionMessage = eatController.getExceptionMessage();
        userId = eatController.getId();
    }

    public ExceptionMaster(PetCreator petCreator) {
        currentUpdate = petCreator.getCurrentUpdate();
        this.petCreator = petCreator;
        exceptionMessage = petCreator.getExceptionMessage();
        userId = petCreator.getId();
    }

    public boolean checkCreateExceptions() {
        PetCreator.Step step =petCreator.getStep();
        try {
            switch (step) {
                case CHOOSE_PET, GET_WEIGHT_CHOOSE_TO_FINISH -> {
                    if (!currentUpdate.hasCallbackQuery()) throw new IncorrectMessageException();
                }
                case GET_PHOTO_CHOOSE_NAME, GET_NAME_CHOOSE_WEIGHT -> {
                    if (currentUpdate.hasMessage() && currentUpdate.getMessage().hasText()) {
                        if (step == PetCreator.Step.GET_NAME_CHOOSE_WEIGHT)
                            return checkNumericFormat(currentUpdate.getMessage().getText());
                    } else throw new IncorrectMessageException();
                }
                case GET_PET_CHOOSE_PHOTO -> {
                    if (!currentUpdate.hasMessage() || !currentUpdate.getMessage().hasPhoto())
                        throw new IncorrectMessageException();
                }
            }

        } catch (IncorrectMessageException e) {
            System.out.println(step);
            sendException("INCORRECT INPUT");
            return false;
        }
        if (petCreator.getExceptionMessage() != null) {
            BotView.deleteMessage(petCreator.getId(), petCreator.getExceptionMessage().getMessageId());
            setExceptionMessage(null);
        }
        return true;
    }

    public boolean checkEatControllerExceptions(){
        if(eatController.getWaitedInput() == EatController.WAITED_INPUT.CALLBACK){
            if(!eatController.getCurrentUpdate().hasCallbackQuery()){
                sendException("Нажміть на кнопки під меню!");
                return false;
            }{
                return true;
            }
        }else if(eatController.getWaitedInput() == EatController.WAITED_INPUT.MESSAGE) {
            if(eatController.getCurrentUpdate().hasMessage()){
                Message receivedMessage = eatController.getCurrentUpdate().getMessage();
                if(receivedMessage.hasText()){
                    String numberOfPet = receivedMessage.getText();
                    if(!checkNumericFormat(numberOfPet))return false;
                    if(eatController.getPetsOfThisUser().containsKey(Short.valueOf(numberOfPet))) return true;
                    else {
                        sendException("Нема вихованця під таким номером!");
                    }
                }
            }
        }
        System.out.println("error");
        return false;
    }
    private boolean checkNumericFormat(String text) {
        try {
            double sum = Double.parseDouble(text);//пробуем перевести введенный текст в число
            if (sum <= 0 || text.startsWith("0"))
                throw new NegativeValueException();//проверяем на логические несостыковки
        } catch (NumberFormatException | NegativeValueException e) {
            NumericExceptions(e); //вызываем метод числовых исключений
            return false;
        }
        return true;
    }

    private void NumericExceptions(Exception e) { //Исключения связанные с воддом числовых данных
        if (e instanceof NegativeValueException) {
            sendException("❗Разрешается только положительные числа.❗");
        } else {
            sendException("❗Неправильный ввод числа.❗");
        } //используем метод sendException, чтоб отправить ошибку
    }

    private void sendException(String text) { //метод для отправки ошибки

        if (currentUpdate.hasMessage())
            BotView.deleteMessage(userId, currentUpdate.getMessage().getMessageId()); // удаляем сообщение пользователя, которое вызвало ошибку
        if (exceptionMessage != null) {             //проверка есть ли в чате старое сообщение об ошибке
            if (exceptionMessage.getText().equals(text))
                return;     //если сообщение тоже, что и было, завершить метод

            BotView.deleteMessage(userId, exceptionMessage.getMessageId());           //запускаем изменение
        }
        setExceptionMessage(BotView.sendText(userId, text));
    }
}
