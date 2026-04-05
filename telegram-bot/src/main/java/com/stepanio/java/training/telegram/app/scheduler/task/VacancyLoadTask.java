package com.stepanio.java.training.telegram.app.scheduler.task;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import ru.stepan.trudvsem.client.dto.request.VacancySearchRequest;
import ru.stepan.trudvsem.client.dto.response.VacancySearchResponse;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.time.LocalDate;
import java.util.List;

public class VacancyLoadTask implements IdentifiableTask {
    private final Long userId;
    private final JsonUserRepository jsonUserRepository;
    private final JsonVacancyRepository jsonVacancyRepository;
    private final TrudVsemRestClient trudVsemRestClient;
    private final MessageSenderImpl sender;
    private final IncomingMessageDto incomingMessageDto;
    private static final int PAGE_SIZE = 100;
    private static final String LIMIT = "30";
    private static final String VACANCIES_CALLBACK_DATA = "vacancies_page_";


    public VacancyLoadTask(Long userId, JsonUserRepository jsonUserRepository, JsonVacancyRepository jsonVacancyRepository, TrudVsemRestClient trudVsemRestClient, MessageSenderImpl sender, IncomingMessageDto incomingMessageDto) {
        this.userId = userId;
        this.jsonUserRepository = jsonUserRepository;
        this.jsonVacancyRepository = jsonVacancyRepository;
        this.trudVsemRestClient = trudVsemRestClient;
        this.sender = sender;
        this.incomingMessageDto = incomingMessageDto;
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public void run() {
        BotUserDto user = jsonUserRepository.findById(getId()).orElseThrow();
        if (wasTaskDoneYesterday(user)) {
            user.setOffset(user.getOffset() + PAGE_SIZE);
        } else {
            user.setOffset(0);
        }
        VacancySearchRequest vacancySearchRequest = new VacancySearchRequest();
        vacancySearchRequest.setRegion(user.getFilter().getRegion());
        vacancySearchRequest.setOffset(String.valueOf(user.getFilter().getOffset()));
        vacancySearchRequest.setText(user.getFilter().getKeyword());
        vacancySearchRequest.setLimit(LIMIT);
        vacancySearchRequest.setExperience(String.valueOf(user.getFilter().getExperience()));

        VacancySearchResponse response = trudVsemRestClient.searchVacancies(vacancySearchRequest);

        List<VacancyDto> vacancies = response.getResults().getVacancies().stream().map(vacancyWrapper -> {
            var vacancy = vacancyWrapper.getVacancy();
            VacancyDto vacancyDto = new VacancyDto();
            vacancyDto.setCompany(vacancy.getCompany().getName());
            vacancyDto.setSalaryMin(vacancy.getSalaryMin());
            vacancyDto.setSalaryMax(vacancy.getSalaryMax());
            vacancyDto.setUrl(vacancy.getUrl());
            vacancyDto.setExperience(vacancy.getRequirement().getExperience());

            return vacancyDto;
        }).toList();
        jsonVacancyRepository.save(user, vacancies);
        List<VacancyDto> vacancyList = jsonVacancyRepository.findById(getId());
        loadVacancies(vacancyList, incomingMessageDto);
        user.setLastExecutionDate(LocalDate.now());
        jsonUserRepository.save(user);
    }

    private boolean wasTaskDoneYesterday(BotUserDto user) {
        if (user.getLastExecutionDate() == null) {
            return false;
        }
        return user.getLastExecutionDate().equals(LocalDate.now().minusDays(1));
    }

    private void loadVacancies(List<VacancyDto> vacancies, IncomingMessageDto incomingMessageDto) {
        int pageSize = 10;
        int offset = 0;

        if (incomingMessageDto.getCallbackData() != null &&
                incomingMessageDto.getCallbackData().startsWith(VACANCIES_CALLBACK_DATA)) {

            offset = Integer.parseInt(
                    incomingMessageDto.getCallbackData().replace(VACANCIES_CALLBACK_DATA, "")
            );
        }

        int end = Math.min(offset + pageSize, vacancies.size());
        List<VacancyDto> page = vacancies.subList(offset, end);

        StringBuilder message = new StringBuilder();

        for (VacancyDto vacancy : page) {
            message.append("Компания: ").append(vacancy.getCompany()).append("\n")
                    .append("Зарплата: ").append(vacancy.getSalaryMin())
                    .append("-").append(vacancy.getSalaryMax()).append("\n")
                    .append("Опыт: ").append(vacancy.getExperience()).append("\n")
                    .append("Подробное описание вакансии <a href=\"")
                    .append(vacancy.getUrl())
                    .append("\">здесь</a>\n\n");
        }

        InlineKeyboardMarkup keyboard = null;

        if (vacancies.size() > pageSize) {
            keyboard = createKeyboard(offset, vacancies.size());
        }

        if (incomingMessageDto.isCallback()) {
            sender.editMessage(
                    incomingMessageDto.getChatId(),
                    incomingMessageDto.getMessageId(),
                    message.toString(),
                    keyboard
            );
        } else {
            sender.sendText(
                    incomingMessageDto.getChatId(),
                    message.toString(),
                    keyboard
            );
        }
    }

    private InlineKeyboardMarkup createKeyboard(int offset, int totalSize) {
        int vacanciesPageSize = 10;

        InlineKeyboardRow row = new InlineKeyboardRow();

        if (offset > 0) {
            InlineKeyboardButton prev = new InlineKeyboardButton("Предыдущие 10");
            prev.setCallbackData(VACANCIES_CALLBACK_DATA + (offset - vacanciesPageSize));
            row.add(prev);
        }

        if (offset + vacanciesPageSize < totalSize) {
            InlineKeyboardButton next = new InlineKeyboardButton("Следующие 10");
            next.setCallbackData(VACANCIES_CALLBACK_DATA + (offset + vacanciesPageSize));
            row.add(next);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(List.of(row));

        return markup;
    }
}

