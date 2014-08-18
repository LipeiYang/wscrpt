package com.qualicom.wscrpt.test;

import java.text.ParseException;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qualicom.wscrpt.domain.AcctData;
import com.qualicom.wscrpt.utils.ConfigFileReader;
import com.qualicom.wscrpt.utils.DateUtil;

public class DbTtest {
	
	private void addData(AcctData h, int i)
	{
		if(h.getAcctInputOctets()==null) h.setAcctInputOctets(0);
		h.setAcctInputOctets(h.getAcctInputOctets()+i);
		
		if(h.getAcctOutputOctets()==null) h.setAcctOutputOctets(0);
		h.setAcctOutputOctets(h.getAcctOutputOctets()+i);
		
		if(h.getAcctInputPackets()==null) h.setAcctInputPackets(0);
		h.setAcctInputPackets(h.getAcctInputPackets()+i);
		
		if(h.getAcctOutputPackets()==null) h.setAcctOutputPackets(0);
		h.setAcctOutputPackets(h.getAcctOutputPackets()+i);
		
		if(h.getAcctSessionTime()==null) h.setAcctSessionTime(0);
		h.setAcctSessionTime(h.getAcctSessionTime()+i);
	}
	
	private String[] connInfos = {"CONNECT 802.11g/n","CONNECT 802.11a/n","CONNECT 802.11a/n/ac"};
	private String[] userNames = {"Jacky","Leo","Kitkat"};
	private String[] ssids = {"hgc on air","hgc free wifi","hgc wifi hotspot"};
	
	private void push2RealTbl()
	{
		for(AcctData o:AcctData.findAllAcctDatas())
		{
	    	System.out.println("insert into acct_data_"+DateUtil.DtToStr(o.getTmStmp())+" select * from acct_data where id = "+o.getId()+";");
		}
	}
	
	Random generator = new Random();
	
	private void gen() throws ParseException
	{
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		for(String called: context.getBean("apListReader",ConfigFileReader.class).getConfigMap().keySet())
		{
			String uid = UUID.randomUUID().toString();
			String userName = userNames[generator.nextInt(userNames.length)];
			String ssid = ssids[generator.nextInt(ssids.length)];
			AcctData h = new AcctData();
			addData(h,0);
			h.setAcctSessionId(uid);
			h.setAcctUniqueId(uid);
			h.setCalledStationId(called);
			h.setCallingStationId(uid);
			h.setConnectInfo(connInfos[generator.nextInt(connInfos.length)]);
			h.setRuckusSsid(ssid);
			h.setUserName(userName);
			h.setSessionStatus("S");
			h.setTmStmp(DateUtil.strKey2Dt("2014/08/27_10:00:00"));
			h.persist();
			h = new AcctData();
			addData(h,10);
			h.setAcctSessionId(uid);
			h.setAcctUniqueId(uid);
			h.setCalledStationId(called);
			h.setCallingStationId(uid);
			h.setConnectInfo(connInfos[generator.nextInt(connInfos.length)]);
			h.setRuckusSsid(ssid);
			h.setUserName(userName);
			h.setSessionStatus("U");
			h.setTmStmp(DateUtil.strKey2Dt("2014/08/27_10:20:00"));
			h.persist();
			h = new AcctData();
			addData(h,20);
			h.setAcctSessionId(uid);
			h.setAcctUniqueId(uid);
			h.setCalledStationId(called);
			h.setCallingStationId(uid);
			h.setConnectInfo(connInfos[generator.nextInt(connInfos.length)]);
			h.setRuckusSsid(ssid);
			h.setUserName(userName);
			h.setSessionStatus("U");
			h.setTmStmp(DateUtil.strKey2Dt("2014/08/27_10:40:00"));
			h.persist();
			h = new AcctData();
			addData(h,30);
			h.setAcctSessionId(uid);
			h.setAcctUniqueId(uid);
			h.setCalledStationId(called);
			h.setCallingStationId(uid);
			h.setConnectInfo(connInfos[generator.nextInt(connInfos.length)]);
			h.setRuckusSsid(ssid);
			h.setUserName(userName);
			h.setSessionStatus("E");
			h.setTmStmp(DateUtil.strKey2Dt("2014/08/27_11:00:00"));
			h.persist();
		}
		push2RealTbl();
	}
	
	public static void main(String[] args) throws ParseException {
		System.out.println(args[0]);
		System.setProperty("cfgfile.path",args[0]);
		System.setProperty("logfile.path",args[1]);
//		new DbTtest().gen();
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		new DbTtest().push2RealTbl();
	}
}
