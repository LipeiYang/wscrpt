package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.qualicom.wscrpt.test.BeanTestBase;
import com.qualicom.wscrpt.utils.ConfigFileReader;
import com.qualicom.wscrpt.utils.IntervalMapUtil;

public class IntervalTest  extends BeanTestBase{

	@Test
	public void test() {
		IntervalMapUtil iMapUtil = new IntervalMapUtil(context.getBean("intervalMapReader",ConfigFileReader.class));
		assertEquals(iMapUtil.getInterval("hgc on air"),Integer.valueOf("5"));
	}

}
