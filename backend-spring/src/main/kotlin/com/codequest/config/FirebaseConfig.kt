package com.codequest.config

import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.auth.FirebaseAuth
import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.io.FileInputStream

@Configuration
class FirebaseConfig {

    @Bean
    fun firebaseApp(): FirebaseApp {
        val serviceAccount = FileInputStream("/config/firebase-adminsdk.json")
        val credentials = GoogleCredentials.fromStream(serviceAccount)
        val options = FirebaseOptions.builder()
            .setCredentials(credentials)
            .build()
        return FirebaseApp.initializeApp(options)
    }

    @Bean
    fun firebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }
}

@ConfigurationProperties(prefix = "firebase.config")
class FirebaseProperties {
    var databaseUrl: String = ""
    var projectId: String = ""
    var credentialsPath: String = ""
}

