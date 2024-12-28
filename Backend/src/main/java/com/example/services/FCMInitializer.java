package com.example.services;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
public class FCMInitializer {

    Logger logger = LoggerFactory.getLogger(FCMInitializer.class);

    @PostConstruct
    public void initialize() {
      String firebaseCredentials = null;
      try {
        firebaseCredentials = System.getenv("FCM_PRIVATE_KEY");
        InputStream serviceAccount;
        if (firebaseCredentials != null && !firebaseCredentials.isEmpty()) {
          // Si la variable de entorno está presente, usarla
          serviceAccount = new ByteArrayInputStream(firebaseCredentials.getBytes(StandardCharsets.UTF_8));
          logger.info("Firebase Admin SDK initialized using environment variable.");
        } else {
          // Si no, intentar cargar desde el archivo (embebido en la imagen)
          
          serviceAccount = new FileInputStream("/app/firebase-service-account.json");
          logger.warn("Firebase Admin SDK initialized using embedded service account file. This is not recommended for production.");
        }

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