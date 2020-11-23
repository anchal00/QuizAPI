package com.crio.buildout.dto;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionResponseDto {
    private String questionId;
    private String title;
    private String description;
    private String type;
    private Map<String, String> options;
    private List<String> userAnswer;
    private List<String> correct;
    private String explanation;
    private boolean answerCorrect;
}
