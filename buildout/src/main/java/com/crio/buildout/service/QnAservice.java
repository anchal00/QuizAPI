package com.crio.buildout.service;

import java.util.List;

import com.crio.buildout.dto.Question;
import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;

import org.springframework.stereotype.Service;

public interface QnAservice {
    
    GetQuestionResponse getQuestionSet(String moduleId);

    SubmitQuestionResponse checkSubmittedAnswers(SubmitQuestionRequest data, String module);

}
