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

    private static final VacationSettings VACATION_SETTINGS = new VacationSettings(
        "54.113406, 13.376549",
        "https://www.ornitho.de/index.php?m_id=94&p_c=3&p_cc=202&sp_tg=1&sp_DFrom=15.08.2022&sp_DTo=15.08.2022&sp_DSeasonFromDay=1&sp_DSeasonFromMonth=1&sp_DSeasonToDay=31&sp_DSeasonToMonth=12&sp_DChoice=offset&sp_DOffset=3&speciesFilter=&sp_S=1197&sp_SChoice=category&sp_Cat[never]=1&sp_Cat[veryrare]=1&sp_Cat[rare]=1&sp_Cat[unusual]=1&sp_Cat[escaped]=1&sp_Cat[common]=1&sp_Family=1&sp_cC=0000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000&sp_cCO=000000010000000000000000000&sp_CommuneCounty=356&sp_Commune=12332&sp_Info=&sp_P=0&sp_PChoice=polygon&sp_Polygon=POLYGON%28%2813.239235457824295+54.15688050109007%2C13.404717024612752+54.21353461803222%2C13.485741194243616+54.090484518081496%2C13.320946272960505+54.0372911104715%2C13.239235457824295+54.15688050109007%29%29&OpenLayers_Control_LayerSwitcher_5_baseLayers=OpenStreetMap+Live&Z%C3%A4hlgebiete_/_Probefl%C3%A4chen=Z%C3%A4hlgebiete+%2F+Probefl%C3%A4chen&sp_PolygonSaveName=&sp_PolygonSaveRestoreID=&sp_AltitudeFrom=-19&sp_AltitudeTo=2962&sp_CommentValue=&sp_OnlyAH=0&sp_Ats=-00000&sp_project=&sp_OnlyStoc=&sp_frmListType=&sp_FChoice=list&sp_FDisplay=DATE_PLACE_SPECIES&sp_DFormat=DESC&sp_FOrderListSpecies=COUNT&sp_FListSpeciesChoice=DATA&sp_DateSynth=15.08.2022&sp_FOrderSynth=ALPHA&sp_FGraphChoice=DATA&sp_FGraphFormat=auto&sp_FAltScale=250&sp_FAltChoice=DATA&sp_FMapFormat=none&submit=Abfrage+starten&mp_item_per_page=60&mp_current_page=1");


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

    private static final class VacationSettings {
        public String location;
        public String ornithoUrl;

        public VacationSettings(String location, String ornithoUrl) {
            this.location = location;
            this.ornithoUrl = ornithoUrl;
        }
    }

}


