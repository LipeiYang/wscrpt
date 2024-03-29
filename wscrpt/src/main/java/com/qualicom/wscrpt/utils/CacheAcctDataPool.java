package com.qualicom.wscrpt.utils;


import gnu.trove.map.hash.THashMap;

import java.util.Map;

import org.apache.commons.lang3.time.DateUtils;

import com.qualicom.wscrpt.domain.AcctData;
import com.qualicom.wscrpt.finder.AcctDataFinder;


public class CacheAcctDataPool {

	public static CacheAcctDataPool instance;
	
	public static CacheAcctDataPool getInstance(){
		if (instance==null){
			instance = new CacheAcctDataPool();
		}
		return instance;
	}
		
	
	Map<String,AcctData> lastAcctDataPool = new THashMap<String,AcctData>();

	public void addToPool(AcctData acctData){
		lastAcctDataPool.put(acctData.getAcctSessionId(), acctData);
		
	}
	public void removeFromPool(AcctData acctData){
		lastAcctDataPool.remove(acctData.getAcctSessionId());
		
	}
	public AcctData searchLastAcctData(AcctData acctData){
		AcctData data = lastAcctDataPool.get(acctData.getAcctSessionId());
		//Try to get data from yesterday 
		if(data == null){
			String lastDateSuffix = DateUtil.DtToStr(DateUtils.addDays(acctData.getTmStmp(),-1));
			if(!acctData.getSessionStatus().equals("S"))
				data = AcctDataFinder.findLastOne(lastDateSuffix, acctData.getAcctUniqueId());
			if(data==null){
				data = new AcctData();
				data.setAcctInputOctets(0L);
				data.setAcctInputPackets(0L);
				data.setAcctOutputOctets(0L);
				data.setAcctOutputPackets(0L);
				data.setAcctSessionId(acctData.getAcctSessionId());
				data.setAcctSessionTime(0);
				data.setAcctUniqueId(acctData.getAcctUniqueId());
				data.setCalledStationId(acctData.getCalledStationId());
				data.setCallingStationId(acctData.getCallingStationId());
				data.setUserName(acctData.getUserName());
				data.setRuckusSsid(acctData.getRuckusSsid());
				data.setSessionStatus(acctData.getSessionStatus());
				data.setConnectInfo(acctData.getConnectInfo());
			}
		}
		return data;
	}

}
