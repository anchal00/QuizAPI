package com.crio.buildout.dto;

import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.crio.buildout.BuildoutApplication;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.boot.test.context.SpringBootTest;

public class QuestionTest {
    
    String json = 
        "{\"questionId\":\"001\",\"title\":\"What is the default IP address of localhost?\","
        +"\"description\":\"General question\",\"type\":\"objective-single\","
        + "\"options\":{\"1\":\"0.0.0.0\",\"2\":\"192.168.0.12\",\"3\":\"127.0.0.1\","
        + "\"4\":\"255.255.255.255\"},"   
        + "\"correctAnswer\":"
        + "[\"4\"]"
        + "}";
    String questionFormat = 
        "{\"questionId\":\"001\",\"title\":\"What is the default IP address of localhost?\","
        +"\"type\":\"objective-single\","
        + "\"options\":{\"1\":\"0.0.0.0\",\"2\":\"192.168.0.12\",\"3\":\"127.0.0.1\","
        + "\"4\":\"255.255.255.255\"}"   
        + "}";
    
    @Test            
    public void serializationAnddeserializationTest() throws Exception{
        ObjectMapper mapper = new ObjectMapper();
        Question question = mapper.readValue(json, Question.class);
        
        assertNotNull(question);
        assertEquals("001", question.getQuestionId());
        assertEquals("objective-single", question.getType());

        String content = mapper.writeValueAsString(question);

        JSONAssert.assertEquals(questionFormat, content, false);

    }

}
