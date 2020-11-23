package com.crio.buildout.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crio.buildout.BuildoutApplication;
import com.crio.buildout.dto.Question;
import com.crio.buildout.dto.SubmitQuestionRequestDto;
import com.crio.buildout.dto.SubmitQuestionResponseDto;
import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.service.QnAservice;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.assertj.core.util.Arrays;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
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
@ExtendWith(MockitoExtension.class)
public class BuildoutControllerTest {
    
    @InjectMocks
    private BuildoutController buildoutController;

    @Mock
    private QnAservice qnAservice;

    private static final String QnA_API_ENDPOINT = "/quiz";

    private ObjectMapper mapper;

    private MockMvc mvc;

    private static SubmitQuestionRequestDto response1;
    private static SubmitQuestionRequestDto response2;
    private static SubmitQuestionRequestDto response3;
    private static SubmitQuestionRequestDto response4;
    
    void loadResponses() {
        ArrayList<String> list1 = new ArrayList<>(){
            {
                add("4");
            }
        };
        ArrayList<String> list2 = new ArrayList<>(){
            {
                add("1");
                add("3");
                add("4");
            }
        };
        ArrayList<String> list3 = new ArrayList<>(){
            {
                add("throwable");
            }
        };
        ArrayList<String> list4 = new ArrayList<>(){
            {
                add("throw");
            }
        };
        response1 = new SubmitQuestionRequestDto("001", list1);
        response2 = new SubmitQuestionRequestDto("002", list2);
        response3 = new SubmitQuestionRequestDto("003", list3);
        response4 = new SubmitQuestionRequestDto("003", list4);
    }      

    public GetQuestionResponse loadQuestions() throws Exception {
        File file = new File(System.getProperty("user.dir") + "/../initial_data_load.json");

        List<Question> list = mapper.readValue(file, new TypeReference<List<Question>>(){});

        var response = new GetQuestionResponse();
        response.setQuestions(list);
        return response;
    }

    @Before
    public void setup() {
        mapper = new ObjectMapper();

        MockitoAnnotations.initMocks(this);

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
        when(qnAservice.getQuestionSet("1")).thenReturn(responseObject);

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
        dto.setUserResponse(new ArrayList<String>(){
            {
                add("dummyAnswer");
            }
        });
        test.setResponses(List.of(dto));

        String content = new ObjectMapper().writeValueAsString(test);

        SubmitQuestionResponseDto response = new SubmitQuestionResponseDto();

        var obj = new SubmitQuestionResponse();
        obj.setQuestions(new ArrayList<SubmitQuestionResponseDto>(){
            {
                add(response);
            }
        });

        when(qnAservice.checkSubmittedAnswers(test, "1"))
            .thenReturn(obj);
        
        URI uri = UriComponentsBuilder.fromPath(QnA_API_ENDPOINT + "/1").build().toUri();
        MockHttpServletResponse httpresponse = mvc.perform(post(uri).content(content)).andReturn().getResponse();
        System.out.println(content);
        assertEquals(HttpStatus.OK.value(), httpresponse.getStatus());
    }

}
