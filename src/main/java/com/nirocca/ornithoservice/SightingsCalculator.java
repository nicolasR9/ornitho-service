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
public class SightingsCalculator {

    List<Sighting> getLastSightings() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES);
    }

    List<Sighting> getLastSightingsZingst() throws IOException {
        return Main.calcSightings(OrnithoUrl.ZINGST_3_DAYS.getUrl(), SortBy.SPECIES);
    }


}
