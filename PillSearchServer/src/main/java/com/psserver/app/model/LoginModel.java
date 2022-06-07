package com.psserver.app.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

// ·Î±×ÀÎ ¸ðµ¨
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class LoginModel {
	public String id;
	public String pw;
}