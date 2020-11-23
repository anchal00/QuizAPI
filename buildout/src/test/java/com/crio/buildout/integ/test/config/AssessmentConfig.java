package com.crio.buildouts.integ.test.config;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AssessmentConfig {
  private String name;
  private String url;
  private String method;
  private String input;
  private int status;
  private String verification;
  private String response;
}
