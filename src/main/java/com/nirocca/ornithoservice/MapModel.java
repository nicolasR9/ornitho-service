package com.nirocca.ornithoservice;

public class MapModel {

    private String coordinatesAsText;

    public MapModel(String coordinatesAsText) {
        this.coordinatesAsText = coordinatesAsText;
    }

    public String getCoordinatesAsText() {
        return coordinatesAsText;
    }
}
