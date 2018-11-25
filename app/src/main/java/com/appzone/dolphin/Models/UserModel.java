package com.appzone.dolphin.Models;

import java.io.Serializable;

public class UserModel implements Serializable {
    private String user_id;
    private String user_name;
    private String user_type;
    private String user_specialization_id_fk;
    private String ar_user_specialization;
    private String en_user_specialization;
    private double rate;
    private String user_full_name;
    private String user_phone;
    private String user_email;
    private String user_photo;
    private String user_token_id;
    private String user_google_lat;
    private String user_google_long;
    private String user_city;
    private String user_address;
    private String user_country;
    private String id_country;
    private String ar_name;
    private String en_name;
    private String ar_city_title;
    private String en_city_title;
    private int success_login;
    private int success_signup;
    private int success_update;
    private int success_update_pass;

    public String getUser_specialization_id_fk() {
        return user_specialization_id_fk;
    }


    public int getSuccess_signup() {
        return success_signup;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getUser_name() {
        return user_name;
    }

    public String getUser_type() {
        return user_type;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_email() {
        return user_email;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getUser_token_id() {
        return user_token_id;
    }

    public String getUser_google_lat() {
        return user_google_lat;
    }

    public String getUser_google_long() {
        return user_google_long;
    }

    public String getUser_city() {
        return user_city;
    }

    public String getUser_address() {
        return user_address;
    }

    public String getUser_country() {
        return user_country;
    }

    public String getId_country() {
        return id_country;
    }

    public String getAr_name() {
        return ar_name;
    }

    public String getEn_name() {
        return en_name;
    }

    public String getAr_city_title() {
        return ar_city_title;
    }

    public String getEn_city_title() {
        return en_city_title;
    }

    public int getSuccess_login() {
        return success_login;
    }

    public String getAr_user_specialization() {
        return ar_user_specialization;
    }

    public String getEn_user_specialization() {
        return en_user_specialization;
    }

    public double getRate() {
        return rate;
    }

    public int getSuccess_update() {
        return success_update;
    }

    public int getSuccess_update_pass() {
        return success_update_pass;
    }
}
