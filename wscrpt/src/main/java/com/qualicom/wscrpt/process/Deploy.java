package com.qualicom.wscrpt.process;

import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class Deploy {
	public static void main(String[] args) {
		String rptDate = args[0];
		String cfgPath = args[1];
		String rptPath = args[2];
		String logPath = args[3];
		
		System.setProperty("cfgfile.path",cfgPath);
		System.setProperty("logfile.path",logPath);
		
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		BeanFactory factory = context;
		System.out.println("Deployment Success");
		

	}
}
