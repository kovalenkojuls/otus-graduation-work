package ru.kovalenkojuls.mailsender.domains.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.Map;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class EmailDTO {
    private String emailTo;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
}

