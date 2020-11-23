package com.crio.buildout.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crio.buildout.BuildoutApplication;
import com.crio.buildout.dto.Question;
import com.crio.buildout.dto.SubmitQuestionRequestDto;
import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repositoryservice.QnARepositoryService;
import com.crio.buildout.service.QnAservice;
import com.crio.buildout.service.QnAserviceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@SpringBootTest(classes = {BuildoutApplication.class})
@ExtendWith(MockitoExtension.class)
public class QnAservicetest {
    
    @InjectMocks 
    private QnAservice qnAservice = new QnAserviceImpl();

    @MockBean
    private QnARepositoryService qnARepositoryService;

    private ObjectMapper mapper;

    @BeforeEach
    public void init() {
        MockitoAnnotations.initMocks(this);
        mapper = new ObjectMapper();
    }

    public List<Question> loadQuestionList() throws Exception {
        File file = new File(System.getProperty("user.dir") + "/../initial_data_load.json");
        List<Question> list = mapper.readValue(file, new TypeReference<List<Question>>(){});
        return list;
    }
    
    public List<QuestionEntity> getQuestionsEntities() {
        QuestionEntity question1 = new QuestionEntity("1", "001", "What is the parent class/interface of Exception class?",
            "java Question", "Subjective", null, Arrays.asList("throwable"), null);
        HashMap<String, String> options = new HashMap<>();
        options.put("1", "0.0.0.0");
        options.put("2", "192.168.1.0");
        options.put("3", "127.0.0.1");
        options.put("4", "255.255.255.255");
        
        QuestionEntity question2 = new QuestionEntity("1", "002", "What is the default IP address of localhost?", "General Question",
            "Objective-single", options, Arrays.asList("3"), null);
            
        List<QuestionEntity> list = new ArrayList<>();
        list.add(question1);
        list.add(question2);
        return list;
    }

    public Map<String, QuestionEntity> getAllEntitiesMap() {
        
        List<QuestionEntity> qEntityList = getQuestionsEntities();
        Map<String, QuestionEntity> ansMap = new HashMap<>();

        for (QuestionEntity entity : qEntityList) {
            
            ansMap.put(entity.getQuestionId(), entity);
            
        }
        return  ansMap;
    }
    
    @Test
    public void properModuleIdGetQuestionSet() throws Exception { 
        List<Question> response = loadQuestionList();
        assertNotNull(response);
        when(qnARepositoryService.getQuestions("1")).thenReturn(response);

        GetQuestionResponse responseObject = qnAservice.getQuestionSet("1");

        verify(qnARepositoryService).getQuestions("1");

        assertNotNull(responseObject);
        assertTrue(responseObject.getQuestions().size() > 0);
    
    }

    @Test
    public void incompleteyAttemptedQuestionsStillGivesResponse() {
        SubmitQuestionRequest request = new SubmitQuestionRequest();
        SubmitQuestionRequestDto dto1 = new SubmitQuestionRequestDto("001", List.of("1"));
        
        request.setResponses(new ArrayList<SubmitQuestionRequestDto>(){
            {
                add(dto1);
            }
        });

        Map<String, QuestionEntity> map = getAllEntitiesMap();

        when(qnARepositoryService.getAllEntitiesMap("1")).thenReturn(map);

        SubmitQuestionResponse response = qnAservice.checkSubmittedAnswers(request, "1");
        assertNotNull(response);
    }

    @Test
    public void answersSubmittedAreValidatedCorrectly() {
        SubmitQuestionRequest request = new SubmitQuestionRequest();

        SubmitQuestionRequestDto dto1 = new SubmitQuestionRequestDto("001", new ArrayList<String>(){{add("1");}});
        SubmitQuestionRequestDto dto2 = new SubmitQuestionRequestDto("002", new ArrayList<String>(){{add("3");}});
        

        request.setResponses(new ArrayList<SubmitQuestionRequestDto>(){
            {
                add(dto1);
                add(dto2);
            }
        });

        Map<String, QuestionEntity> map = getAllEntitiesMap();

        when(qnARepositoryService.getAllEntitiesMap("1")).thenReturn(map);
        SubmitQuestionResponse response = qnAservice.checkSubmittedAnswers(request, "1");

        assertNotNull(response);
        assertFalse(response.getQuestions().get(0).isAnswerCorrect());
        assertTrue(response.getQuestions().get(1).isAnswerCorrect());

    }

    @Test
    public void unAttemptedQuestionsAreEvaluatedToFalse(){
        SubmitQuestionRequest request = new SubmitQuestionRequest();

        Map<String, QuestionEntity> map = getAllEntitiesMap();

        when(qnARepositoryService.getAllEntitiesMap("1")).thenReturn(map);
        SubmitQuestionResponse response = qnAservice.checkSubmittedAnswers(request, "1");

        assertNotNull(response);
        assertFalse(response.getQuestions().get(0).isAnswerCorrect());
        assertFalse(response.getQuestions().get(1).isAnswerCorrect());

    }

    @Test
    public void invalidModuleReturnsEmptyResponse() {

        assertTrue(qnAservice.getQuestionSet("test").getQuestions().isEmpty());
  }
}
