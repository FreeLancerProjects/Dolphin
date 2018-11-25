package com.appzone.dolphin.Models;

import java.io.Serializable;

public class ServiceModel implements Serializable {
    private String id_services;
    private String ar_services_title;
    private String en_services_title;
    private String services_logo;

    public String getId_services() {
        return id_services;
    }

    public String getAr_services_title() {
        return ar_services_title;
    }

    public String getEn_services_title() {
        return en_services_title;
    }

    public String getServices_logo() {
        return services_logo;
    }
}
