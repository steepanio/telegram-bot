package ru.stepan.trudvsem.client.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.stepan.trudvsem.client.config.TrudVsemClientProperties;
import ru.stepan.trudvsem.client.dto.request.VacancySearchRequest;
import ru.stepan.trudvsem.client.dto.response.VacancySearchResponse;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class TrudVsemRestClientTest {
    private HttpClient httpClient;
    private HttpResponse httpResponse;
    private ObjectMapper mapper;
    private TrudVsemClientProperties properties;
    private VacancySearchResponse expectedResponse;
    private TrudVsemRestClient trudVsemRestClient;

    @BeforeEach
    void setUp () {
        httpClient = mock(HttpClient.class);
        httpResponse = mock(HttpResponse.class);
        mapper = mock(ObjectMapper.class);
        properties = mock(TrudVsemClientProperties.class);
        expectedResponse = mock(VacancySearchResponse.class);
        trudVsemRestClient = new TrudVsemRestClient(httpClient, mapper, properties);
    }

    @Test
    void SearchVacanciesTest () throws IOException, InterruptedException {
        when(properties.getBaseUrl()).thenReturn("http://test");
        when(httpClient.send(any(), any())).thenReturn(httpResponse);
        when(httpResponse.body()).thenReturn("{json}");
        when(mapper.readValue(anyString(), any(Class.class))).thenReturn(expectedResponse);

        VacancySearchResponse result = trudVsemRestClient.searchVacancies(new VacancySearchRequest());

        verify(httpClient).send(any(), any());
        assertEquals(expectedResponse, result);
    }
}
