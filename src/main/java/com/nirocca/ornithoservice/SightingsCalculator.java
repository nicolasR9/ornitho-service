package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.OrnithoUrl;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class SightingsCalculator {
    private List<Sighting> lastSightingsNordsee = new ArrayList<>();
    private static final ZoneId TIMEZONE = ZoneId.of("Europe/Berlin");
    private ZonedDateTime lastCalculatedNordsee = ZonedDateTime.of(LocalDateTime.MIN, TIMEZONE);

    List<Sighting> getLastSightings() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES);
    }

    List<Sighting> getLastSightingsNotThisYear() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES, true);
    }

    //6 hours
    @Scheduled(fixedDelay = 21600000)
    void calcLastSightingsNordsee() throws IOException {
        lastSightingsNordsee = Main.calcSightings(OrnithoUrl.NORDSEE_3_DAYS.getUrl(), SortBy.SPECIES);
        lastCalculatedNordsee = ZonedDateTime.now(TIMEZONE);
    }


    List<Sighting> getLastSightingsNordsee() {
        return lastSightingsNordsee;
    }

    ZonedDateTime getLastCalculatedNordsee() {
        return lastCalculatedNordsee;
    }
}
