package com.crio.buildout.service;

import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;

public interface QnAservice {
    
    GetQuestionResponse getQuestionSet(String moduleId);

    SubmitQuestionResponse checkSubmittedAnswers(SubmitQuestionRequest data, String module);

}
