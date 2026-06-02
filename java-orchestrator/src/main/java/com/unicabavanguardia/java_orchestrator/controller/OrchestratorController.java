package com.unicabavanguardia.java_orchestrator.controller;

import com.unicabavanguardia.java_orchestrator.model.ChatHistory;
import com.unicabavanguardia.java_orchestrator.service.ChatHistoryService;
import com.unicabavanguardia.java_orchestrator.service.AiService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/orchestrator")
@CrossOrigin(origins = "*")
public class OrchestratorController {

    private final ChatHistoryService chatHistoryService;
    private final AiService aiService;

    public OrchestratorController(ChatHistoryService chatHistoryService, AiService aiService) {
        this.chatHistoryService = chatHistoryService;
        this.aiService = aiService;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askAssistant(@RequestBody Map<String, Object> request) {
        try {
            Long auditId = Long.valueOf(request.get("auditId").toString());
            String userPrompt = request.get("prompt").toString();

            if (auditId == null || userPrompt == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "auditId y prompt son obligatorios"));
            }

            chatHistoryService.appendMessage(auditId, "user", userPrompt);

            String aiResponse = aiService.generateResponse(userPrompt);

            ChatHistory updatedChat = chatHistoryService.appendMessage(auditId, "assistant", aiResponse);

            return ResponseEntity.ok(updatedChat);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error en la orquestación: " + e.getMessage()));
        }
    }
}