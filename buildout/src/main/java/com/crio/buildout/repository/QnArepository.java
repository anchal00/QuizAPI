package com.crio.buildout.repository;

import com.crio.buildout.models.QuestionEntity;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface QnArepository extends MongoRepository<QuestionEntity, String> {
    
    public List<QuestionEntity> findAllByModuleId(String moduleId);
    
}
