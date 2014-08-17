package com.qualicom.wscrpt.test.process;

import org.junit.Test;

import com.qualicom.wscrpt.process.GenRpts;

public class GenRptTest {

	@Test
	public void test() {
		String rptDate = "20140830";
		GenRpts.main(new String[]{"20140830","/Volumes/Mac Data/WorkSpace/sms/Dropbox/sms/project files/sms setup/sms/config","/Users/JK/Documents/Rpt","/Users/JK/Documents/Log"});
	}

}
