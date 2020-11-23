package com.crio.buildout.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.repository.QnArepository;
import com.crio.buildout.service.QnAservice;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/quiz")
public class BuildoutController {

    @Autowired
    QnAservice qnAservice;

    @Autowired
    QnArepository repo;

    @RequestMapping(value = "/{moduleId}", method = RequestMethod.GET)
    public ResponseEntity<GetQuestionResponse> getQuestions(@PathVariable String moduleId, HttpServletRequest request) {

        GetQuestionResponse obj = qnAservice.getQuestionSet(moduleId);
        if (obj.getQuestions().size() == 0) {
            return new ResponseEntity<GetQuestionResponse>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<GetQuestionResponse>(obj, HttpStatus.OK);
    }

    @RequestMapping(value = "/{moduleId}", method = RequestMethod.POST)
    public ResponseEntity<SubmitQuestionResponse> submitAnswers(@PathVariable String moduleId,
            @RequestBody String requestContent) {

        if (requestContent == null) {
            return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.BAD_REQUEST);
        }
        SubmitQuestionRequest submittedDataByUser = null;
        try {
            submittedDataByUser = new ObjectMapper().readValue(requestContent,
                SubmitQuestionRequest.class);
            
        } catch (Exception e) {
            return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.BAD_REQUEST);
        }
        
        SubmitQuestionResponse response = qnAservice.checkSubmittedAnswers(submittedDataByUser, moduleId);

        if(response == null) {
            return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.BAD_REQUEST);
        }
        if (response.getQuestions().size() == 0) {
            return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.NOT_FOUND);
        }
        
        return new ResponseEntity<SubmitQuestionResponse>(response, HttpStatus.OK);
        
    }
       
}
