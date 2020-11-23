// CRIO_SOLUTION_AND_STUB_START_MODULE_RESTAURANTSAPI

/*
 *
 *  * Copyright (c) Crio.Do 2019. All rights reserved
 *
 */

package com.crio.buildouts.integ.test;

import com.crio.buildouts.integ.test.config.AssessmentConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

@TestInstance(Lifecycle.PER_CLASS)
@Log
public class IntegrationTest {

  public static final String BASE_URL = "http://localhost:8081";
  private Map<String, AssessmentConfig> configMap;

  @BeforeAll
  public void readConfigs() throws IOException, URISyntaxException {
    ObjectMapper mapper = new ObjectMapper();
    AssessmentConfig[] assessmentConfigs = mapper
        .readValue(resolveFileAsString("assessments.json"), AssessmentConfig[].class);
    configMap = Stream.of(assessmentConfigs)
        .collect(Collectors.toMap(AssessmentConfig::getName, config -> config));
  }

  private void executeTest(String name) throws Exception {
    AssessmentConfig config = configMap.get(name);
    System.out.println("executing name");
    switch (config.getMethod()) {
      case "GET":
        executeGetRequest(config);
        break;
      case "POST":
        executePost(config);
        break;
      default:
        executePut(config);
        break;
    }
  }


  @Test
  public void executeTests() throws IOException, URISyntaxException {
    ObjectMapper mapper = new ObjectMapper();
    AssessmentConfig[] assessmentConfigs = mapper
        .readValue(resolveFileAsString("assessments.json"), AssessmentConfig[].class);
    List<Exception> result = Stream.of(assessmentConfigs).map(config -> {
      try {
        executeTest(config.getName());
      } catch (Exception ex) {
        ex.printStackTrace();
        return ex;
      }
      return null;
    }).filter(Objects::nonNull)
        .collect(Collectors.toList());
    Assertions.assertTrue(result.isEmpty(), "All test-cases should have passed.");
  }


  public void executePut(AssessmentConfig config) throws Exception {
    URI uri = UriComponentsBuilder
        .fromPath(config.getUrl())
        .build().toUri();

    String content = resolveFileAsString(config.getInput());

    WebClient client = WebClient
        .builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    String response = client
        .put().uri(BASE_URL + uri).bodyValue(content)
        .exchange()
        .block()
        .bodyToMono(String.class).block();

    Assertions.assertNull(response);
  }

  private String resolveFileAsString(String input) throws URISyntaxException, IOException {
    File inputFile = new File(Thread.currentThread().getContextClassLoader()
        .getResource(input).toURI());
    return new String(Files.readAllBytes(inputFile.toPath()), "utf-8");
  }

//  public void executeGet(AssessmentConfig config) throws Exception {
//    URI uri = UriComponentsBuilder
//        .fromPath(config.getUrl())
//        .build().toUri();
//
//    File responseFile = new File(Thread.currentThread().getContextClassLoader()
//        .getResource(config.getResponse()).toURI());
//    String content = new String(Files.readAllBytes(responseFile.toPath()), "utf-8");
//
//    WebClient client = WebClient
//        .builder()
//        .baseUrl(BASE_URL)
//        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
//        .build();
//
//    String response = client
//        .put().uri(BASE_URL + uri).bodyValue(content)
//        .exchange()
//        .block()
//        .bodyToMono(String.class).block();
//
//    System.out.println(response);
//  }


  public void executeGetRequest(AssessmentConfig config) throws Exception {
    URI uri = UriComponentsBuilder
        .fromPath(config.getUrl())
        .build().toUri();

    String content = resolveFileAsString(config.getResponse());

    WebClient client = WebClient
        .builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    String response = client
        .get().uri(BASE_URL + uri)
        .exchange()
        .block()
        .bodyToMono(String.class).block();

    log.warning(response);
    JSONAssert.assertEquals(content, response, false);
  }


  public void executePost(AssessmentConfig config) throws Exception {
    URI uri = UriComponentsBuilder
        .fromPath(config.getUrl())
        .build().toUri();

    String requestContent = resolveFileAsString(config.getInput());
    String responseContent = resolveFileAsString(config.getResponse());

    WebClient client = WebClient
        .builder()
        .baseUrl(BASE_URL)
        .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .build();

    String response = client
        .post().uri(BASE_URL + uri).bodyValue(requestContent)
        .exchange()
        .block()
        .bodyToMono(String.class).block();

    log.warning(response);
    JSONAssert.assertEquals(responseContent, response, false);
  }

}