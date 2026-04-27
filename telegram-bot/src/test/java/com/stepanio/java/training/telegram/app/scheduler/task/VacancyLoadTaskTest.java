package com.stepanio.java.training.telegram.app.scheduler.task;

import com.stepanio.java.training.telegram.app.TestHelper;
import com.stepanio.java.training.telegram.app.dto.BotUserDto;
import com.stepanio.java.training.telegram.app.dto.IncomingMessageDto;
import com.stepanio.java.training.telegram.app.exception.VacancyLoadTaskException;
import com.stepanio.java.training.telegram.app.messageSender.MessageSenderImpl;
import com.stepanio.java.training.telegram.app.repository.RepositoryService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.stepan.trudvsem.client.dto.response.*;
import ru.stepan.trudvsem.client.http.TrudVsemRestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class VacancyLoadTaskTest {
    private TrudVsemRestClient trudVsemRestClient;
    private RepositoryService repositoryService;
    private MessageSenderImpl messageSender;
    private TestHelper testHelper = new TestHelper();
    private VacancySearchResponse response;
    private BotUserDto user = testHelper.createUser();

    @BeforeEach
    void setUp () {
        trudVsemRestClient = mock(TrudVsemRestClient.class);
        repositoryService = mock(RepositoryService.class);
        messageSender = mock(MessageSenderImpl.class);
        response = mock(VacancySearchResponse.class);
    }

    @Test
    void runTest() {
        when(repositoryService.getUser(1L)).thenReturn(user);
        when(trudVsemRestClient.searchVacancies(any())).thenReturn(response);
        when(response.getResults()).thenReturn(mockResponse().getResults());
        when(repositoryService.getVacancies(1L)).thenReturn(List.of(testHelper.createVacancy()));
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(false);
        message.setUserId(1L);
        message.setChatId(1L);
        VacancyLoadTask task = new VacancyLoadTask(1L, trudVsemRestClient, messageSender, message, repositoryService);

        task.run();

        verify(trudVsemRestClient).searchVacancies(any());
        verify(repositoryService).saveVacancies(any(), any());
        verify(messageSender).sendText(anyLong(), anyString(), any());
        verify(repositoryService).saveUser(user);
    }

    @Test
    void runTestWithOffset () {
        user.setLastExecutionDate(LocalDate.now().minusDays(1));
        user.setOffset(100);
        when(repositoryService.getUser(1L)).thenReturn(user);
        when(trudVsemRestClient.searchVacancies(any())).thenReturn(response);
        when(response.getResults()).thenReturn(mockResponse().getResults());
        when(repositoryService.getVacancies(1L)).thenReturn(List.of(testHelper.createVacancy()));
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        VacancyLoadTask task = new VacancyLoadTask(1L, trudVsemRestClient, messageSender, message, repositoryService);

        task.run();

        verify(trudVsemRestClient).searchVacancies(any());
        verify(repositoryService).saveVacancies(any(), any());
        verify(messageSender).sendText(anyLong(), anyString(), any());
        verify(repositoryService).saveUser(user);
        assertEquals(200, user.getOffset());
        assertEquals(mockResponse().getResults().getVacancies().get(0).getVacancy().getCompany().getName(),
                repositoryService.getVacancies(1L).get(0).getCompany());
    }

    @Test
    void runTestIfOffsetIsZero () {
        user.setLastExecutionDate(LocalDate.now());
        user.setOffset(0);
        when(repositoryService.getUser(1L)).thenReturn(user);
        when(trudVsemRestClient.searchVacancies(any())).thenReturn(response);
        when(response.getResults()).thenReturn(mockResponse().getResults());
        when(repositoryService.getVacancies(1L)).thenReturn(List.of(testHelper.createVacancy()));
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        VacancyLoadTask task = new VacancyLoadTask(1L, trudVsemRestClient, messageSender, message, repositoryService);

        task.run();

        verify(trudVsemRestClient).searchVacancies(any());
        verify(repositoryService).saveVacancies(any(), any());
        verify(messageSender).sendText(anyLong(), anyString(), any());
        verify(repositoryService).saveUser(user);
        assertEquals(0, user.getOffset());
    }

    @Test
    void runTestWithCallbackAndEditMessage () {
        when(repositoryService.getUser(1L)).thenReturn(user);
        when(trudVsemRestClient.searchVacancies(any())).thenReturn(response);
        when(response.getResults()).thenReturn(mockResponse().getResults());
        when(repositoryService.getVacancies(1L)).thenReturn(List.of(testHelper.createVacancy()));
        IncomingMessageDto message = new IncomingMessageDto();
        message.setCallback(true);
        message.setMessageId(123);
        message.setUserId(1L);
        message.setChatId(1L);
        VacancyLoadTask task = new VacancyLoadTask(1L, trudVsemRestClient, messageSender, message, repositoryService);

        task.run();

        verify(trudVsemRestClient).searchVacancies(any());
        verify(repositoryService).saveVacancies(any(), any());
        verify(messageSender).editMessage(anyLong(), anyInt(), anyString(), any());
        verify(messageSender, never()).sendText(anyLong(), anyString(), any());
        verify(repositoryService).saveUser(user);
    }

    @Test
    void runTestWithException() {
        when(repositoryService.getUser(1L)).thenReturn(user);
        when(trudVsemRestClient.searchVacancies(any())).thenThrow(new VacancyLoadTaskException("Не удалось запустить задачу"));
        when(response.getResults()).thenReturn(mockResponse().getResults());
        when(repositoryService.getVacancies(1L)).thenReturn(List.of(testHelper.createVacancy()));
        IncomingMessageDto message = new IncomingMessageDto();
        message.setUserId(1L);
        message.setChatId(1L);
        VacancyLoadTask task = new VacancyLoadTask(1L, trudVsemRestClient, messageSender, message, repositoryService);


        assertThrows(VacancyLoadTaskException.class, task::run);
    }


    private VacancySearchResponse mockResponse() {
        var metaDto = new MetaDto();
        var response = new VacancySearchResponse();
        var results = new ResultsDto();
        var vacanciesWrapper = new VacancyWrapper();
        var vacancy = new VacancyDto();
        var company = new VacancyDto.Company();
        var requirement = new VacancyDto.Requirement();
        var region = new VacancyDto.Region();

        company.setName("Company");
        requirement.setExperience(1);
        vacancy.setCompany(company);
        vacancy.setRequirement(requirement);
        region.setName("Moscow");
        region.setRegionCode("77");
        vacancy.setRegion(region);
        vacancy.setUrl("http://test.com");
        vacancy.setId("1");
        vacancy.setDateModify("01.02.2026");
        vacancy.setSalary("7000");
        vacancy.setSalaryMin(7000);
        vacancy.setSalaryMax(8000);
        vacancy.setJobName("Java");
        metaDto.setLimit("5");
        metaDto.setOffset("100");
        metaDto.setTotal(1000);
        vacanciesWrapper.setVacancy(vacancy);
        results.setVacancies(List.of(vacanciesWrapper));
        response.setResults(results);
        response.setMeta(metaDto);
        return response;
    }
}
