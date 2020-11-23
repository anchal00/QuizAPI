package com.crio.buildout.dto;

import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import org.springframework.data.mongodb.core.mapping.Field;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    private String questionId;
    private String title;
    private String type;
    private Map<String, String> options;
}
