package com.stepanio.java.training.telegram.app.command;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.CommandResponse;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalTime;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.never;

public class NotifyCommandTest {
    private BotCommandHelper helper;
    private RepositoryService service;
    private NotifyCommand notifyCommand;
    private final TestHelper testHelper = new TestHelper();

    @BeforeEach
    void setUp() {
        helper = mock(BotCommandHelper.class);
        service = mock(RepositoryService.class);
        notifyCommand = new NotifyCommand(helper, service);
    }

    @ParameterizedTest
    @ValueSource(strings = {"77 15", "12", "-2 30", "23 65", ""})
    void handleTestWithInvalidArguments(String arguments) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = testHelper.createMessage(arguments);
        CommandResponse response = notifyCommand.handle(message);
        assertNull(service.getUser(1L).getFilter().getNotificationTime());
        assertEquals("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/notify 7 15 (разделитель пробел)", response.getText());
        verify(service, never()).saveUser(any());
    }

    @ParameterizedTest
    @MethodSource("commandData")
    void handleTestWithValidArguments(List<String> arguments, LocalTime expected) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        message.setCallback(false);
        message.setArguments(arguments);
        CommandResponse response = notifyCommand.handle(message);
        LocalTime notificationTime = user.getFilter().getNotificationTime();
        assertEquals(expected, notificationTime);
        verify(service).saveUser(user);
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n"
                , response.getText());
    }

    @ParameterizedTest
    @ValueSource(strings = {"notification_12:00", "notification_15:00", "notification_18:00"})
    void handleTestWithSomeCallbackData (String callbackData) {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);
        when(helper.getInlineKeyboardMarkup(any()))
                .thenReturn(testHelper.createMarkup());
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(true);
        message.setCallbackData(callbackData);
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = notifyCommand.handle(message);
        String time = callbackData.replace("notification_", "");
        String[] parts = time.split(":");
        int hours = Integer.parseInt(parts[0]);
        int minutes = Integer.parseInt(parts[1]);
        LocalTime expected = LocalTime.of(hours, minutes);
        assertEquals(expected, user.getFilter().getNotificationTime());
        verify(service).saveUser(user);
        assertEquals(testHelper.createMarkup(), response.getMarkup());
        assertEquals("Задайте критерии поиска и время нотификации.\n Затем нажмите \"Готово\", либо введите команду /ready\n",
                response.getText());
    }

    @Test
    void handleTestWithoutArguments() {
        BotUserDto user = testHelper.createUser();
        when(service.getUser(1L)).thenReturn(user);

        IncomingMessageDto message = new IncomingMessageDto();
        message.setCommandName("/notification");
        message.setArguments(List.of());
        message.setCallback(false);
        message.setUserId(1L);
        message.setChatId(1L);
        CommandResponse response = notifyCommand.handle(message);
        assertTrue(response.getText().contains("Неправильно введены данные. Вот пример для установки фильтра: " +
                "/notify 7 15 (разделитель пробел)"));
        assertNull(user.getFilter().getNotificationTime());
        verify(service, never()).saveUser(any());
    }

    static Stream<Arguments> commandData() {
        return Stream.of(Arguments.of(List.of("7", "15"), LocalTime.of(7, 15)),
                Arguments.of(List.of("23", "40"), LocalTime.of(23, 40)),
                Arguments.of(List.of("00", "00"), LocalTime.of(0, 0)),
                Arguments.of(List.of("12", "10"), LocalTime.of(12, 10)));
    }
}
