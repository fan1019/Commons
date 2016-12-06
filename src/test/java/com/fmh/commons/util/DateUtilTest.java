package com.fmh.commons.util;


import org.junit.Test;

import java.text.ParseException;
import java.util.Date;

public class DateUtilTest {

	@Test
	public void test1(){
		System.out.println(DateUtil.format(new Date(),"yyyy-MM-dd"));
	}

	@Test
	public void test2() throws ParseException {
		System.out.println(DateUtil.format("2016-06-01","yyyy-MM"));
	}

	@Test
	public void test3(){
		System.out.println(DateUtil.getDate(2016,6,15,12,12,6));
	}

	@Test
	public void test4(){
		System.out.println(DateUtil.getTimeMillis(new Date()));
	}
}
