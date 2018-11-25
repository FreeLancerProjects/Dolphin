package com.appzone.dolphin.Models;

import java.io.Serializable;

public class TechnicalOrderModel implements Serializable{

    private String id_order;
    private String service_id_fk;
    private String client_id_fk;
    private String technical_id_fk;
    private String order_status;
    private String order_date;
    private String date_notification;
    private String order_details;
    private String order_voice;
    private String order_video;
    private String order_image;
    private String order_type;
    private String order_date_str;
    private String order_hours;
    private String order_address;
    private String order_address_lat;
    private String order_address_long;
    private String technical_arrival;
    private String date_of_add;
    private String technical_evaluation;
    private String evaluation_note;
    private String office_evaluation;
    private String user_full_name;
    private String user_photo;
    private String user_phone;
    private String user_state;
    private String customer_satisfaction_level;
    private String ar_user_specialization;
    private String en_user_specialization;
    private String services_logo;

    public String getId_order() {
        return id_order;
    }

    public String getService_id_fk() {
        return service_id_fk;
    }

    public String getClient_id_fk() {
        return client_id_fk;
    }

    public String getTechnical_id_fk() {
        return technical_id_fk;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getOrder_date() {
        return order_date;
    }

    public String getDate_notification() {
        return date_notification;
    }

    public String getOrder_details() {
        return order_details;
    }

    public String getOrder_voice() {
        return order_voice;
    }

    public String getOrder_video() {
        return order_video;
    }

    public String getOrder_image() {
        return order_image;
    }

    public String getOrder_type() {
        return order_type;
    }

    public String getOrder_date_str() {
        return order_date_str;
    }

    public String getOrder_hours() {
        return order_hours;
    }

    public String getOrder_address() {
        return order_address;
    }

    public String getOrder_address_lat() {
        return order_address_lat;
    }

    public String getOrder_address_long() {
        return order_address_long;
    }

    public String getTechnical_arrival() {
        return technical_arrival;
    }

    public String getDate_of_add() {
        return date_of_add;
    }

    public String getTechnical_evaluation() {
        return technical_evaluation;
    }

    public String getEvaluation_note() {
        return evaluation_note;
    }

    public String getOffice_evaluation() {
        return office_evaluation;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public String getUser_photo() {
        return user_photo;
    }

    public String getUser_phone() {
        return user_phone;
    }

    public String getUser_state() {
        return user_state;
    }

    public String getCustomer_satisfaction_level() {
        return customer_satisfaction_level;
    }

    public String getAr_user_specialization() {
        return ar_user_specialization;
    }

    public String getEn_user_specialization() {
        return en_user_specialization;
    }

    public String getServices_logo() {
        return services_logo;
    }
}
