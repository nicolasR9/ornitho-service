package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.model.Sighting;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SightingsController {

    @Autowired
    private ScheduledSightingsCalculator sightingsCalculator;

    @GetMapping("/last3days")
    public String last3days(Model model) {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightings();
        model.addAttribute("sightings", lastSightings);
        return "last3days";
    }
}


