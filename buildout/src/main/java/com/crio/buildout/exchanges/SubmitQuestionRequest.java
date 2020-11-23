package com.crio.buildout.exchanges;

import java.util.List;

import com.crio.buildout.dto.SubmitQuestionRequestDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionRequest {
    private List<SubmitQuestionRequestDto> responses;
}
