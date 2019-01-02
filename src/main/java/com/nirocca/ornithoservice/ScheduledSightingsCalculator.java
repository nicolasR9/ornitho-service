package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.OrnithoUrl;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledSightingsCalculator {
    private static final ZoneId TIMEZONE = ZoneId.of("Europe/Berlin");

    private List<Sighting> lastSightings;

    private ZonedDateTime lastCalculated = ZonedDateTime.of(LocalDateTime.MIN, TIMEZONE);


    //12 hours
    @Scheduled(fixedDelay = 43200000)
    public void calculate() throws IOException {
        lastSightings = Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES);
        lastCalculated = ZonedDateTime.now(TIMEZONE);
    }

    List<Sighting> getLastSightings() {
        return lastSightings;
    }

    ZonedDateTime getLastCalculated() {
        return lastCalculated;
    }
}
