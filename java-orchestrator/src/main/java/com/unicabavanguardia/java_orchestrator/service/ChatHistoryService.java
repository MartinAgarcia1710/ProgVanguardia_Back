package com.unicabavanguardia.java_orchestrator.service;

import com.unicabavanguardia.java_orchestrator.model.ChatHistory;
import com.unicabavanguardia.java_orchestrator.repository.ChatHistoryRepository;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class ChatHistoryService {

    private final ChatHistoryRepository chatHistoryRepository;

    public ChatHistoryService(ChatHistoryRepository chatHistoryRepository) {
        this.chatHistoryRepository = chatHistoryRepository;
    }

    public ChatHistory appendMessage(Long auditId, String role, String content) {
        ChatHistory chatHistory = chatHistoryRepository.findByAuditId(auditId)
                .orElseGet(() -> {
                    ChatHistory newChat = new ChatHistory();
                    newChat.setAuditId(auditId);
                    return newChat;
                });

        ChatHistory.Message newMessage = new ChatHistory.Message(role, content, LocalDateTime.now());

        chatHistory.getMessages().add(newMessage);
        chatHistory.setUpdatedAt(LocalDateTime.now());

        return chatHistoryRepository.save(chatHistory);
    }

    public Optional<ChatHistory> getChatByAuditId(Long auditId) {
        return chatHistoryRepository.findByAuditId(auditId);
    }
}