package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;
import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.OrnithoUrl;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.statistics.Species;
import com.nirocca.ornithoalert.statistics.StatisticsCalculator;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class SightingsCalculator {

    List<Sighting> getLastSightings() throws IOException {
        return Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES);
    }

    List<Sighting> getRareSightings() throws IOException {
        List<com.nirocca.ornithoalert.statistics.Sighting> mySightings = StatisticsCalculator.readMySightings();
        Set<Species> frequentSpecies =
            StatisticsCalculator.calcSpeciesSightedAlmostEveryYear(mySightings, 2);
        Set<String> frequentLatin = frequentSpecies.stream().map(s -> s.getLatinName()).collect(Collectors.toSet());
        List<Sighting> lastSightings = Main.calcSightings(OrnithoUrl.GROSSRAUM_LAST_3_DAYS.getUrl(), SortBy.SPECIES, FilterMySightedSpecies.NO);
        return lastSightings.stream().filter(s -> !frequentLatin.contains(s.getLatinName())).collect(Collectors.toList());
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
