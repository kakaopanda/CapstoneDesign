package com.example.capstonedesign;

public class ComponentModel {
    public String component_code;
    public String component_name;
    public String component_name_eng;
    public String type_code;
    public String type_name;
    public String injection_root;
    public String injection_unit;
    public String injection_day;

    public String toString() {
        return component_code + component_name + component_name_eng + type_code
               + type_name + injection_root + injection_unit + injection_day;
    }
}
