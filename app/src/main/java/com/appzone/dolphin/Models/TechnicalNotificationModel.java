package com.appzone.dolphin.Models;

import java.io.Serializable;

public class TechnicalNotificationModel implements Serializable {

    private String order_date;
    private String date_notification;
    private String ar_user_specialization;
    private String en_user_specialization;
    private String services_logo;

    public String getOrder_date() {
        return order_date;
    }

    public String getDate_notification() {
        return date_notification;
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
