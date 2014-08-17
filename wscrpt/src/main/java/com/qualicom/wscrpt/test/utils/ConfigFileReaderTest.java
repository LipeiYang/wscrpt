package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.*;

import java.io.IOException;

import org.junit.Test;

import com.qualicom.wscrpt.utils.ConfigFileReader;

public class ConfigFileReaderTest {
	ConfigFileReader cFR;
	@Test
	public void testReadContent() {
		try {
			cFR = new ConfigFileReader("/Volumes/Mac Data/sms/sms content/config/ap_list.csv",false);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(cFR.getConfigCtnt("C4-01-7C-24-D8-B0").contains("TESTSSID572"),true);
		
		
		//cFR = context.getBean("apInfoReader", ConfigFileReader.class);
	}
	@Test
	public void testJumpLine() {
		try {
			cFR = new ConfigFileReader("/Volumes/Mac Data/sms/sms content/config/ap_list.csv",true);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		assertEquals(cFR.getConfigMap().size(),5);
	}

}
