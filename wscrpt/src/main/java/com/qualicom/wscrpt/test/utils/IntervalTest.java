package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qualicom.wscrpt.utils.ConfigFileReader;
import com.qualicom.wscrpt.utils.IntervalMapUtil;

public class IntervalTest  {
	@Before
	public void sysSetup(){
		System.setProperty("cfgfile.path","/Volumes/Mac Data/WorkSpace/sms/Dropbox/sms/issue");
		System.setProperty("logfile.path","/Users/JK/Documents/Log");
	}
	@Test
	public void test() {
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		IntervalMapUtil iMapUtil = new IntervalMapUtil(context.getBean("intervalMapReader",ConfigFileReader.class));
		assertEquals(iMapUtil.getInterval("HKJC"),Integer.valueOf("15"));
	}

}
