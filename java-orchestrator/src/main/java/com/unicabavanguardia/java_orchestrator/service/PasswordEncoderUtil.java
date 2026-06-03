package com.unicabavanguardia.java_orchestrator.service;

import org.springframework.stereotype.Component;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
@Component
public class PasswordEncoderUtil {

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    // Compara la contraseña que ingresa el usuario en el Login con el hash de la DB
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}