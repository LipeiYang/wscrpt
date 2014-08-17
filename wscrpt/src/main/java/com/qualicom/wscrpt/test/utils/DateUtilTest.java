package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.Test;

import com.qualicom.wscrpt.utils.DateUtil;

public class DateUtilTest {

	@Test
	public void test() throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date d = sdf.parse("2014/07/19 20:15:01");
		Date outDate = DateUtil.truncDateByInterval(d, 15);
		assertEquals(sdf.format(outDate),"2014/07/19 20:15:00");
	}

}
