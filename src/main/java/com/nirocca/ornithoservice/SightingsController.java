package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.model.Sighting;
import java.io.IOException;
import java.util.List;
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
        model.addAttribute("sightings", lastSightings);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysZingst")
    public String last3daysZingst(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsZingst();
        model.addAttribute("sightings", lastSightings);
        return "sightingsTemplate";
    }
}


