package com.qualicom.wscrpt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


public class IntervalMapUtil {
	
	private Map<String, Integer> intervalMap = new HashMap<String, Integer>(); 
	private Integer defualtValue = 60; 
	private Integer maxValue = 60; 
	
	/*
	 * return default value, if not found return "others"
	 * */
	
	private Logger logger = Logger.getLogger(IntervalMapUtil.class);
	
	public IntervalMapUtil(ConfigFileReader intervalReader){
		
		Map<String,List<String>> intvalMap = intervalReader.getConfigMap();
		for(String ssid : intvalMap.keySet()){
			List<String> intervalStrList = intvalMap.get(ssid);
			
			if(null==intervalStrList||intervalStrList.size()==0)
			{
				logger.error("SSID ["+ssid+"] has no interval value.");
				continue;
			}
			
			int intvl_value = 0;
			try {
				intvl_value = Integer.valueOf(intervalStrList.get(intervalStrList.size() - 1));
			} catch (NumberFormatException e) {
				logger.error("SSID ["+ssid+"] interval value ["+intervalStrList.get(intervalStrList.size() - 1)+"] must be integer.");
				continue;
			}
			
			if(intvl_value % 5 != 0)
			{
				logger.error("SSID ["+ssid+"] interval value must be multiples of 5.");
				continue;
			}
			
			if (intvl_value > maxValue)
			{
				logger.error("SSID ["+ssid+"] interval value can not larger than "+maxValue+".");
				continue;
			}
			
			intervalMap.put(ssid, intvl_value);
		}
	}
	
	public Integer getInterval(String ssid)
	{
		if(intervalMap.get(ssid)==null)
			return defualtValue;
		return intervalMap.get(ssid);
	}
	

}
