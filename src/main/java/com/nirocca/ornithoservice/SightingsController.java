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

    public static final VacationSettings VACATION_SETTINGS = new VacationSettings(
        "54.1817001638333, 7.886407201326603",
        "https://www.ornitho.de/index.php?m_id=94&p_c=places&p_cc=217&sp_tg=1&sp_DFrom=24.08.2023&sp_DTo=24.08.2023&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=3&speciesFilter=&sp_S=54681&sp_SChoice=category&sp_Cat%5Bnever%5D=1&sp_Cat%5Bveryrare%5D=1&sp_Cat%5Brare%5D=1&sp_Cat%5Bunusual%5D=1&sp_Cat%5Bescaped%5D=1&sp_Cat%5Bcommon%5D=1&sp_Family=1&sp_PChoice=canton&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000110000000000&p_cc=217&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_Polygon=&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=ALPHA&sp_FListSpeciesChoice=DATA&sp_DateSynth=10.11.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten");


    @GetMapping("/last3days")
    public String last3days(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightings();
        creatModel(model, lastSightings, SightingModel.HOME);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysNotThisYear")
    public String last3daysNotThisYear(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsNotThisYear();
        creatModel(model, lastSightings, SightingModel.HOME);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysRare")
    public String last3daysRare(Model model) throws IOException {
        List<Sighting> rareSightings = sightingsCalculator.getRareSightings();
        creatModel(model, rareSightings, SightingModel.HOME);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysVacation")
    public String last3daysVacation(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsVacation(VACATION_SETTINGS.ornithoUrl);
        creatModel(model, lastSightings, VACATION_SETTINGS.location);
        return "sightingsTemplate";
    }

    @GetMapping("/last3daysVacationNotThisYear")
    public String last3daysVacationNotThisYear(Model model) throws IOException {
        List<Sighting> lastSightings = sightingsCalculator.getLastSightingsVacationNotThisYear(VACATION_SETTINGS.ornithoUrl);
        creatModel(model, lastSightings, VACATION_SETTINGS.location);
        return "sightingsTemplate";
    }

    private void creatModel(Model model, List<Sighting> lastSightings, String baseLocation) {
        List<SightingModel> modelSightings = lastSightings.stream()
            .map(s -> {
                try {
                    return new SightingModel(s, CoordinatesExporter.getCoordinates(s.getUrl()), baseLocation);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).collect(Collectors.toList());

        model.addAttribute("sightings", modelSightings);
        model.addAttribute("lastCalculated", ZonedDateTime.now(ZoneId.of("Europe/Berlin")));
    }

    public static final class VacationSettings {
        public String location;
        public String ornithoUrl;

        public VacationSettings(String location, String ornithoUrl) {
            this.location = location;
            this.ornithoUrl = ornithoUrl;
        }
    }

}


