package ru.stepan.trudvsem.client.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class VacancyDto {
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


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Company {
        private String name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Region {
        @JsonProperty("region_code")
        private String regionCode;
        private String name;
    }
}

