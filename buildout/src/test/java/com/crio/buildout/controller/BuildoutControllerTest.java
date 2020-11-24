package com.crio.buildout.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.http.MediaType.APPLICATION_JSON_UTF8;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.crio.buildout.BuildoutApplication;
import com.crio.buildout.dto.Question;
import com.crio.buildout.dto.SubmitQuestionRequestDto;
import com.crio.buildout.dto.SubmitQuestionResponseDto;
import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repositoryservice.QnARepositoryService;
import com.crio.buildout.service.QnAservice;
import com.crio.buildout.service.QnAserviceImpl;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.AutoConfigureJsonTesters;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.util.UriComponentsBuilder;


@SpringBootTest(classes = {BuildoutApplication.class}) 
@AutoConfigureMockMvc
public class BuildoutControllerTest {
    
    @InjectMocks
    private BuildoutController buildoutController;

    @MockBean
    private QnAservice qnAservice;
    private  ObjectMapper mapper;
    private static final String QnA_API_ENDPOINT = "/quiz";

    private MockMvc mvc;

    public GetQuestionResponse loadQuestions() throws Exception {
        File file = new File(System.getProperty("user.dir") + "/../initial_data_load.json");
        if (!file.exists()) {
            return null;
        }
       
        List<Question> list = mapper.readValue(file, new TypeReference<List<Question>>(){});

        GetQuestionResponse response = new GetQuestionResponse();
        response.setQuestions(list);
        return response;
    }

    @BeforeEach
    public void setup() {
        MockitoAnnotations.initMocks(this);
        mapper = new ObjectMapper();
        mvc = MockMvcBuilders.standaloneSetup(buildoutController).build();

    }
    @Test
    public void sendValidGetQuestionsRequestGetAllQuestions() throws Exception {
        
      GetQuestionResponse responseObject = loadQuestions();
      assertNotNull(responseObject);
      when(qnAservice.getQuestionSet("1")).thenReturn(responseObject);

      URI uri = UriComponentsBuilder.fromPath(QnA_API_ENDPOINT + "/" + 1).build().toUri();
      MockHttpServletResponse response = mvc.perform(get(uri.toString())
        .accept(APPLICATION_JSON_UTF8))
        .andReturn().getResponse();

      verify(qnAservice, times(1)).getQuestionSet("1");

      assertEquals(HttpStatus.OK.value(), response.getStatus());
      assertNotNull(response.getContentAsString());
      assertTrue(response.getContentAsString().length() > 0);
    }

    @Test
    public void missingModuleIdResultsInBadHttpRequest() throws Exception {
        URI uri = UriComponentsBuilder
            .fromPath(QnA_API_ENDPOINT)
            .build().toUri();

        assertEquals("/quiz", uri.toString());
        
        MockHttpServletResponse response = mvc.perform(
            get(uri.toString()))
            .andReturn().getResponse();
        
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void incorrectlySpelledEndpointResultsInBadHttpRequest() throws Exception {

        GetQuestionResponse responseObject = loadQuestions();
        assertNotNull(responseObject);
        when(qnAservice.getQuestionSet(any(String.class))).thenReturn(responseObject);

        URI uri = UriComponentsBuilder
            .fromPath("/quiiz" + "/" + 1)
            .build().toUri();

        assertEquals("/quiiz/1", uri.toString());
        
        MockHttpServletResponse response = mvc.perform(
            get(uri.toString()))
            .andReturn().getResponse();
        
        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatus());
    }

    @Test
    public void submitRequestWithNullBodymakesBadRequest() throws Exception {
        URI uri = UriComponentsBuilder.fromPath(QnA_API_ENDPOINT + "/1").build().toUri();
        MockHttpServletResponse response = mvc.perform(post(uri)).andReturn().getResponse();
        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatus());
    }


    @Test
    public void submitRequestWithBodyMakesOkRequest() throws Exception {

        SubmitQuestionRequest test = new SubmitQuestionRequest();
        SubmitQuestionRequestDto dto = new SubmitQuestionRequestDto();

        dto.setQuestionId("test");
        List<String> list1 = new ArrayList<String>();
        list1.add("dummyAnswer");
        dto.setUserResponse(list1);

        List<SubmitQuestionRequestDto> list2 = new ArrayList<SubmitQuestionRequestDto>();
        list2.add(dto);
        test.setResponses(list2);

        String content = new ObjectMapper().writeValueAsString(test);

        SubmitQuestionResponseDto response = new SubmitQuestionResponseDto();

        SubmitQuestionResponse obj = new SubmitQuestionResponse();
        List<SubmitQuestionResponseDto> list3 = new ArrayList<SubmitQuestionResponseDto>();
        list3.add(response);

        obj.setQuestions(list3);

        when(qnAservice.checkSubmittedAnswers(test, "1"))
            .thenReturn(obj);
        
        URI uri = UriComponentsBuilder.fromPath(QnA_API_ENDPOINT + "/1").build().toUri();
        MockHttpServletResponse httpresponse = mvc.perform(post(uri).content(content)).andReturn().getResponse();
        System.out.println(content);
        assertEquals(HttpStatus.OK.value(), httpresponse.getStatus());
    }

}
