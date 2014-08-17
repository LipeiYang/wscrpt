package com.qualicom.wscrpt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class IntervalMapUtil {
	
	private Map<String, Integer> intervalMap = new HashMap<String, Integer>(); 
	private Integer defualtVlaue = 60; 
	
	/*
	 * return default value, if not found return "others"
	 * */
	
	public IntervalMapUtil(ConfigFileReader intervalReader){
		Map<String,List<String>> intvalMap = intervalReader.getConfigMap();
		for(String ssid : intvalMap.keySet()){
			List<String> intervalStrList = intvalMap.get(ssid);
			intervalMap.put(ssid, Integer.valueOf(intervalStrList.get(intervalStrList.size()-1)));
			
		}
	}
	
	public Integer getInterval(String ssid)
	{
		if(intervalMap.get(ssid)==null)
			return defualtVlaue;
		return intervalMap.get(ssid);
	}
	

}
