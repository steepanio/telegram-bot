package com.stepanio.java.training.telegram.app;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

public class TestHelper {
    public InlineKeyboardMarkup createMarkup () {
        InlineKeyboardButton button = new InlineKeyboardButton("button");
        button.setCallbackData("button");
        InlineKeyboardRow row = new InlineKeyboardRow();
        row.add(button);
        return new InlineKeyboardMarkup(List.of(row));
    }

    public IncomingMessageDto createMessage(String arg) {
        IncomingMessageDto dto = new IncomingMessageDto();
        dto.setArguments(List.of(arg));
        dto.setCallback(false);
        dto.setUserId(1L);
        dto.setChatId(1L);
        return dto;
    }

    public BotUserDto createUser() {
        BotUserDto user = new BotUserDto();
        user.setFilter(new SearchFilter());
        return user;
    }

    public VacancyDto createVacancy() {
        VacancyDto vacancy = new VacancyDto();
        vacancy.setCompany("Company");
        vacancy.setUrl("http://test.com");
        vacancy.setSalaryMin(7000);
        vacancy.setSalaryMax(8000);
        vacancy.setExperience(1);
        return vacancy;
    }
}

