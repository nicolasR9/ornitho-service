package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.model.Coordinates;
import com.nirocca.ornithoalert.model.Sighting;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class SightingModel {

    public static final String HOME = "52.40552,13.21875";
    public static final String NORDSEE = "54.24743,8.84237";

    private Sighting sighting;
    private Coordinates coordinates;
    private String homeLocation;

    public SightingModel(Sighting sighting, Coordinates coordinates, String homeLocation) {
        this.sighting = sighting;
        this.coordinates = coordinates;
        this.homeLocation = homeLocation;
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

    public Coordinates getCoordinates() {
        return coordinates;
    }

    public String getNavigateUrl() {
        DecimalFormat df = (DecimalFormat) NumberFormat.getNumberInstance(Locale.US);
        return String.format("http://maps.google.de/maps?saddr=%s&daddr=%s,%s",
            homeLocation, df.format(coordinates.getLatitude()), df.format(coordinates.getLongitude()));
    }
}
