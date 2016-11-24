package com.fmh.commons.mongo;

public enum FilterType {
	eq("eq"),
	ne("ne"),
	gt("gt"),
	gte("gte"),
	lt("lt"),
	lte("lte");

	public String type;

	FilterType(String type){
		this.type = type;
	}
}
