package com.swp.ihelp.google.firebase;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.FileInputStream;
import java.io.IOException;

@Service
public class FirebaseInitializer {

    @Value("${app.firebase-config}")
    private String firebaseConfigPath;

    @PostConstruct
    public void initialize() {
        try {
            FileInputStream serviceAccount = new FileInputStream(firebaseConfigPath);
            FirebaseOptions.Builder builder = FirebaseOptions.builder();
            FirebaseOptions options = builder.setCredentials(GoogleCredentials.fromStream(serviceAccount)).build();
            if (FirebaseApp.getApps().isEmpty()) {
                FirebaseApp.initializeApp(options);
            }
        } catch (IOException ex) {

        }
    }

}
