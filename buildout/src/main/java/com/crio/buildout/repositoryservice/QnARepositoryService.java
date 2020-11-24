package com.crio.buildout.repositoryservice;

import com.crio.buildout.dto.Question;
import com.crio.buildout.models.QuestionEntity;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

public interface QnARepositoryService {
    
  public List<Question> getQuestions(String moduleId);

  public Map<String, QuestionEntity> getAllEntitiesMap(String moduleId);

  public void clearDb();
  
  public void populateDatabase(List<QuestionEntity> listToLoad);
}
