package com.qualicom.wscrpt.test;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class BeanTestBase {
	protected ApplicationContext context;
	 
	public BeanTestBase(){
		context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
	}
}
