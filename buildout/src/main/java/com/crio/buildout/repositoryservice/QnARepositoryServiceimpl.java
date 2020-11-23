package com.crio.buildout.repositoryservice;

import com.crio.buildout.dto.Question;
import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repository.QnArepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

@Service
public class QnARepositoryServiceimpl implements QnARepositoryService {

  @Autowired
  MongoTemplate mongoTemplate;

  @Autowired
  QnArepository questionrepo;

  private ModelMapper mapper;

  @Override
  public List<Question> getQuestions(String moduleId) {
    List<Question> questionList = new ArrayList<>();
    if (moduleId.isEmpty()) {
      return questionList;
    }
    List<QuestionEntity> qEntityList = questionrepo.findAllByModuleId(moduleId);
    mapper = new ModelMapper();
    for (QuestionEntity entity : qEntityList) {
      questionList.add(mapper.map(entity, Question.class));
    }
    return questionList;
  }

  @Override
  public Map<String, QuestionEntity> getAllEntitiesMap(String moduleId) {
        
    List<QuestionEntity> qEntityList = questionrepo.findAllByModuleId(moduleId);
    Map<String, QuestionEntity> ansMap = new HashMap<>();
    for (QuestionEntity entity : qEntityList) {        
      ansMap.put(entity.getQuestionId(), entity);      
    }
    return  ansMap;
  }
    
  @Override
  public void populateDatabase(List<QuestionEntity> listToLoad) {
    questionrepo.saveAll(listToLoad);
    System.out.println(questionrepo.count() + " questions added");
  }

  @Override
  public void clearDb() {
    questionrepo.deleteAll();
  }
    
}
