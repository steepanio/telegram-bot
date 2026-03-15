package ru.stepan.trudvsem.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.stepan.trudvsem.client.config.TrudVsemClientProperties;
import ru.stepan.trudvsem.client.dto.request.VacancySearchRequest;
import ru.stepan.trudvsem.client.dto.response.VacancySearchResponse;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class TrudVsemRestClient {
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final TrudVsemClientProperties properties;

    public TrudVsemRestClient(HttpClient httpClient, ObjectMapper objectMapper, TrudVsemClientProperties properties) {
        this.httpClient = httpClient;
        this.objectMapper = objectMapper;
        this.properties = properties;
    }

    private VacancySearchResponse searchVacancies(VacancySearchRequest request) {
        String baseUrl = properties.getBaseUrl();
        if (isNotEmpty(request.getRegion())) {
            baseUrl = baseUrl + "/region/" + request.getRegion();
        }
        List<String> queryParams = new ArrayList<>();
        if (isNotEmpty(request.getExperience())) {
            queryParams.add("experience=" + request.getExperience());
        }
        if (isNotEmpty(request.getModifiedFrom())) {
            queryParams.add("modifiedFrom=" + request.getModifiedFrom());
        }
        if (isNotEmpty(request.getLimit())) {
            queryParams.add("limit=" + request.getLimit());
        }
        if (isNotEmpty(request.getText())) {
            queryParams.add("text=" + request.getText());
        }
        if (isNotEmpty(request.getOffset())) {
            queryParams.add("offset=" + request.getOffset());
        }
        String params = String.join("&", queryParams);
        if (!queryParams.isEmpty()) {
            baseUrl += "?" + params;
        }

        try {
            HttpRequest httpRequest = HttpRequest.newBuilder().uri(new URI(baseUrl))
                    .GET().build();
            HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            return objectMapper.readValue(httpResponse.body(), VacancySearchResponse.class);

        } catch (URISyntaxException | InterruptedException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static boolean isNotEmpty(String value) {
        return value != null && !value.isEmpty();
    }
}
