package com.crio.buildout.exchanges;

import java.util.ArrayList;
import java.util.List;

import com.crio.buildout.dto.Question;
import com.crio.buildout.dto.SubmitQuestionResponseDto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubmitQuestionResponse {
    private List<SubmitQuestionResponseDto> responsedto = new ArrayList<>();
}
