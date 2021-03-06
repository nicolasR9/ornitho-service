package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.model.Sighting;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SightingsController {

    private static final double NORDSEE_NORTH_BORDER_LAT = 54.432914;
    @Autowired
    private SightingsCalculator sightingsCalculator;

    @GetMapping("/last3days")
    public String last3days(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightings();
        List<SightingModel> modelSightings = lastSightings.stream()
            .map(s -> {
                try {
                    return new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), SightingModel.HOME);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

        model.addAttribute("sightings", modelSightings);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysNordsee")
    public String last3daysNordsee(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsNordsee();
        List<SightingModel> modelSightings = lastSightings.stream()
            .map(s -> {
                try {
                    return new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), SightingModel.NORDSEE);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

        modelSightings = modelSightings.stream().filter(
            s -> s.getCoordinates().getLatitude() < NORDSEE_NORTH_BORDER_LAT).collect(Collectors.toList());

        model.addAttribute("sightings", modelSightings);
        return "sightingsTemplate";
    }

    @GetMapping(value="/last3daysNordseeMap", produces= MediaType.TEXT_PLAIN_VALUE)
    public String last3daysNordseeMap(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsNordsee();
        lastSightings = lastSightings.stream().filter(s -> {
            try {
                return CoordinatesExporter.getCoordinates(s.getUrl()).getLatitude() < NORDSEE_NORTH_BORDER_LAT;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).collect(Collectors.toList());
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        new CoordinatesExporter().printCoordinates(lastSightings, false, outStream);
        String coordinatesAsText = outStream.toString();
        model.addAttribute("coordinatesText", new MapModel(coordinatesAsText));
        return "mapTemplate";
    }

}


