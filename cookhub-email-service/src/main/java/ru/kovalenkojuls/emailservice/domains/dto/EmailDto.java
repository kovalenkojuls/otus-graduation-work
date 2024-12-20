package ru.kovalenkojuls.emailservice.domains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailDto {
    private String emailTo;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
}

