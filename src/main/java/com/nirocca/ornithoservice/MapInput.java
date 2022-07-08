package com.nirocca.ornithoservice;

import com.nirocca.ornithoalert.Constants.FilterMySightedSpecies;

public class MapInput {
    private String url;

    private FilterMySightedSpecies filter;
    private OutputType outputType;

    public FilterMySightedSpecies getFilter() {
        return filter;
    }

    public void setFilter(FilterMySightedSpecies filter) {
        this.filter = filter;
    }

    public OutputType getOutputType() {
        return outputType;
    }

    public void setOutputType(OutputType outputType) {
        this.outputType = outputType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public static enum OutputType {
        GPX,
        GPS_VISUALIZER
    }
}
