package com.qualicom.wscrpt.utils;

public class LinuxSpecialCharFilter {
	public static String removeSpecChar(String s){
		return s.replaceAll("[:<>&|/]", "");
	}
}
