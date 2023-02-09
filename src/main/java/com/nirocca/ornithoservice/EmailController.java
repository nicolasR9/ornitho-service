package com.nirocca.ornithoservice;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmailController {

    private static final String ENTITY_NAME = "Sighting";
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private SightingsCalculator sightingsCalculator;

    @Autowired
    private EmailSender emailSender;

    static {
        FirebaseOptions options;
        try {
            options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setProjectId("ornitho-service")
                .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        FirebaseApp.initializeApp(options);
    }

    @GetMapping("/sendUpdateEmail")
    public void checkAndAlert() throws IOException, ExecutionException, InterruptedException {
        LOGGER.info("sendUpdateEmail started");

        Firestore db = FirestoreClient.getFirestore();

        List<Sighting> lastSightings = sightingsCalculator.getSightingsBrandenburgTwoDays();
        lastSightings = lastSightings.stream().filter(s -> !wasAlreadySentBefore(db, s)).collect(Collectors.toList());
        if (!lastSightings.isEmpty()) {
            LOGGER.info("sending email with {} sightings", lastSightings.size());
            emailSender.send(lastSightings);

            CollectionReference dbCollection = db.collection(ENTITY_NAME);
            for (Sighting s : lastSightings) {
                putDoc(dbCollection, s);
            }
        } else {
            LOGGER.info("No new sightings.");
        }
    }

    private void putDoc(CollectionReference dbCollection, Sighting sighting)
        throws ExecutionException, InterruptedException {
        DocumentReference docRef = dbCollection.document(URLEncoder.encode(sighting.getUrl(),
            Charset.defaultCharset()));
        ApiFuture<WriteResult> result = docRef.set(Collections.singletonMap("dummy", Boolean.TRUE));
        System.out.println("Added : " + result.get().getUpdateTime());
    }

    private boolean wasAlreadySentBefore(Firestore db, Sighting sighting) {
        DocumentReference docRef = db.collection(ENTITY_NAME).document(URLEncoder.encode(sighting.getUrl(),
            Charset.defaultCharset()));
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document;
        try {
            document = future.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        return document.exists();
    }

}
