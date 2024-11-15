package ru.kovalenkojuls.cookhub.domains.dto;


import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import java.util.Map;

@AllArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class EmailDto {
    private String emailTo;
    private String subject;
    private String templateName;
    private Map<String, Object> templateVariables;
}
