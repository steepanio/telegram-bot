package com.stepanio.java.training.telegram.app.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchFilter {
    private String experience;
    private String salary;
    private String text;
    private String region;
    private String modifiedFrom;
}
