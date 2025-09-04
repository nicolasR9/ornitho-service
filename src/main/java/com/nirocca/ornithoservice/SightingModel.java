package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SightingModel {

    public static final String HOME = "52.40552,13.21875";
    public static final String NORDSEE = "54.24743,8.84237";

    private final Sighting sighting;
    private final String homeLocation;

    public SightingModel(Sighting sighting, String homeLocation) {
        this.sighting = sighting;
        this.homeLocation = homeLocation;
    }

    public String getLatinName() {
        return sighting.latinName();
    }

    public String getLocation() {
        return sighting.locationText();
    }

    public String getGermanNamePlural() {
        return sighting.germanName();
    }

    public String getUrl() {
        return sighting.url();
    }

    public String getDate() {
        return sighting.date();
    }

    public String getCount() {
        return sighting.count();
    }

    public Coordinates getCoordinates() {
        return sighting.coordinates();
    }

    public String getNavigateUrl() {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        return String.format("http://maps.google.de/maps?saddr=%s&daddr=%s,%s",
            homeLocation, df.format(sighting.coordinates().getLatitude()), df.format(sighting.coordinates().getLongitude()));
    }
}
