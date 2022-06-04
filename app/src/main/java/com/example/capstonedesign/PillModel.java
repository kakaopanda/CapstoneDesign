package com.example.capstonedesign;

public class PillModel {
    public String pill_name;
    public String pill_serial;
    public String is_prescription;
    public String appearance;
    public String business_name;
    public String classify;
    public String component;

    public PillModel() {
        component = "";
    }

    public String toString() {
        return pill_name + pill_serial + is_prescription + appearance + business_name + classify + component;
    }
}
