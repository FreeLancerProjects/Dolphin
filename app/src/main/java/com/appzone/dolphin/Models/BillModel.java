package com.appzone.dolphin.Models;

import java.io.Serializable;

public class BillModel implements Serializable {
    private String id_payment;
    private String id_order_fk;
    private String technical_id_fk;
    private String client_id_fk;
    private String work_hours;
    private String hour_cost;
    private String transfer_cost;
    private String spare_parts_cost;
    private String total_cost;
    private String discount;
    private String payment_method;
    private String confirm;
    private String payment_date;
    private String payment_time;


    public String getId_payment() {
        return id_payment;
    }

    public String getId_order_fk() {
        return id_order_fk;
    }

    public String getTechnical_id_fk() {
        return technical_id_fk;
    }

    public String getClient_id_fk() {
        return client_id_fk;
    }

    public String getWork_hours() {
        return work_hours;
    }

    public String getHour_cost() {
        return hour_cost;
    }

    public String getTransfer_cost() {
        return transfer_cost;
    }

    public String getSpare_parts_cost() {
        return spare_parts_cost;
    }

    public String getTotal_cost() {
        return total_cost;
    }

    public String getDiscount() {
        return discount;
    }

    public String getPayment_method() {
        return payment_method;
    }

    public String getConfirm() {
        return confirm;
    }

    public String getPayment_date() {
        return payment_date;
    }

    public String getPayment_time() {
        return payment_time;
    }
}
