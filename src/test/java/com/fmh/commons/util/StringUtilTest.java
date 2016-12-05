package com.fmh.commons.util;

import org.junit.Test;

public class StringUtilTest {

	@Test
	public void test1(){
		String str ="   sdhsd   	";
		System.out.println(StringUtil.trim(str));
	}

	@Test
	public void test2(){
		String str = "dsadas";
		System.out.println(StringUtil.isEmpty(str));
	}
}
