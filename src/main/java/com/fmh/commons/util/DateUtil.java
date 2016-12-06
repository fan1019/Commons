package com.fmh.commons.util;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String format(final Date date, final String format) {
		if (!StringUtil.isEmpty(format) && date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			return dateFormat.format(date);
		}
		return null;
	}

	public static Date format(final String date, final String format) {
		if (!StringUtil.isEmpty(format) && date != null) {
			SimpleDateFormat dateFormat = new SimpleDateFormat(format);
			try {
				return dateFormat.parse(date);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Date getDate(final int year, final int month, final int day, final int hourOfDay, final int minute, final int second) {
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.YEAR, year);
		calendar.set(Calendar.MONTH, month - 1 < 0 ? 0 : month - 1);
		calendar.set(Calendar.DATE, day);
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		return calendar.getTime();
	}

	public static Date getDate(final int year, final int month, final int day) {
		return getDate(year, month, day,0,0,0);
	}

	public static Long getTimeMillis(final Date date){
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		return calendar.getTimeInMillis();
	}
}
