package com.crio.buildout.models;

import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "qnaset")
public class QuestionEntity {
    
  @Field("moduleId")
  private String moduleId;

  @Field("questionId")
  private String questionId;

  @Field("title")
  private String title;

  @Field("description")
  private String description;

  @Field("type")
  private String type;

  @Field("options")
  private Map<String, String> options;

  @Field("correctAnswer")
  private List<String> correctAnswer;

  @Field("explanation")
  private String explanation;  
}
