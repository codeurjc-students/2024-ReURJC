package com.example.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FCMInitializer {

    Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

    @PostConstruct
    public void initialize() {
        try {
            // Lee la variable de entorno FCM_PRIVATE_KEY
            String firebaseCredentials = System.getenv("FCM_PRIVATE_KEY");

            // Si la variable no está definida, puedes lanzar una excepción o usar una configuración por defecto
            if (firebaseCredentials == null || firebaseCredentials.isEmpty()) {
                logger.error("FCM_PRIVATE_KEY environment variable not set.");
                // Puedes lanzar una excepción aquí si la clave es obligatoria:
                // throw new RuntimeException("FCM_PRIVATE_KEY environment variable not set.");
                // O usar una configuración por defecto (menos recomendado):
                // firebaseCredentials = "{\"type\": \"service_account\", ... }"; 
                return; // Puedes simplemente salir si no quieres hacer nada si la variable no está configurada.
            }

            InputStream serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8));

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application initialized");
            }
        } catch (IOException e) {
            logger.error("Error initializing Firebase", e);
        }
    }
}