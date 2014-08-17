package com.qualicom.wscrpt.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import com.qualicom.wscrpt.vo.ApInfo;

public class ApMapUtil {

	Map<String,ApInfo> csidApList;
	
	Map<String,String> csidLocList;

	Logger missInfoLogger = Logger.getLogger("missinfo");
	
	
	public ApMapUtil(ConfigFileReader apListReader,ConfigFileReader apInfoReader){
		Map<String,ApInfo> apInfoList;		
		Map<String,String> apLocList;	
		
		csidApList = new HashMap<String,ApInfo>();
		csidLocList = new HashMap<String,String>();
		apInfoList = new HashMap<String,ApInfo>();
		apLocList = new HashMap<String,String>();
		
		missInfoLogger.info("Called Station Id");
		
		for(String key : apInfoReader.getArrConfigMap().keySet()){
			List<String[]> apInfoCtnt = apInfoReader.getArrConfigMap().get(key);
			String[] apInfoStrArr =  apInfoCtnt.get(apInfoCtnt.size()-1);
			String ap_desc,ap_loc;

			if(apInfoStrArr.length==0){	
				ap_loc = "Empty Location";
				ap_desc = "";
			}
			else if(apInfoStrArr.length==1){
				ap_loc = "Empty Location";
				ap_desc = apInfoStrArr[0];
			}
			else{
				ap_desc = apInfoStrArr[0];
				ap_loc = apInfoStrArr[1];
			}
			ApInfo tmpApInfo = new ApInfo(key,ap_desc);
			apInfoList.put(key, tmpApInfo);
			apLocList.put(key, ap_loc);
		}
				
		for(String bssid : apListReader.getConfigMap().keySet()){
			List<String> apNames = apListReader.getConfigMap().get(bssid);
			String apName = apNames.get(0);
			
			ApInfo apInfo = apInfoList.get(apName);
			
			if(apInfo==null)
				csidApList.put(bssid, ApInfo.OTHERS);
			else 
				csidApList.put(bssid,apInfo);
						
			String location = apLocList.get(apName);			
			if(location==null || location.equals("")){
				csidLocList.put(bssid, "others");
			}
			else
				csidLocList.put(bssid, apLocList.get(apName));
		}		
	}
	/*
	 * never return null, if not found return "others"
	 * */
	
	public String getLocation(String calledStationId)
	{
		String Location = this.csidLocList.get(calledStationId);
		if(Location == null){
			return "others";
		}
		else
			return Location;
		
	}
	
	/*
	 * never return null, if not found return ApInfo("others","")/ApInfo.OTHERS
	 * */
	public  ApInfo getApInfo(String calledStationId)
	{
		ApInfo apInfo = this.csidApList.get(calledStationId);
		if(apInfo==null){
			missInfoLogger.info(calledStationId);
			return ApInfo.OTHERS; 
		}
		else			
			return apInfo;
	}
}
