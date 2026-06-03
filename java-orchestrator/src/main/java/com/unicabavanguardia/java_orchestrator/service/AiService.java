package com.unicabavanguardia.java_orchestrator.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.http.MediaType;
import java.util.Map;

@Service
public class AiService {

    private final RestClient restClient;

    public AiService(@Value("${python.analyzer.url}") String analyzerUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(analyzerUrl)
                .build();
    }

    /**
     * Se conecta con el microservicio de FastAPI en Python para obtener la auditoría con IA
     * @param codeSnippet El fragmento de código o consulta enviado por el alumno
     * @param language El lenguaje real detectado/configurado en el frontend
     */
    public String generateResponse(String codeSnippet, String language) {
        try {
            // Mapeamos el payload dinámico para el Pydantic (AuditRequest) de FastAPI
            Map<String, String> requestBody = Map.of(
                    "code_snippet", codeSnippet,
                    "language", language // <-- Ya no está harcodeado en "java"
            );

            // Consumo síncrono del microservicio en Python
            String pythonJsonResponse = restClient.post()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(requestBody)
                    .retrieve()
                    .body(String.class);

            return pythonJsonResponse;

        } catch (Exception e) {
            // Fallback en formato JSON válido si el microservicio de Python está caído
            return "{\"score_general\": 0, \"findings\": [{\"severity\": \"Critico\", \"category\": \"Sistema\", \"title\": \"Error de Orquestación\", \"description\": \"No se pudo conectar con el motor analítico de Python. Detalle: " + e.getMessage() + "\", \"affected_lines\": \"0\", \"suggested_fix\": \"\", \"pedagogical_explanation\": \"\"}]}";
        }
    }
}