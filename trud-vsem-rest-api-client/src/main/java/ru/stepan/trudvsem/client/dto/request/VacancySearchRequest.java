package ru.stepan.trudvsem.client.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VacancySearchRequest {
    private String text;
    private String region;
    private String salary;
    private String offset;
    private String limit;
    private String experience;
    private String modifiedFrom;
}
