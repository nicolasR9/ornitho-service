package com.nirocca.ornithoservice;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.util.List;
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

    @GetMapping("/sendUpdateEmail")
    public void checkAndAlert() throws IOException {
        LOGGER.info("sendUpdateEmail started");

        DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

        List<Sighting> lastSightings = sightingsCalculator.getSightingsBrandenburgTwoDays();
        lastSightings = lastSightings.stream().filter(s -> !wasAlreadySentBefore(datastore, s)).collect(Collectors.toList());
        if (!lastSightings.isEmpty()) {
            LOGGER.info("sending email with {} sightings", lastSightings.size());
            emailSender.send(lastSightings);

            List<Entity> entities = lastSightings.stream().map(s -> new Entity(ENTITY_NAME, s.getUrl()))
                .collect(Collectors.toList());
            datastore.put(entities);
        } else {
            LOGGER.info("No new sightings.");
        }
    }

    private boolean wasAlreadySentBefore(DatastoreService datastore, Sighting s) {
        try {
            datastore.get(KeyFactory.createKey("Sighting", s.getUrl()));
            return true;
        } catch (EntityNotFoundException e) {
            return false;
        }

    }

}
