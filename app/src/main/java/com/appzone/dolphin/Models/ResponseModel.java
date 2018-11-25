package com.appzone.dolphin.Models;

import java.io.Serializable;

public class ResponseModel implements Serializable {
    private int success_contact;
    private int success_location;
    private int success_token_id;
    private int success_logout;
    private int success_evaluation;
    private int success_confirm;
    private int success_resevation;
    private int success_read;
    private int success_movement;
    private int success_payment;
    private String order_step;

    public int getSuccess_contact() {
        return success_contact;
    }

    public int getSuccess_location() {
        return success_location;
    }

    public int getSuccess_token_id() {
        return success_token_id;
    }

    public int getSuccess_logout() {
        return success_logout;
    }

    public int getSuccess_evaluation() {
        return success_evaluation;
    }

    public int getSuccess_confirm() {
        return success_confirm;
    }

    public int getSuccess_resevation() {
        return success_resevation;
    }

    public int getSuccess_read() {
        return success_read;
    }

    public int getSuccess_movement() {
        return success_movement;
    }

    public int getSuccess_payment() {
        return success_payment;
    }

    public String getOrder_step() {
        return order_step;
    }
}
