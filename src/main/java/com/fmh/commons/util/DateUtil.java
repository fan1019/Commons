package com.fmh.commons.util;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateUtil {

	public static String format(final Date date, final String format){
		if (!StringUtil.isEmpty(format) && date != null){
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(date);
		}
		return null;
	}

	public static Date format(final String date, final String format){
		if (!StringUtil.isEmpty(format) && date != null){
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			try {
				return dateFormat.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
}
