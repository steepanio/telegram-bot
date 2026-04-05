package ru.stepan.trudvsem.client.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ResourceBundle;

@Data
@AllArgsConstructor
public class TrudVsemClientProperties {
    private String baseUrl;

    private TrudVsemClientProperties() {
        ResourceBundle bundle = ResourceBundle.getBundle("trudvsem.properties");
        this.baseUrl = bundle.getString("trudvsem.base-url");
    }
}
