package com.unicabavanguardia.java_orchestrator.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "audit-platform-db") // Forzamos el nombre de la colección que pediste
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChatHistory {

    @Id
    private String id;
    @Field("audit_id")
    private Long auditId;

    @Field("updated_at")
    private LocalDateTime updatedAt;

    private List<Message> messages = new ArrayList<>();

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Message {
        private String role;
        private String content;
        private LocalDateTime timestamp;
    }
}