package com.nirocca.ornithoservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutionException;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LiferMapController {

    private static final Logger LOGGER = LoggerFactory.getLogger(LiferMapController.class);
    private static final String MAP_URL_FIELD = "url";

    private final String authToken;

    public LiferMapController() throws IOException {
        this.authToken = loadToken();
    }

    // token lives in the gitignored classpath file lifermap.properties (like sendgrid.properties)
    private String loadToken() throws IOException {
        try (InputStream in = LiferMapController.class.getResourceAsStream("/lifermap.properties")) {
            if (in == null) {
                return "";
            }
            Properties properties = new Properties();
            properties.load(in);
            return properties.getProperty("lifermap.auth.token", "");
        }
    }

    @GetMapping("/liferMap")
    public ResponseEntity<Void> liferMap() throws ExecutionException, InterruptedException {
        DocumentSnapshot document = getDocument();
        String url = document.exists() ? document.getString(MAP_URL_FIELD) : null;
        if (url == null || url.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .header("Content-Type", "text/plain")
                    .build();
        }
        return ResponseEntity.status(HttpStatus.FOUND)
                .header("Location", url)
                .build();
    }

    @PostMapping("/liferMap/update")
    public ResponseEntity<String> update(@RequestBody String mapUrl,
                                        @RequestHeader(value = "X-Auth-Token", required = false) String token)
            throws ExecutionException, InterruptedException {
        if (authToken.isEmpty() || !authToken.equals(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        mapUrl = mapUrl.trim();
        if (!mapUrl.startsWith("https://www.gpsvisualizer.com/")) {
            return ResponseEntity.badRequest().body("unexpected url");
        }
        ApiFuture<WriteResult> result = FirestoreClient.getFirestore().collection("config").document("liferMap")
                .set(Map.of(MAP_URL_FIELD, mapUrl));
        LOGGER.info("stored lifer map url {} at {}", mapUrl, result.get().getUpdateTime());
        return ResponseEntity.ok("stored");
    }

    private DocumentSnapshot getDocument() throws ExecutionException, InterruptedException {
        Firestore db = FirestoreClient.getFirestore();
        return db.collection("config").document("liferMap").get().get();
    }

}
