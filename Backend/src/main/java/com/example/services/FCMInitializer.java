package com.example.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FCMInitializer {

    Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

    @Value("${firebase.service-account-file}")
    private String serviceAccountFile = "";

    @PostConstruct
    public void initialize() {
        try {
            InputStream serviceAccount;

            // Comprueba si se está ejecutando en Docker
            if (isRunningInDocker()) {
                logger.info("Detected Docker environment.");
                serviceAccount = new ClassPathResource(serviceAccountFile).getInputStream();
                logger.info("Firebase Admin SDK initialized using service account file in Docker: " + serviceAccountFile);
            } else {
                // Si no está en Docker, intentamos con el classpath
                serviceAccount = new ClassPathResource(serviceAccountFile).getInputStream();
                logger.info("Firebase Admin SDK initialized using classpath service account file: " + serviceAccountFile);
            }

            /*FirebaseOptions options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream("hola"))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application initialized");
            }*/
        } catch (IOException e) {
            logger.error("Error initializing Firebase", e);
        }
    }
    
    private boolean isRunningInDocker() {
        return "true".equals(System.getenv("RUNNING_IN_DOCKER"));
    }
}