package com.digua;

import com.digua.logtest.TestLogger;
import com.digua.netty.NettySocketServer;

public class Main {
	public static void main(String args[]) {
		// TestLogger loggerTest = new TestLogger();
		
		// loggerTest.testLog4jLog();		
		// loggerTest.testSlf4jLog();
		
		try {
			new NettySocketServer().startServer(6080);
		}
		catch(Exception e) {
			
		}
		
	}
}


