package com.unicabavanguardia.java_orchestrator.controller;

import com.unicabavanguardia.java_orchestrator.model.ChatHistory;
import com.unicabavanguardia.java_orchestrator.service.ChatHistoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditChatController {

    private final ChatHistoryService chatHistoryService;

    public AuditChatController(ChatHistoryService chatHistoryService) {
        this.chatHistoryService = chatHistoryService;
    }

    // 1. ENDPOINT PARA AGREGAR MENSAJE: POST http://localhost:8080/api/audit/{auditId}/message
    @PostMapping("/{auditId}/message")
    public ResponseEntity<?> addMessageToChat(
            @PathVariable Long auditId,
            @RequestBody Map<String, String> body) {

        String role = body.get("role"); // "user" o "assistant"
        String content = body.get("content");

        if (role == null || content == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "role y content son obligatorios"));
        }

        ChatHistory updatedChat = chatHistoryService.appendMessage(auditId, role, content);
        return ResponseEntity.ok(updatedChat);
    }

    // 2. ENDPOINT PARA TRAER EL CHAT: GET http://localhost:8080/api/audit/{auditId}/history
    @GetMapping("/{auditId}/history")
    public ResponseEntity<?> getChatHistory(@PathVariable Long auditId) {
        return chatHistoryService.getChatByAuditId(auditId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}