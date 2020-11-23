package com.crio.buildout.repositoryservice;

import java.util.List;
import java.util.Map;

import com.crio.buildout.dto.Question;
import com.crio.buildout.models.QuestionEntity;

public interface QnARepositoryService {
    
    List<Question> getQuestions(String moduleId);
    Map<String, QuestionEntity> getAllEntitiesMap(String moduleId);
    void clearDb();
    void populateDatabase(List<QuestionEntity> listToLoad);
}
