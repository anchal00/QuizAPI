package com.crio.buildout.exchanges;

import java.util.ArrayList;
import java.util.List;

import com.crio.buildout.dto.Question;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetQuestionResponse {
    private List<Question> questions = new ArrayList<>();
}
