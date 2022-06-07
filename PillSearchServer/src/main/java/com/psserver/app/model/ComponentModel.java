package com.psserver.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// ÁÖ¼ººÐ ¸ðµ¨
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ComponentModel {
    public String component_code;
    public String component_name;
    public String component_name_eng;
    public String type_code;
    public String type_name;
    public String injection_root;
    public String injection_unit;
    public String injection_day;
}