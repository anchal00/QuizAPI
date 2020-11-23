package com.crio.buildout.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.Map;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class Question {

    private String questionId;
    private String title;
    private String type;
    private Map<String, String> options;
}
