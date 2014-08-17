package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qualicom.wscrpt.test.BeanTestBase;
import com.qualicom.wscrpt.utils.ApMapUtil;
import com.qualicom.wscrpt.utils.ConfigFileReader;
import com.qualicom.wscrpt.vo.ApInfo;

public class  ApMapUtilTest extends BeanTestBase{
	 ApMapUtil amU ;
	 @Before
	public void Setup(){
		 System.setProperty("cfgfile.path","C:\\Users\\jacky.cheung\\Dropbox\\sms\\project files\\sms setup\\sms\\config\\");
	 }

	@Test
	public void testLocation() {
		 
		 ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		//BeanFactory factory = context;
		amU = new ApMapUtil(context.getBean("apListReader",ConfigFileReader.class),context.getBean("apInfoReader",ConfigFileReader.class));
		assertEquals(amU.getLocation("24-C9-A1-32-8C-C8"),"Lido_Avenue_1_SUNC-OLT5_S04P04");
	}
	@Test
	public void testLocationOthers() {
				
		//BeanFactory factory = context;
		amU = new ApMapUtil(context.getBean("apListReader",ConfigFileReader.class),context.getBean("apInfoReader",ConfigFileReader.class));
		assertEquals(amU.getLocation("C4-01-7C-24-D8-B1"),"others");
	}
	@Test
	public void testApInfo() {
		amU = new ApMapUtil(context.getBean("apListReader",ConfigFileReader.class),context.getBean("apInfoReader",ConfigFileReader.class));
		assertEquals(amU.getApInfo("TESTSSID572").getApDesc(),new ApInfo("C4-01-7C-24-D8-B0","Whampoa_Site3_Pebbles_World_01").getApDesc());
	}
	@Test
	public void testApInfoOthers() {
		amU = new ApMapUtil(context.getBean("apListReader",ConfigFileReader.class),context.getBean("apInfoReader",ConfigFileReader.class));
		assertEquals(amU.getApInfo("TESTSSID588").getApDesc(),new ApInfo("others","").getApDesc());
	}

}
