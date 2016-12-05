package com.fmh.commons.util;

public class StringUtil {
	private static String spaces = "[\\s\\ue003\\u00A0\\u1680\\u180E\\u2002-\\u200D\\u202F\\u205F\\u2060\\u3000\\uFEFF]+";

	public static String trim(String test){
		return test == null ? null : test.replaceAll("^" + spaces + "|" + spaces + "$", "");
	}

	public static Boolean isEmpty(String test){
		return test == null || trim(test).isEmpty();
	}
}
