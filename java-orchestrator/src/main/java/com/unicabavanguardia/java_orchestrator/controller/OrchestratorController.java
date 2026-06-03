package com.unicabavanguardia.java_orchestrator.controller;

import com.unicabavanguardia.java_orchestrator.model.ChatHistory;
import com.unicabavanguardia.java_orchestrator.model.Audit;
import com.unicabavanguardia.java_orchestrator.model.User;
import com.unicabavanguardia.java_orchestrator.repository.AuditRepository;
import com.unicabavanguardia.java_orchestrator.repository.UserRepository;
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
    private final AuditRepository auditRepository;
    private final UserRepository userRepository;

    public OrchestratorController(ChatHistoryService chatHistoryService, AiService aiService,
                                  AuditRepository auditRepository, UserRepository userRepository) {
        this.chatHistoryService = chatHistoryService;
        this.aiService = aiService;
        this.auditRepository = auditRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/ask")
    public ResponseEntity<?> askAssistant(@RequestBody Map<String, Object> request) {
        try {
            Long auditId = Long.valueOf(request.get("auditId").toString());
            String userPrompt = request.get("prompt").toString();

            if (auditId == null || userPrompt == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "auditId y prompt son obligatorios"));
            }

            // === BLINDAJE TOTAL DE POSTGRES ===
            // === ASIGNACIÓN RELACIONAL REAL ===
            try {
                if (!auditRepository.existsById(auditId)) {
                    // Extraemos el ID del usuario real enviado por el Front
                    Long userId = Long.valueOf(request.get("userId").toString());
                    User actualStudent = userRepository.findById(userId).orElse(null);

                    if (actualStudent != null) {
                        Audit newAudit = new Audit();
                        newAudit.setId(auditId);
                        newAudit.setStudent(actualStudent); // Ligamos la FK real
                        newAudit.setSubject("Programación 2");
                        newAudit.setStatus("PENDING");

                        auditRepository.save(newAudit);
                        System.out.println("¡Auditoría " + auditId + " guardada con éxito en Supabase!");
                    } else {
                        System.out.println("ADVERTENCIA: No se encontró el usuario con ID " + userId + " en Postgres.");
                    }
                }
            } catch (Exception e) {
                System.out.println("Sincronización Postgres omitida: " + e.getMessage());
            }
            // ===================================

            // Continúa el flujo síncrono hacia MongoDB Atlas que ya sabemos que responde perfecto
            chatHistoryService.appendMessage(auditId, "user", userPrompt);

            // Hablamos con la IA simulada
            String aiResponse = aiService.generateResponse(userPrompt);

            // Guardamos la respuesta del bot en Mongo
            ChatHistory updatedChat = chatHistoryService.appendMessage(auditId, "assistant", aiResponse);

            // Respondemos con el JSON final al frontend de Streamlit
            return ResponseEntity.ok(updatedChat);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(Map.of("error", "Error en la orquestación: " + e.getMessage()));
        }
    }
}