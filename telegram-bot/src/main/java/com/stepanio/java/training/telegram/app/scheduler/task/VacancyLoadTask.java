package com.stepanio.java.training.telegram.app.scheduler.task;

import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.VacancyDto;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.JsonUserRepository;
import com.stepanio.java.training.telegram.app.repository.JsonVacancyRepository;
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


    public VacancyLoadTask(Long userId, JsonUserRepository jsonUserRepository, JsonVacancyRepository jsonVacancyRepository, TrudVsemRestClient trudVsemRestClient, MessageSenderImpl sender) {
        this.userId = userId;
        this.jsonUserRepository = jsonUserRepository;
        this.jsonVacancyRepository = jsonVacancyRepository;
        this.trudVsemRestClient = trudVsemRestClient;
        this.sender = sender;
    }

    @Override
    public Long getId() {
        return userId;
    }

    @Override
    public void run() {
        BotUserDto user = jsonUserRepository.findById(getId()).orElseThrow();
        if (wasTaskDoneYesterday(user)) {
            user.setOffset(user.getOffset() + 10);
        } else {
            user.setOffset(0);
        }
        VacancySearchRequest vacancySearchRequest = new VacancySearchRequest();
        vacancySearchRequest.setRegion(user.getFilter().getRegion());
        vacancySearchRequest.setOffset(String.valueOf(user.getFilter().getOffset()));
        vacancySearchRequest.setText(user.getFilter().getKeyword());
        vacancySearchRequest.setLimit("30");
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
        if (vacancyList.size() > 10) {
            int size = 10;
            List<VacancyDto> vacancyDtoList = vacancyList.subList(0, Math.min(size, vacancyList.size()));
            StringBuilder message = new StringBuilder();
            for (VacancyDto vacancy : vacancyDtoList) {
                message.append(vacancy.toString()).append("\n\n");
            }
            sender.sendText(user.getChatId(), message.toString());
        }
    }

    private boolean wasTaskDoneYesterday(BotUserDto user) {
        LocalDate yesterday = user.getLastExecutionDate().minusDays(1);
        return user.getLastExecutionDate() != null && user.getLastExecutionDate().equals(yesterday);
    }
}

