package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VacancyDto {
    private String company;
    private int salaryMin;
    private int salaryMax;
    private int experience;
    private String url;
}
