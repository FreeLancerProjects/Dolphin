package com.appzone.dolphin.Models;

import java.io.Serializable;

public class OrderStateModel implements Serializable {
    private String order_step;
    private int order_payment;

    public String getOrder_step() {
        return order_step;
    }

    public int getOrder_payment() {
        return order_payment;
    }
}
