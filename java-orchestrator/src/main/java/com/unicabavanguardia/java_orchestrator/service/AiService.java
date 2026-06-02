package com.unicabavanguardia.java_orchestrator.service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@Service
public class AiService {

    private final RestTemplate restTemplate = new RestTemplate();

    private final String AI_API_URL = "https://api.openai.com/v1/chat/completions";

    public String generateResponse(String prompt) {
        try {
            return "Respuesta procesada por el LLM para el prompt: " + prompt;
        } catch (Exception e) {
            return "Error de comunicación con el motor de IA: " + e.getMessage();
        }
    }
}