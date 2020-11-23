package com.crio.buildout.repositoryservices;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import com.crio.buildout.BuildoutApplication;
import com.crio.buildout.dto.Question;
import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repository.QnArepository;
import com.crio.buildout.repositoryservice.QnARepositoryService;
import com.crio.buildout.repositoryservice.QnARepositoryServiceimpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest(classes = {BuildoutApplication.class})
@RunWith(SpringRunner.class) 
public class QnARepositoryServiceTest {
    
    @TestConfiguration
    static class QnARepositoryServiceTestContextConfiguration {
	 
        @Bean
        public QnARepositoryService employeeService() {
            return new QnARepositoryServiceimpl();
        }
    }

    @Autowired
    QnARepositoryService qnARepositoryService;
    
    @MockBean
    QnArepository qnArepository;

    @BeforeEach
    public void setup() {
        List<QuestionEntity> list = getQuestionsEntities();
        when(qnArepository.findAllByModuleId("1")).thenReturn(list);
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

    @Test
    public void correctModuleIdGetsListofQuestions() {

        List<QuestionEntity> listdummy = getQuestionsEntities();
        when(qnArepository.findAllByModuleId("1")).thenReturn(listdummy);

        List<Question> list = qnARepositoryService.getQuestions("1");

        verify(qnArepository).findAllByModuleId("1");
        assertNotNull(list);

        assertEquals(2, list.size());
        assertEquals("001", list.get(0).getQuestionId());
        assertEquals("002", list.get(1).getQuestionId());
    }

    @Test
    public void missingOrwrongModuleIdReturnsEmptyResponse() {
        List<Question> list = qnARepositoryService.getQuestions(" ");
        assertEquals(0, list.size());

        List<Question> list1 = qnARepositoryService.getQuestions("3");
        assertEquals(0, list1.size());

        List<Question> list2 = qnARepositoryService.getQuestions("test");
        assertEquals(0, list2.size());
    }

    @Test
    public void correctModuleIdGetsMapofQuestionEntities() {
        List<QuestionEntity> listdummy = getQuestionsEntities();
        when(qnArepository.findAllByModuleId("1")).thenReturn(listdummy);

        Map<String, QuestionEntity> map = qnARepositoryService
            .getAllEntitiesMap("1");

        verify(qnArepository).findAllByModuleId("1");

        assertNotNull(map);
        assertEquals(2, map.size());            
        assertEquals(listdummy.get(0), map.get("001"));
    }

}
