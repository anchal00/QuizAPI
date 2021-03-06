package com.crio.buildout.dataloader;

import com.crio.buildout.models.QuestionEntity;
import com.crio.buildout.repositoryservice.QnARepositoryService;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class LoadDatabase implements CommandLineRunner {

  @Autowired
  QnARepositoryService qnaRepositoryService;
    
  @Override
  public void run(String... args) throws Exception {
    File file = new File(System.getProperty("user.dir") + "/../initial_data_load.json");        
    List<QuestionEntity> list = new ObjectMapper().readValue(file, 
        new TypeReference<List<QuestionEntity>>(){});
    for (QuestionEntity each : list) {
      each.setModuleId("1");
    }
    qnaRepositoryService.clearDb();
    qnaRepositoryService.populateDatabase(list);
    System.out.println("Populated the Db");
  }  
}
