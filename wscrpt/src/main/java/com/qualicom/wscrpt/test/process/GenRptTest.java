package com.qualicom.wscrpt.test.process;

import org.junit.Test;

import com.qualicom.wscrpt.process.GenRpts;

public class GenRptTest {

	@Test
	public void test() throws Exception {
		String rptDate = "20140831";
		GenRpts.main(new String[]{rptDate,"/Volumes/Mac Data/WorkSpace/sms/Dropbox/sms/project files/sms setup/sms/config","/Users/JK/Documents/Rpt","/Users/JK/Documents/Log"});
	}

}
