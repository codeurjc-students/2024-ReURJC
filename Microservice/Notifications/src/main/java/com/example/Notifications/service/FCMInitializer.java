package com.example.Notifications.service;


import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;

@Service
public class FCMInitializer {
    private static final String FIREBASE_CONFIG_FILE = "firebase-service-account.json";
    private static final Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

    @PostConstruct
    public void initialize() {
        try (InputStream serviceAccount = getClass().getClassLoader().getResourceAsStream(FIREBASE_CONFIG_FILE)) {
            if (serviceAccount == null) {
                throw new IOException("Firebase configuration file not found: " + FIREBASE_CONFIG_FILE);
            }

            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();

            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
                logger.info("Firebase application initialized");
            }
        } catch (IOException e) {
            logger.error("Failed to initialize Firebase", e);
        }
    }
}
