package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.SortBy;
import com.nirocca.ornithoalert.CoordinatesExporter;
import com.nirocca.ornithoalert.Main;
import com.nirocca.ornithoalert.PrintParameters;
import com.nirocca.ornithoalert.model.Sighting;
import com.nirocca.ornithoalert.util.GpsVisualizerTxtToGpxConverter;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MapRequestController {

    @GetMapping("/map")
    public String mapForm(Model model) {
        model.addAttribute("mapInput", new MapInput());
        return "mapRequestForm";
    }

    @PostMapping(value= "/map", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> mapSubmit(@ModelAttribute MapInput mapInput, Model model) throws IOException {
        List<Sighting> sightings = Main.calcSightings(mapInput.getUrl(), SortBy.SPECIES, mapInput.getFilter());
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        new CoordinatesExporter().printCoordinates(new PrintParameters(sightings, false, output, null, true));

        HttpHeaders responseHeaders = new HttpHeaders();
        switch (mapInput.getOutputType()) {
            case GPX -> {
                responseHeaders.set("Content-disposition", "attachment; filename=map.gpx");
                byte[] bytes = GpsVisualizerTxtToGpxConverter.toGpx(output.toString()).getBytes(StandardCharsets.UTF_8);
                return new ResponseEntity<>(bytes, responseHeaders, HttpStatus.OK);
            }
            case GPS_VISUALIZER -> {
                responseHeaders.set("Content-disposition", "attachment; filename=map.txt");
                return new ResponseEntity<>(output.toByteArray(), responseHeaders, HttpStatus.OK);
            }
            default -> throw new RuntimeException("Unknown output type: " + mapInput.getOutputType());
        }
    }

}
