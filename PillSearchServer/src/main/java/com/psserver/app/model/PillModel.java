package com.psserver.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// ¾Ë¾à ¸ðµ¨
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class PillModel {
	public String pill_name;
	public String pill_serial;
	public String is_prescription;
	public String appearance;
	public String business_name;
	public String classify;
	public String component;
}