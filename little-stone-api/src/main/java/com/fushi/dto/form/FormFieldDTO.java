package com.fushi.dto.form;

import lombok.Data;

import java.util.List;

@Data
public class FormFieldDTO {
    private String fieldType;
    private String fieldName;
    private String label;
    private String placeholder;
    private Boolean required;
    private String pattern;
    private String defaultValue;
    private Integer minLength;
    private Integer maxLength;
    private Integer min;
    private Integer max;
    private List<SelectOption> options;
    private String validationMessage;

    @Data
    public static class SelectOption {
        private String label;
        private String value;
    }
}