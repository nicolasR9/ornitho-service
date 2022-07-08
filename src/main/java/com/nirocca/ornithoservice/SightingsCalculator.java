package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.OrnithoUrl;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class SightingsCalculator {

    List<Sighting> getLastSightings() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES);
    }

    List<Sighting> getLastSightingsNotThisYear() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES, FilterMySightedSpecies.ONLY_THIS_YEAR);
    }

    List<Sighting> getLastSightingsVacation(String ornithoUrl) throws IOException {
        return Main.calcSightings(ornithoUrl, SortBy.SPECIES);
    }

    List<Sighting> getLastSightingsVacationNotThisYear(String ornithoUrl) throws IOException {
        return Main.calcSightings(ornithoUrl, SortBy.SPECIES, FilterMySightedSpecies.ONLY_THIS_YEAR);
    }

    List<Sighting> getSightingsBrandenburgTwoDays() throws IOException {
        return Main.calcSightings(OrnithoUrl.BRANDENBURG_LAST_2_DAYS.getUrl(), SortBy.SPECIES);
    }


}
