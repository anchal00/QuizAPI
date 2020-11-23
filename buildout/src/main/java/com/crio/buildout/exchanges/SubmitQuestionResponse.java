package com.crio.buildout.exchanges;

import com.crio.buildout.dto.SubmitQuestionResponseDto;
import com.crio.buildout.dto.Summary;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionResponse {
    private List<SubmitQuestionResponseDto> questions = new ArrayList<>();
    private Summary summary;
}
