package com.stepanio.java.training.telegram.app.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class ApiResponseDto {
    private Results results;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Results {
        private List<VacancyWrapper> vacancies;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VacancyWrapper {
        private Vacancy vacancy;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Vacancy {
        private String id;
        @JsonProperty("date_modify")
        private String dateModify;
        private String salary;
        @JsonProperty("salary_min")
        private String salaryMin;
        @JsonProperty("salary_max")
        private String salaryMax;
        private Region region;
        private Company company;
        @JsonProperty("vac_url")
        private String url;
        @JsonProperty("job-name")
        private String jobName;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private String name;
    }
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Region {
        @JsonProperty("region_code")
        private String regionCode;
        private String name;
    }
}
