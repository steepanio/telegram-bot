package com.stepanio.java.training.telegram.app.messageSender;

import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

public interface MessageSender {
    void sendText (long chatId, String text, InlineKeyboardMarkup keyboardMarkup);
    public void editMessage(long chatId, int messageId, String text, InlineKeyboardMarkup keyboard);
}
