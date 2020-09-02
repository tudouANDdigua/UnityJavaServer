package com.digua.logtest;


public class TestLogger {
	private org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(getClass());
	
	private org.slf4j.Logger logger2 = org.slf4j.LoggerFactory.getLogger("slf4jxxx");
	
	public void testLog4jLog() {
		this.logger.debug("HelloWorld"); // 调试信息的时候，不重要;
		this.logger.info("key message"); // info: 关键信息;
		this.logger.error("error message"); // 错误，和警告信息;
	}
	
	public void testSlf4jLog() {
		this.logger2.debug("HelloWorld");
		this.logger2.info("test");
		this.logger2.error("test " + 5);
		this.logger2.error("{}, {}", "blake", "bycw");
	}
}