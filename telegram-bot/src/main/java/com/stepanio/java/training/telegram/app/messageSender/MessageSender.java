package com.stepanio.java.training.telegram.app.messageSender;

public interface MessageSender {
    void sendText (long chatId, String text);
}
