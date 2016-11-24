package com.fmh.commons.mongo;

public enum FilterType {
	eq("eq");

	public String type;

	FilterType(String type){
		this.type = type;
	}
}
