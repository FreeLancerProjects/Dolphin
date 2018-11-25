package com.appzone.dolphin.Models;

import java.io.Serializable;
import java.util.List;

public class Country_City_Model implements Serializable{
    private String id_country;
    private String ar_name;
    private String en_name;
    private String ar_nationality;
    private String en_nationality;
    private String phone_code;
    private String google_lat;
    private String google_long;
    private List<CityModel> sub_city;

    public String getId_country() {
        return id_country;
    }

    public String getAr_name() {
        return ar_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public String getAr_nationality() {
        return ar_nationality;
    }

    public String getEn_nationality() {
        return en_nationality;
    }

    public String getPhone_code() {
        return phone_code;
    }

    public String getGoogle_lat() {
        return google_lat;
    }

    public String getGoogle_long() {
        return google_long;
    }

    public List<CityModel> getSub_city() {
        return sub_city;
    }

    public class CityModel implements Serializable
    {
        private String id_city;
        private String ar_city_title;
        private String en_city_title;


        public String getId_city() {
            return id_city;
        }

        public String getAr_city_title() {
            return ar_city_title;
        }

        public String getEn_city_title() {
            return en_city_title;
        }
    }
}
