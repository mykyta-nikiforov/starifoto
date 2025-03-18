package ua.in.photomap.user.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;

@Configuration
public class GoogleConfig {

    @Value("${photomap.google.auth.client-id}")
    private String clientId;

    @Bean
    public GoogleIdTokenVerifier googleIdTokenVerifier() {
        return new GoogleIdTokenVerifier.Builder(new ApacheHttpTransport(), new GsonFactory())
                .setAudience(Collections.singletonList(clientId))
                .build();
    }
}