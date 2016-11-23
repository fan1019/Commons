package com.fmh.commons.log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Loggers {
	public static Logger ROOT = LoggerFactory.getLogger(Loggers.class);
	public static Logger STDOUT = LoggerFactory.getLogger("BaseLog");
}
