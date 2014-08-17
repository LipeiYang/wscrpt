package com.qualicom.wscrpt.test;

import java.text.ParseException;
import java.util.Date;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qualicom.wscrpt.domain.AcctData;
import com.qualicom.wscrpt.utils.DateUtil;

public class DbTtest {
	public static void main(String[] args) throws ParseException {
		System.out.println(args[0]);
		System.setProperty("cfgfile.path",args[0]);
		System.setProperty("logfile.path",args[1]);
		Logger logger = Logger.getLogger(DbTtest.class);
		ApplicationContext context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		logger.info("hello,@@");
		BeanFactory factory = context;
//		yyyy/MM/dd_HH:mm:ss
//		AcctData u = new AcctData();
//		u.setUserName("ccc");
//		u.setAcctUniqueId("ccc");
//		u.persist();
		
		AcctData h = new AcctData();
		h.setAcctInputOctets(15);
		h.setAcctUniqueId("fff");
		h.setAcctInputOctets(1);
		h.setAcctInputPackets(1);
		h.setAcctOutputOctets(1);
		h.setAcctOutputPackets(1);
		h.setAcctSessionTime(1);
		h.setAcctSessionId("fff");
		h.setAcctUniqueId("dddccc");
		h.setCalledStationId("TESTSSID572");
		h.setCallingStationId("ccc");
		h.setConnectInfo("802.11/G");
		h.setId((long)5);
		h.setRuckusSsid("hgc on air");
		h.setUserName("Jacky");
		h.setSessionStatus("S");
		
		Date curDate = DateUtil.strKey2Dt("2014/08/30_23:49:00");
		h.setTmStmp(curDate);
		h.persist();
		
		h = new AcctData();
		h.setAcctInputOctets(7);
		h.setAcctUniqueId("fff");
		h.setAcctInputOctets(7);
		h.setAcctInputPackets(7);
		h.setAcctOutputOctets(7);
		h.setAcctOutputPackets(7);
		h.setAcctSessionId("fff");
		h.setAcctUniqueId("dddccc");
		h.setCalledStationId("TESTSSID572");
		h.setCallingStationId("ccc");
		h.setConnectInfo("802.11/K");
		h.setId((long)6);
		h.setRuckusSsid("hgc on air");
		h.setUserName("Jacky");
		h.setSessionStatus("U");
		h.setAcctSessionTime(10);
		curDate = DateUtil.strKey2Dt("2014/08/31_01:10:00");
		h.setTmStmp(curDate);
		h.persist();

		h = new AcctData();
		h.setAcctInputOctets(15);
		h.setAcctUniqueId("fff");
		h.setAcctInputOctets(15);
		h.setAcctInputPackets(15);
		h.setAcctOutputOctets(15);
		h.setAcctOutputPackets(15);
		h.setAcctSessionId("fff");
		h.setAcctUniqueId("dddccc");
		h.setCalledStationId("TESTSSID572");
		h.setCallingStationId("ccc");
		h.setConnectInfo("802.11/G");
		h.setId((long)7);
		h.setRuckusSsid("hgc on air");
		h.setUserName("Jacky");
		h.setSessionStatus("E");
		h.setAcctSessionTime(10);
		curDate = DateUtil.strKey2Dt("2014/08/27_01:15:00");
		h.setTmStmp(curDate);
		h.persist();
		
//		h = new AcctData();
//		h.setAcctInputOctets(0);
//		h.setAcctUniqueId("eee");
//		h.setAcctInputOctets(0);
//		h.setAcctInputPackets(0);
//		h.setAcctOutputOctets(0);
//		h.setAcctOutputPackets(0);
//		h.setAcctSessionId("eee");
//		h.setAcctUniqueId("dddbbb");
//		h.setCalledStationId("TESTSSID726");
//		h.setCallingStationId("bbb");
//		h.setConnectInfo("802.11/N");
//		h.setId((long)3);
//		h.setRuckusSsid("hgc on air");
//		h.setUserName("Jackal");
//		h.setSessionStatus("S");
//		h.setAcctSessionTime(0);
//		curDate = DateUtil.strKey2Dt("2014/08/27_02:10:00");
//		h.setTmStmp(curDate);
//		h.persist();
//		
//		h = new AcctData();
//		h.setAcctInputOctets(100);
//		h.setAcctUniqueId("eee");
//		h.setAcctInputOctets(100);
//		h.setAcctInputPackets(100);
//		h.setAcctOutputOctets(100);
//		h.setAcctOutputPackets(100);
//		h.setAcctSessionId("eee");
//		h.setAcctUniqueId("dddbbb");
//		h.setCalledStationId("TESTSSID726");
//		h.setCallingStationId("bbb");
//		h.setConnectInfo("802.11/N");
//		h.setId((long)4);
//		h.setRuckusSsid("hgc on air");
//		h.setUserName("Jackal");
//		h.setSessionStatus("U");
//		h.setAcctSessionTime(100);
//		curDate = DateUtil.strKey2Dt("2014/08/27_07:10:00");
//		h.setTmStmp(curDate);
//		h.persist();
//		
//		
//		h = new AcctData();
//		h.setAcctInputOctets(109);
//		h.setAcctUniqueId("eee");
//		h.setAcctInputOctets(109);
//		h.setAcctInputPackets(109);
//		h.setAcctOutputOctets(109);
//		h.setAcctOutputPackets(109);
//		h.setAcctSessionId("eee");
//		h.setAcctUniqueId("dddbbb");
//		h.setCalledStationId("TESTSSID726");
//		h.setCallingStationId("bbb");
//		h.setConnectInfo("802.11/N");
//		h.setId((long)4);
//		h.setRuckusSsid("hgc on air");
//		h.setUserName("Jackal");
//		h.setSessionStatus("E");
//		h.setAcctSessionTime(109);
//		curDate = DateUtil.strKey2Dt("2014/08/27_08:10:00");
//		h.setTmStmp(curDate);
//		h.persist();


//		h.setAcctInputOctets(19);
//		h.setAcctUniqueId("ccc");
//		h.persist();
//		h.setTmStmp(new Date());
//		h.persist();
		
//		for(AcctData r: AcctDataFinder.findAcctData("20140730",1,4))
//		{
//			System.out.println(AcctDataFinder.findLastOne("20140730",r.getAcctUniqueId()));
//		}
		
//		System.out.println("====");
//		System.out.println(AcctDataFinder.findLastOne("20140720", "ccc"));
//		List<Object[]> clmProvRawLs = UniqueUserFinder.findNoOfUsers(monthStart, monthEnd).getResultList();

	}
}
