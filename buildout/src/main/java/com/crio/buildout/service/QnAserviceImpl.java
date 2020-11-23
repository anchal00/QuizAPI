package com.crio.buildout.service;

import com.crio.buildout.dto.Question;
import com.crio.buildout.dto.SubmitQuestionRequestDto;
import com.crio.buildout.dto.SubmitQuestionResponseDto;
import com.crio.buildout.dto.Summary;
import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repositoryservice.QnARepositoryService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class QnAserviceImpl implements QnAservice {

  @Autowired
  QnARepositoryService qnaRepository;

  @Override
  public GetQuestionResponse getQuestionSet(String moduleId) {

    if (moduleId.isEmpty()) {
      return null;
    }
    List<Question> list = qnaRepository.getQuestions(moduleId);
    GetQuestionResponse responseObject = new GetQuestionResponse();
    responseObject.setQuestions(list);
    return responseObject;
  }

  public boolean validateAnswers(List<String> submitList, 
      List<String> actualAnswers) {
    if (submitList == null || submitList.size() == 0) {
      return false;
    }
    Set<String> answers = new HashSet<>();
    if (submitList.size() != actualAnswers.size()) {
      return false;
    }
    for (String each : actualAnswers) {
      answers.add(each);
    }
    for (String each : submitList) {
      if (!answers.contains(each)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public SubmitQuestionResponse checkSubmittedAnswers(
      SubmitQuestionRequest data, String module) {        
    
    SubmitQuestionResponse response = new SubmitQuestionResponse();
    List<SubmitQuestionRequestDto> list = data.getResponses();
    Map<String, QuestionEntity> qentityMap = qnaRepository.getAllEntitiesMap(module);
    ArrayList<QuestionEntity> qentitylist = new ArrayList<>(qentityMap.values());

    Map<String, Boolean> results = new HashMap<>();
    Map<String, List<String>> givenAnswers = new HashMap<>();

    int totalQuestions = qentityMap.size();
    int attempted = 0;
    int score = 0;
    int total = totalQuestions;

    if (list != null && list.size() > 0) {
      for (SubmitQuestionRequestDto each : list) {  
        List<String> allAnswers = qentityMap.get(each.getQuestionId()).getCorrectAnswer();
        List<String> submittedAnswers = each.getUserResponse();
            
        boolean isCorrect = validateAnswers(submittedAnswers, allAnswers);        
        if (isCorrect) {
          score++;
        }
        givenAnswers.put(each.getQuestionId(), submittedAnswers);
        results.put(each.getQuestionId(), isCorrect);
        ++attempted;
      }
    } 
    //unattempted questions are evaluated to False by default
    while (attempted != totalQuestions) {
      results.put(qentitylist.get(attempted).getQuestionId(), false);
      ++attempted;
    }
    List<SubmitQuestionResponseDto> listOfEvaluatedResponses = makeResponseDtoList(results,
        qentityMap, givenAnswers);

    Summary summary = new Summary(score, total);
    response.setQuestions(listOfEvaluatedResponses);
    response.setSummary(summary);
    return response;
  }    

  public List<SubmitQuestionResponseDto> makeResponseDtoList(
        Map<String, Boolean> results, 
        Map<String, QuestionEntity> qentityMap,
        Map<String, List<String>> givenAnswers) {
        
    List<SubmitQuestionResponseDto> listOfEvaluatedResponses = new ArrayList<>();
    for (Map.Entry<String, Boolean> e : results.entrySet()) {
      SubmitQuestionResponseDto dto = new SubmitQuestionResponseDto();
      dto.setAnswerCorrect(e.getValue());
      dto.setCorrect(qentityMap.get(e.getKey()).getCorrectAnswer());
      dto.setOptions(qentityMap.get(e.getKey()).getOptions());
      dto.setTitle(qentityMap.get(e.getKey()).getTitle());
      dto.setExplanation(null);
      dto.setDescription(qentityMap.get(e.getKey()).getDescription());
      dto.setQuestionId(e.getKey());
      dto.setUserAnswer(givenAnswers.get(e.getKey()));
      dto.setType(qentityMap.get(e.getKey()).getType());
      listOfEvaluatedResponses.add(dto);
    }
    return listOfEvaluatedResponses;
  }
}
