package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilter {
    private String keyword;
    private String region;
    private Integer salaryMin;
    private Integer experience;
    private Integer limit;
    private Integer offset;
    private LocalTime notificationTime;
}
