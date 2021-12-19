package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SightingsController {

    @Autowired
    private SightingsCalculator sightingsCalculator;

    @GetMapping("/last3days")
    public String last3days(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightings();
        creatModel(model, lastSightings);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysNotThisYear")
    public String last3daysNotThisYear(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsNotThisYear();
        creatModel(model, lastSightings);
        return "sightingsTemplate";
    }

    private void creatModel(Model model, List<Sighting> lastSightings) {
        List<SightingModel> modelSightings = lastSightings.stream()
            .map(s -> {
                try {
                    return new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), SightingModel.HOME);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

        model.addAttribute("sightings", modelSightings);
        model.addAttribute("lastCalculated", ZonedDateTime.now(ZoneId.of("Europe/Berlin")));
    }

}


