package com.crio.buildout.service;

import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;

import org.springframework.stereotype.Service;

public interface QnAservice {
    
  public GetQuestionResponse getQuestionSet(String moduleId);

  public SubmitQuestionResponse checkSubmittedAnswers(SubmitQuestionRequest data, String module);

}
