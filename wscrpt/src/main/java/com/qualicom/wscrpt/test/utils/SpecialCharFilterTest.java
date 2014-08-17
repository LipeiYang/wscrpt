package com.qualicom.wscrpt.test.utils;

import static org.junit.Assert.*;

import org.junit.Test;

import com.qualicom.wscrpt.utils.LinuxSpecialCharFilter;

public class SpecialCharFilterTest {
//		/:<>&\|\/
	@Test
	public void test1() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc:cde");
		assertEquals(s,"abccde");
	}
	@Test
	public void test2() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc<cde");
		assertEquals(s,"abccde");
	}
	@Test
	public void test3() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc>cde");
		assertEquals(s,"abccde");
	}
	@Test
	public void test4() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc&cde");
		assertEquals(s,"abccde");
	}
	@Test
	public void test5() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc|cde");
		assertEquals(s,"abccde");
	}
	@Test
	public void test6() {
		String s = LinuxSpecialCharFilter.removeSpecChar("abc cde");
		assertEquals(s,"abc cde");
	}
}
