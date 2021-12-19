package com.nirocca.ornithoservice;

import com.google.appengine.api.memcache.ErrorHandlers;
import com.google.appengine.api.memcache.Expiration;
import com.google.appengine.api.memcache.MemcacheService;
import com.google.appengine.api.memcache.MemcacheServiceFactory;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class EmailController {

    @Autowired
    private SightingsCalculator sightingsCalculator;

    @Autowired
    private EmailSender emailSender;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmailController.class);

    @GetMapping("/sendUpdateEmail")
    public void checkAndAlert() throws IOException {
        LOGGER.info("sendUpdateEmail started");
        MemcacheService cache = getCache(); //make sure not to send the same sighting twice

        List<Sighting> lastSightings = sightingsCalculator.getNewSightings();
        lastSightings = lastSightings.stream().filter(s -> !cache.contains(s.getUrl())).collect(Collectors.toList());
        if (!lastSightings.isEmpty()) {
            LOGGER.info("sending email with {} sightings", lastSightings.size());
            emailSender.send(lastSightings);
            Map<String, String> cacheMap = lastSightings.stream().collect(Collectors.toMap(Sighting::getUrl, (s) -> ""));
            cache.putAll(cacheMap, Expiration.byDeltaSeconds((int) TimeUnit.DAYS.toSeconds(3)));
        } else {
            LOGGER.info("No new sightings.");
        }
    }

    private MemcacheService getCache() {
        MemcacheService cache = MemcacheServiceFactory.getMemcacheService();
        cache.setErrorHandler(ErrorHandlers.getConsistentLogAndContinue(Level.INFO));
        return cache;
    }

}
