package com.qualicom.wscrpt.process;

import org.apache.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Deploy {
	public static void main(String[] args) {
		
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		Logger.getLogger(Deploy.class).info("Deployment Success of Database Table");
		
	}
}
