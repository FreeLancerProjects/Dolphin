package com.appzone.dolphin.Models;

import java.io.Serializable;

public class LocationBookModel implements Serializable {
    private double lat;
    private double lng;

    public LocationBookModel(double lat, double lng) {
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat() {
        return lat;
    }

    public double getLng() {
        return lng;
    }
}
