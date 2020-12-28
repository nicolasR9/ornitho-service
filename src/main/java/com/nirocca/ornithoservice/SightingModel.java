package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SightingModel {

    private static final String HOME = "52.40552,13.21875";
    private Sighting sighting;
    private Coordinates coordinates;

    public SightingModel(Sighting sighting, Coordinates coordinates) {
        this.sighting = sighting;
        this.coordinates = coordinates;
    }

    public String getLatinName() {
        return sighting.getLatinName();
    }

    public String getLocation() {
        return sighting.getLocation();
    }

    public String getGermanNamePlural() {
        return sighting.getGermanNamePlural();
    }

    public String getUrl() {
        return sighting.getUrl();
    }

    public String getDate() {
        return sighting.getDate();
    }

    public String getCount() {
        return sighting.getCount();
    }

    public String getNavigateUrl() {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        return String.format("http://maps.google.de/maps?saddr=%s&daddr=%s,%s",
            HOME, df.format(coordinates.getLatitude()), df.format(coordinates.getLongitude()));
    }
}
