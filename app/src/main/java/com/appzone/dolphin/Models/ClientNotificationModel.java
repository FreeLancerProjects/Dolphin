package com.appzone.dolphin.Models;

import java.io.Serializable;

public class ClientNotificationModel implements Serializable{
    private String id_order;
    private String client_id_fk;
    private String technical_id_fk;
    private String service_id_fk;
    private String order_status;
    private String date_notification;
    private String technical_arrival;
    private String order_date;
    private String user_full_name;
    private String user_photo;
    private String user_phone;
    private String ar_user_specialization;
    private String en_user_specialization;
    private String services_logo;


    public String getId_order() {
        return id_order;
    }

    public String getClient_id_fk() {
        return client_id_fk;
    }

    public String getTechnical_id_fk() {
        return technical_id_fk;
    }

    public String getService_id_fk() {
        return service_id_fk;
    }

    public String getOrder_status() {
        return order_status;
    }

    public String getDate_notification() {
        return date_notification;
    }

    public String getTechnical_arrival() {
        return technical_arrival;
    }

    public String getOrder_date() {
        return order_date;
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
