package com.crio.buildout.repositoryservice;

import com.crio.buildout.dto.Question;
import com.crio.buildout.models.QuestionEntity;
import java.util.List;
import java.util.Map;

public interface QnARepositoryService {
    
  List<Question> getQuestions(String moduleId);

  Map<String, QuestionEntity> getAllEntitiesMap(String moduleId);

  void clearDb();
  
  void populateDatabase(List<QuestionEntity> listToLoad);
}
