package com.psserver.app.dao;

import java.util.Vector;

import com.psserver.app.model.ComponentModel;
import com.psserver.app.model.PillModel;

public interface Dao<T> {
	
	Boolean create(T t);
	
	String get(String id);
	
	void upload(String id, String filename);
	
	PillModel pillInfo(Vector<String> infoStr);
	
	ComponentModel getComponent(String name);
}
