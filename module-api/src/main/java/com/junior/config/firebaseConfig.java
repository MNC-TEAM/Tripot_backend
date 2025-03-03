package com.junior.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import jakarta.annotation.PostConstruct;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;

@Configuration
public class firebaseConfig {
    @PostConstruct
    public void init() throws IOException {
        String path = Paths.get("/tripot-dev-firebase-adminsdk-key.json").toAbsolutePath().toString();
        FileInputStream serviceAccount = new FileInputStream(path);

        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);
    }
}
