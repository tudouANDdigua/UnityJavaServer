package com.test;

public class TestLogger {
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
	public void testLog4jLog() {
		logger.debug("debug内容");
		logger.info("info内容");
	}
	
	private org.slf4j.Logger logger2 = org.slf4j.LoggerFactory.getLogger(getClass());
	
	public void testSlf4jLog() {
		logger2.debug("slf4j debug");
		logger2.info("slf4j debug");
	}
}
