package ru.kovalenkojuls.cookhub.domains.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;
import java.util.Set;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class RecaptchaResponseDto {
    private boolean success;

    @JsonAlias("error-codes")
    private Set<String> errorCodes;
}


