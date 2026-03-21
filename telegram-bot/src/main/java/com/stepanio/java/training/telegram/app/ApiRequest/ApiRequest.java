package com.stepanio.java.training.telegram.app.ApiRequest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stepanio.java.training.telegram.app.dto.ApiResponseDto;
import com.stepanio.java.training.telegram.app.dto.SearchFilter;
import com.stepanio.java.training.telegram.app.exceptions.DirectoryCreateException;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ApiRequest {
    private static String BASE_URL = "http://opendata.trudvsem.ru/api/v1/vacancies";
    private final File vacanciesDirectory = new File("vacancies");
    private final File vacanciesFile = new File(vacanciesDirectory, "vacancies.json");

    private final HttpClient httpClient;
    private final ObjectMapper mapper;

    public ApiRequest(HttpClient httpClient, ObjectMapper mapper) {
        this.httpClient = httpClient;
        this.mapper = mapper;
        if (!this.vacanciesDirectory.exists()) {
            boolean created = this.vacanciesDirectory.mkdirs();
            if (!created) {
                throw new DirectoryCreateException("Папка с пользователями не создана");
            }
        }
    }

    private void httpRequest(SearchFilter filter) {
        String fullUrl = buildFullUrl(filter);
        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(fullUrl))
                .GET().build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            ApiResponseDto apiResponseDto = mapper.readValue(response.body(), ApiResponseDto.class);
            List<ApiResponseDto.Vacancy> listVacancies =
                    apiResponseDto.getResults().getVacancies().stream()
                            .map(ApiResponseDto.VacancyWrapper::getVacancy)
                            .filter(v -> v.getSalary() != null)
                            .filter(v -> v.getSalary().equals(filter.getSalary()))
                            .collect(Collectors.toList());
            mapper.writerWithDefaultPrettyPrinter().writeValue(vacanciesFile, listVacancies);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    private static String buildParams(SearchFilter filter) {
        return Stream.of(
                        new String[]{"text", filter.getText()},
                        new String[]{"experience", filter.getExperience()},
                        new String[]{"modifiedFrom", filter.getModifiedFrom()}
                )
                .filter(e -> e[1] != null && !e[1].isEmpty())
                .map(e -> e[0] + "=" + e[1])
                .collect(Collectors.joining("&"));
    }

    private static String buildFullUrl(SearchFilter filter) {
        String params = buildParams(filter);
        String regionPart = filter.getRegion() != null ? "/region/" + filter.getRegion() : "";
        return BASE_URL + regionPart + (params.isEmpty() ? "" : "?" + params);
    }
}



