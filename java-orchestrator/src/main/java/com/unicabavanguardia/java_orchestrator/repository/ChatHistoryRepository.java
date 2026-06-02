package com.unicabavanguardia.java_orchestrator.repository;

import com.unicabavanguardia.java_orchestrator.model.ChatHistory;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface ChatHistoryRepository extends MongoRepository<ChatHistory, String> {
    Optional<ChatHistory> findByAuditId(Long auditId);
}