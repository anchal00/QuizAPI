package com.crio.buildout.controller;

import com.crio.buildout.exchanges.GetQuestionResponse;
import com.crio.buildout.exchanges.SubmitQuestionRequest;
import com.crio.buildout.exchanges.SubmitQuestionResponse;
import com.crio.buildout.repository.QnArepository;
import com.crio.buildout.service.QnAservice;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
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

  @GetMapping("/{moduleId}")
  public ResponseEntity<GetQuestionResponse> getQuestions(
        @PathVariable String moduleId) {

    GetQuestionResponse obj = qnAservice.getQuestionSet(moduleId);
    if (obj.getQuestions().size() == 0) {
      return new ResponseEntity<GetQuestionResponse>(HttpStatus.NOT_FOUND);
    }
    return new ResponseEntity<GetQuestionResponse>(obj, HttpStatus.OK);
  }

  @PostMapping("/{moduleId}")
  public ResponseEntity<SubmitQuestionResponse> submitAnswers(
        @PathVariable String moduleId,
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
        
    SubmitQuestionResponse response = qnAservice
        .checkSubmittedAnswers(submittedDataByUser, moduleId);
    if (response == null) {
      return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.BAD_REQUEST);
    }
    if (response.getQuestions().size() == 0) {
      return new ResponseEntity<SubmitQuestionResponse>(HttpStatus.NOT_FOUND);
    }    
    return new ResponseEntity<SubmitQuestionResponse>(response, HttpStatus.OK);     
  }         
}
