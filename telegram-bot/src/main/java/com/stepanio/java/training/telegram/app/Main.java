package com.stepanio.java.training.telegram.app;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.adapter.TelegramIncomingMessageAdapter;
import com.stepanio.java.training.telegram.app.app.Application;
import com.stepanio.java.training.telegram.app.command.*;
import com.stepanio.java.training.telegram.app.command.dispatcher.CommandDispatcher;
import com.stepanio.java.training.telegram.app.command.parser.CommandLineParser;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.CsvRegionsRepository;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import com.stepanio.java.training.telegram.app.scheduler.TaskScheduler;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;
import ru.stepan.trudvsem.client.config.TrudVsemClientProperties;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.io.File;
import java.net.http.HttpClient;
import java.util.List;

public class Main {

    public static void main(String[] args) throws TelegramApiException {
        File usersDirectory = new File("data/users");
        File vacanciesDirectory = new File("data/vacancies");
        ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        HttpClient httpClient = HttpClient.newHttpClient();
        TrudVsemClientProperties properties = new TrudVsemClientProperties("trudvsem.properties");

        String botToken = System.getenv("BOT_TOKEN");
        if (botToken == null || botToken.isBlank()) {
            throw new IllegalStateException("BOT_TOKEN is not set");
        }
        TelegramClient client = new OkHttpTelegramClient(botToken);
        MessageSenderImpl sender = new MessageSenderImpl(client);

        TelegramBotsLongPollingApplication telegramApp =
                new TelegramBotsLongPollingApplication();

        CommandLineParser parser = new CommandLineParser();
        TelegramIncomingMessageAdapter adapter = new TelegramIncomingMessageAdapter(parser);

        JsonUserRepository jsonUserRepository = new JsonUserRepository(mapper, usersDirectory);
        JsonVacancyRepository jsonVacancyRepository = new JsonVacancyRepository(mapper, vacanciesDirectory);
        RepositoryService repositoryService = new RepositoryService(jsonUserRepository, jsonVacancyRepository);
        CsvRegionsRepository csvRegionsRepository = new CsvRegionsRepository();
        TrudVsemRestClient trudClient = new TrudVsemRestClient(httpClient, mapper, properties);
        TaskScheduler scheduler = new TaskScheduler();
        BotCommandHelper helper = new BotCommandHelper();

        List<BotCommand> commands = List.of(
                new StartCommand(repositoryService),
                new StopCommand(scheduler, repositoryService),
                new RegionCommand(csvRegionsRepository, helper, repositoryService),
                new ExperienceCommand(helper, repositoryService),
                new KeywordCommand(helper, repositoryService),
                new MenuCommand(helper, repositoryService),
                new NotifyCommand(helper, repositoryService),
                new ReadyCommand(trudClient, sender, scheduler, repositoryService),
                new TimezoneCommand(repositoryService),
                new SalaryCommand(helper, repositoryService)
        );

        CommandDispatcher dispatcher = new CommandDispatcher(
                commands,
                new UnknownCommand()
        );

        Application app = new Application(
                adapter,
                dispatcher,
                sender,
                telegramApp, botToken
        );
        app.run();
    }
}
