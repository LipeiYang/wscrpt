package com.qualicom.wscrpt.process;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.qualicom.wscrpt.domain.AcctData;
import com.qualicom.wscrpt.finder.AcctDataFinder;
import com.qualicom.wscrpt.test.DbTtest;
import com.qualicom.wscrpt.utils.ApMapUtil;
import com.qualicom.wscrpt.utils.CacheAcctDataPool;
import com.qualicom.wscrpt.utils.ConfigFileReader;
import com.qualicom.wscrpt.utils.DateUtil;
import com.qualicom.wscrpt.utils.IntervalMapUtil;
import com.qualicom.wscrpt.utils.LinuxSpecialCharFilter;
import com.qualicom.wscrpt.utils.RptTyp;
import com.qualicom.wscrpt.vo.ApInfo;
import com.qualicom.wscrpt.vo.RptContent;
import com.qualicom.wscrpt.vo.RptNode;

public class GenRpts {
	
	Date rptDate;
	String rptPath;
	String rptIntvlPath;
	String concurIntvlPath;
	
	Set<RptTyp> rptTypOfDay;
	ApplicationContext context ;
	BeanFactory factory;
	RptNode rptTree;
	int pageSize = 2000;
	Map<RptTyp, List<Date>> rptTypDateMap;
	
	int out_intvl;
	int out_concur_intvl;
	String out_ssid;
	String out_loc;
	RptCtntCsvWriter writer;
	
	IntervalMapUtil intvalMapUtil;
	IntervalMapUtil intvalConcurMapUtil;
	public IntervalMapUtil getIntvalMapUtil() {
		return intvalMapUtil;
	}
	public void setIntvalMapUtil(IntervalMapUtil intvalMapUtil) {
		this.intvalMapUtil = intvalMapUtil;
	}
	public IntervalMapUtil getIntvalConcurMapUtil() {
		return intvalConcurMapUtil;
	}
	public void setIntvalConcurMapUtil(IntervalMapUtil intvalConcurMapUtil) {
		this.intvalConcurMapUtil = intvalConcurMapUtil;
	}

	ApMapUtil apMapUtil;
	public GenRpts(String irptDate, String irptPath) throws Exception {
		
		rptPath = irptPath;
		writer = RptCtntCsvWriter.getInstance();
		rptTree = new RptNode();
		try {
			rptDate = DateUtil.str2Dt(irptDate);
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			Logger.getLogger(GenRpts.class).error("Parse Date: "+ irptDate + " failed");
			e1.printStackTrace();
			throw e1;
			
		}
		
		context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		
		rptTypDateMap = new HashMap<RptTyp,List<Date>>();
		
		factory = context;
		intvalMapUtil = context.getBean("intervalUtil",IntervalMapUtil.class);
		intvalConcurMapUtil = context.getBean("concurIntervalUtil",IntervalMapUtil.class);
		apMapUtil = context.getBean("mapUtil",ApMapUtil.class);
//		try {
//			if(rptIntvlPath!=null && !rptIntvlPath.equals("")){			
//					intvalMapUtil = new IntervalMapUtil(new ConfigFileReader(rptIntvlPath,false));			
//				}			
//			if(concurIntvlPath!=null && !concurIntvlPath.equals("")){
//				intvalConcurMapUtil = new IntervalMapUtil(new ConfigFileReader(concurIntvlPath,false));
//			}
//		}
//		catch(Exception e) {			
//			e.printStackTrace();
//		}
	}
	public void buildRptTree() throws Exception{
		
		Map<String,Set<RptTyp>> rptDateTypeMap = RptGenHelper.createRptDateTypeMap(rptDate);
		// Gene RptType to Date List for help during output report
		for(String date : rptDateTypeMap.keySet()){
			Set<RptTyp> rptSet = rptDateTypeMap.get(date);
			for(RptTyp rptTyp: rptSet){
				List<Date> dateSet = rptTypDateMap.get(rptTyp);
				if(dateSet==null){
					dateSet = new ArrayList<Date>();
					rptTypDateMap.put(rptTyp, dateSet);
				}
				try {
					dateSet.add(DateUtil.str2Dt(date));
				} catch (ParseException e) {
					Logger.getLogger(GenRpts.class).error("Parse date in buildRpt Tree failed. Date:" + date);
					if(Logger.getLogger(GenRpts.class).isDebugEnabled())
					{
						e.printStackTrace();
					}
					throw e;
				}
			}
		}
		for(RptTyp rptTyp : rptTypDateMap.keySet()){
			List<Date> dateList = rptTypDateMap.get(rptTyp);
			Collections.sort(dateList, new Comparator<Date>(){
				 @Override
				 public int compare(Date d1, Date d2) {
					 return d1.compareTo(d2);				 
				 }	    
				});
			rptTypDateMap.put(rptTyp, dateList);
		}
		List<String> OrderedDate = new ArrayList<String>(rptDateTypeMap.keySet());
		Collections.sort(OrderedDate);
		for(String dateStr : OrderedDate){
			rptTypOfDay = rptDateTypeMap.get(dateStr);
			generateRptByDay(dateStr);
		}
		
	}
	public void genRptFromTree() throws IOException, ParseException{
		Map<Object,RptNode> hMap = rptTree.getHierarchyMap();
		if(hMap!=null)
			genRptSsidNode(hMap,rptPath);
	}
	
	private void genRptSsidNode(Map<Object,RptNode> hMap,String rptPath) throws IOException, ParseException{
		for(Object ssid : hMap.keySet()){				
			String tmpRptPath = rptPath+"/"+LinuxSpecialCharFilter.removeSpecChar((String)ssid);
			File ssidDir = new File(tmpRptPath);
			if(ssidDir.exists()==false){
				if(!ssidDir.mkdir()){
					Logger.getLogger(GenRpts.class).error("Failed to gen folder SSID:" + ssid );
					continue;
				}
			}				
			String ssidDateStr = DateUtil.DtToStr(rptDate);
			File dateUnderSsidDir = new File(tmpRptPath + "/" + ssidDateStr);
			if(dateUnderSsidDir.exists()==false){
				if(!dateUnderSsidDir.mkdir()){
					Logger.getLogger("miss.info").error("Failed to gen date folder under SSID:" + ssid +" for date: " +  ssidDateStr);
					continue;
				}
			}	
			out_intvl = intvalMapUtil.getInterval((String)ssid);
			out_concur_intvl = intvalConcurMapUtil.getInterval((String)ssid);
			out_ssid = (String)ssid;
			writer.setSsid((String)ssid);
			if(hMap.get(ssid).getContentMap()!=null)
				outputRptNodeCtnt(hMap.get(ssid).getContentMap(),dateUnderSsidDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)ssid));
			if(hMap.get(ssid).getHierarchyMap()!=null)
				genRptLocNode(hMap.get(ssid).getHierarchyMap(),dateUnderSsidDir.getAbsolutePath());
			writer.setSsid(null);
		}
		
	}
	private void genRptLocNode(Map<Object,RptNode> hMap,String rptPath) throws IOException, ParseException{
		for(Object loc : hMap.keySet()){
			String tmpRptPath = rptPath+"/"+LinuxSpecialCharFilter.removeSpecChar((String)loc);
			File locDir = new File(tmpRptPath);
			if(locDir.exists()==false){
				if(!locDir.mkdir()){
					Logger.getLogger(GenRpts.class).error("Failed to gen folder under Location :" + LinuxSpecialCharFilter.removeSpecChar((String)loc) );
					continue;
				}
			}						
			writer.setLoc((String)loc);
			if(hMap.get(loc).getContentMap()!=null)
				outputRptNodeCtnt(hMap.get(loc).getContentMap(),locDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)loc+"_"));
			if(hMap.get(loc).getHierarchyMap()!=null)
				genRptApNode(hMap.get(loc).getHierarchyMap(),locDir.getAbsolutePath());
			writer.setLoc(null);
		}
	}
	private void genRptApNode(Map<Object,RptNode> hMap,String rptPath) throws IOException, ParseException{
		for(Object ap : hMap.keySet()){				
			ApInfo apInfo =(ApInfo)ap;
			String tmpRptPath = rptPath+"/"+LinuxSpecialCharFilter.removeSpecChar(apInfo.getApMac());
			File apDir = new File(tmpRptPath);
			if(apDir.exists()==false){
				if(!apDir.mkdir()){
					continue;
				}
			}					
			writer.setAp_des(apInfo.getApDesc());
			writer.setAp_mac(apInfo.getApMac());
			if(hMap.get(apInfo).getContentMap()!=null)
				outputRptNodeCtnt(hMap.get(apInfo).getContentMap(),apDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar(apInfo.getApMac()+"_"));
			if(hMap.get(apInfo).getCncuSessMap()!=null)
				outputRptNodeConcur(hMap.get(apInfo).getCncuSessMap(),apDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar(apInfo.getApMac()+"_"));
			writer.setAp_des(null);
			writer.setAp_mac(null);
		}
	}
	private void outputRptNodeConcur(Map<Date, Set<String>>CncuSessMap, String outputDir,String prefix) throws IOException, ParseException{
		String rptNamePrefix = outputDir+"/"+prefix+DateUtil.DtToStr(rptDate)+"_";
		File rptFile = new File(rptNamePrefix+"concurrent_session.csv");
		int sumConcur = 0;
		writer.openFile(rptFile);
		
		Date targetDate = DateUtil.beginOfToday(rptDate);
		Date endDate = DateUtils.addDays(targetDate, 1);
		
		writer.writeConcurHeader();
		while(targetDate.compareTo(endDate) < 0){
			Set<String> cncuSessSet = CncuSessMap.get(targetDate);
			if(cncuSessSet==null){
				writer.writreConcurLine(targetDate, 0);
			}	
			else{
				writer.writreConcurLine(targetDate,cncuSessSet.size());
				sumConcur += cncuSessSet.size();
			}
			
			targetDate = DateUtils.addMinutes(targetDate,out_concur_intvl);
		}
		writer.writreConcurSumLine(sumConcur/(int)(24*(60.0/out_concur_intvl)));
		writer.flushFile();
	}
	private void outputRptNodeCtnt(Map<Date,RptContent> rptCtntMap, String outputDir,String prefix) throws IOException, ParseException{
		String rptNamePrefix = outputDir+"/"+prefix+DateUtil.DtToStr(rptDate)+"_";
		File rptFile;
		RptContent sumCtnt = new RptContent();
		List<Date> dayList;
		Set<String> connInfoSet = null;
		List<String> connInfoOrder = null;
		Map<String,Integer> connCountTmp = null;
		Map<String,Integer> connCountSum = null;
		
		for(RptTyp rptTyp : rptTypOfDay){
			switch (rptTyp) {
				case MONTH:
					 dayList = rptTypDateMap.get(rptTyp);
					 rptFile = new File(rptNamePrefix+"Monthly.csv");
					 sumCtnt = new RptContent();
					 writer.openFile(rptFile);
					 connInfoSet = new HashSet<String>();
					 
					 connCountSum = new HashMap<String,Integer>();
					 for(Date date : dayList){
							RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));
							if(rptCtnt==null){
								continue;
							}
							connInfoSet.addAll(rptCtnt.getConnInfoMap().keySet());
							//init newly added conn info
							for(String connInfo: rptCtnt.getConnInfoMap().keySet()){
								if(!connCountSum.keySet().contains(connInfo)){
									connCountSum.put(connInfo, 0);
								}
							}
					 }
					 connInfoOrder = new ArrayList<String>(connInfoSet);
					 Collections.sort(connInfoOrder);
					 writer.setDynColmOrder(connInfoOrder);
					 writer.writeHeader();
					for(Date date : dayList){
						connCountTmp =  new HashMap<String,Integer>();						
						RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));						
						if(rptCtnt==null){
							rptCtnt =  new RptContent();
						}
						
						for(String connInfo : connInfoSet){
							Set<String> connTypeSet = RptGenHelper.getConnInfoByType(rptCtnt,connInfo);
							int connCount = connCountSum.get(connInfo);
							if(connTypeSet!=null){
								connCount += connTypeSet.size();
								connCountTmp.put(connInfo,connTypeSet.size());
							}
							else
								connCountTmp.put(connInfo,0);
							connCountSum.put(connInfo, connCount);
							
							
						}
						writer.writeLine(rptCtnt,date,connCountTmp,-1);
					
						sumCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + sumCtnt.getAcctInputOctets()));
						sumCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + sumCtnt.getAcctInputPackets()));
						sumCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + sumCtnt.getAcctOutputOctets()));
						sumCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + sumCtnt.getAcctOutputPackets()));
						sumCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + sumCtnt.getAcctSessionTime()));
						sumCtnt.getUserNameSet().addAll(rptCtnt.getUserNameSet());
						sumCtnt.getCallingStationIdSet().addAll(rptCtnt.getCallingStationIdSet());
						
					}
					break;
				case WEEK:
					dayList = rptTypDateMap.get(rptTyp);
					rptFile = new File(rptNamePrefix+"Weekly.csv");
					sumCtnt = new RptContent();
					writer.openFile(rptFile);
					connInfoSet = new HashSet<String>();
					connCountSum = new HashMap<String,Integer>();
					for(Date date : dayList){
						RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));
						if(rptCtnt==null){
							continue;
						}
						connInfoSet.addAll(rptCtnt.getConnInfoMap().keySet());
						for(String connInfo: rptCtnt.getConnInfoMap().keySet()){
							if(!connCountSum.keySet().contains(connInfo)){
								connCountSum.put(connInfo, 0);
							}
						}
					}
					connInfoOrder = new ArrayList<String>(connInfoSet);
					 Collections.sort(connInfoOrder);
					 writer.setDynColmOrder(connInfoOrder);
					 writer.writeHeader();
					for(Date date : dayList){
						connCountTmp =  new HashMap<String,Integer>();
						RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));
						if(rptCtnt==null){
							rptCtnt =  new RptContent();
						}
						
						for(String connInfo : connInfoSet){
							Set<String> connTypeSet = RptGenHelper.getConnInfoByType(rptCtnt,connInfo);
							int connCount = connCountSum.get(connInfo);
							if(connTypeSet!=null){
								connCount += connTypeSet.size();
								connCountTmp.put(connInfo,connTypeSet.size());
							}
							else
								connCountTmp.put(connInfo,0);
							connCountSum.put(connInfo, connCount);							
						}
						
						writer.writeLine(rptCtnt,date,connCountTmp,-1);
											
						sumCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + sumCtnt.getAcctInputOctets()));
						sumCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + sumCtnt.getAcctInputPackets()));
						sumCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + sumCtnt.getAcctOutputOctets()));
						sumCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + sumCtnt.getAcctOutputPackets()));
						sumCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + sumCtnt.getAcctSessionTime()));
						sumCtnt.getUserNameSet().addAll(rptCtnt.getUserNameSet());
						sumCtnt.getCallingStationIdSet().addAll(rptCtnt.getCallingStationIdSet());
						
					}
					break;
				case DAY:
					rptFile = new File(rptNamePrefix+"Daily.csv");
					sumCtnt = new RptContent();
					writer.openFile(rptFile);
					connCountSum = new HashMap<String,Integer>();
					connInfoSet = new HashSet<String>();
					Date targetDate = DateUtil.beginOfToday(rptDate);
					Date endDate = DateUtils.addDays(targetDate, 1);
					//scan available connetion info
					while(targetDate.compareTo(endDate) < 0){
						RptContent rptCtnt = rptCtntMap.get(targetDate);
						if(rptCtnt==null){
							targetDate = DateUtils.addMinutes(targetDate,out_intvl);
							continue;
						}
						connInfoSet.addAll(rptCtnt.getConnInfoMap().keySet());
						for(String connInfo: rptCtnt.getConnInfoMap().keySet()){
							if(!connCountSum.keySet().contains(connInfo)){
								connCountSum.put(connInfo, 0);
							}
						}
						targetDate = DateUtils.addMinutes(targetDate,out_intvl);
					}					
					targetDate = DateUtil.beginOfToday(rptDate);
					connInfoOrder = new ArrayList<String>(connInfoSet);
					 Collections.sort(connInfoOrder);
					 writer.setDynColmOrder(connInfoOrder);
					 writer.writeHeader();
					while(targetDate.compareTo(endDate) < 0){
						connCountTmp =  new HashMap<String,Integer>();
						RptContent rptCtnt = rptCtntMap.get(targetDate);
						if(rptCtnt==null){
							rptCtnt =  new RptContent();
						}	
						for(String connInfo : connInfoSet){
							Set<String> connTypeSet = RptGenHelper.getConnInfoByType(rptCtnt,connInfo);
							int connCount = connCountSum.get(connInfo);
							if(connTypeSet!=null){
								connCount += connTypeSet.size();
								connCountTmp.put(connInfo,connTypeSet.size());
							}
							else
								connCountTmp.put(connInfo,0);
							connCountSum.put(connInfo, connCount);							
						}
						writer.writeLine(rptCtnt,targetDate,connCountTmp,out_intvl);
						targetDate = DateUtils.addMinutes(targetDate,out_intvl);
						sumCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + sumCtnt.getAcctInputOctets()));
						sumCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + sumCtnt.getAcctInputPackets()));
						sumCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + sumCtnt.getAcctOutputOctets()));
						sumCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + sumCtnt.getAcctOutputPackets()));
						sumCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + sumCtnt.getAcctSessionTime()));
						sumCtnt.getUserNameSet().addAll(rptCtnt.getUserNameSet());
						sumCtnt.getCallingStationIdSet().addAll(rptCtnt.getCallingStationIdSet());
					}					
					break;
			}
			writer.writeSumLine(sumCtnt,connCountSum);
			writer.flushFile();
		}
	}
	
	private void generateRptByDay(String date) {
		int curRecOffset = 0;
		List<AcctData> adList = AcctDataFinder.findAcctData(date, curRecOffset, curRecOffset+pageSize);
		
		while(adList.size()!=0){
			for(AcctData acctData : adList){
				AcctData lastAcctData = CacheAcctDataPool.getInstance().searchLastAcctData(acctData);	
				RptNode ssidNode = RptGenHelper.getRptNodeByObj(rptTree,acctData.getRuckusSsid());				
				addAcctDataToRptNode(ssidNode, acctData,lastAcctData, "ssid");		
				if(acctData.getSessionStatus().equals("E")){
					CacheAcctDataPool.getInstance().removeFromPool(acctData);
				}
				else{
					CacheAcctDataPool.getInstance().addToPool(acctData);
				}
				
			}
			curRecOffset += pageSize;
			adList= AcctDataFinder.findAcctData(date, curRecOffset, curRecOffset+pageSize);
		}
	}
	private  void addAcctDataToRptNode(RptNode curNode , AcctData acctData,AcctData lastAcctData,String rptLevel){
		//if curNode dont contain the data's ssid, add one
		processAcctData(RptGenHelper.getRptNodeContent(curNode),acctData,lastAcctData);
		
		if(rptLevel.equals("ssid")){
			//add to Rpt Content				
			RptNode locNode = RptGenHelper.getRptNodeByObj(curNode,apMapUtil.getLocation(acctData.getCalledStationId()));
			addAcctDataToRptNode(locNode,acctData,lastAcctData,"location");
			//ssidTree.put(acctData.getRuckusSsid(),)
		}
		if(rptLevel.equals("location")){
			//add to Rpt Content						
			RptNode apNode = RptGenHelper.getRptNodeByObj(curNode,apMapUtil.getApInfo(acctData.getCalledStationId()));
			addAcctDataToRptNode(apNode,acctData,lastAcctData,"ap");
			//ssidTree.put(acctData.getRuckusSsid(),)
		}
		if(rptLevel.equals("ap")){
			processAcctDataConcurSess(curNode,acctData,lastAcctData);
			return ;
		}
			
	}
	private void processAcctDataConcurSess(RptNode node,AcctData acctData,AcctData lastAcctData ){
		Set<String> sessSet ;
		int interval = this.intvalConcurMapUtil.getInterval(acctData.getRuckusSsid());
		if( lastAcctData.getTmStmp()==null)						 
			return ;					
		Date concurSnapTime = DateUtil.truncDateByInterval(acctData.getTmStmp(), interval);
			while(concurSnapTime.compareTo(acctData.getTmStmp()) <= 0 && concurSnapTime.compareTo(lastAcctData.getTmStmp())>=0){
				Date beginOfToday = DateUtil.beginOfToday(acctData.getTmStmp());
				if(concurSnapTime.compareTo(beginOfToday) >= 0){
					sessSet = RptGenHelper.getRptConcurSess(node,concurSnapTime);
					sessSet.add(acctData.getAcctSessionId());
					Set<String> conSessInMiddle = RptGenHelper.getRptConcurSess(node,concurSnapTime);							
					conSessInMiddle.add(lastAcctData.getAcctSessionId());
				}
				concurSnapTime = DateUtils.addMinutes(concurSnapTime,-interval);
			}
		
	}
	private void processAcctData(Map<Date,RptContent> rptCtntMap, AcctData acctData,AcctData lastAcctData){
		RptContent rptCtnt;
		Set<String> acctUniqIdSet;
		boolean dayProcessed = false;
		int acctDataTmDiff ;
		for(RptTyp rptTyp : rptTypOfDay){
			switch (rptTyp) {
				case MONTH: 					
					if(dayProcessed){ 
						break;
					}
					rptCtnt = RptGenHelper.getRptContentByDate(rptCtntMap, DateUtil.getDayEnd(acctData.getTmStmp()));
					rptCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + acctData.getAcctInputOctets() - lastAcctData.getAcctInputOctets()));
					rptCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + acctData.getAcctInputPackets() - lastAcctData.getAcctInputPackets()));
					rptCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + acctData.getAcctOutputOctets() - lastAcctData.getAcctOutputOctets()));
					rptCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + acctData.getAcctOutputPackets() - lastAcctData.getAcctOutputPackets()));
					rptCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + acctData.getAcctSessionTime() - lastAcctData.getAcctSessionTime()));
					rptCtnt.getUserNameSet().add(acctData.getUserName());
					acctUniqIdSet = RptGenHelper.getConnInfoByType(rptCtnt, acctData.getConnectInfo());
					acctUniqIdSet.add(acctData.getAcctUniqueId());
					rptCtnt.getCallingStationIdSet().add(acctData.getCallingStationId());
					dayProcessed = true;
					break;			
				case WEEK:
					if(dayProcessed){ 
						break;
					}
					rptCtnt = RptGenHelper.getRptContentByDate(rptCtntMap, DateUtil.getDayEnd(acctData.getTmStmp()));
					rptCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + acctData.getAcctInputOctets() - lastAcctData.getAcctInputOctets()));
					rptCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + acctData.getAcctInputPackets() - lastAcctData.getAcctInputPackets()));
					rptCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + acctData.getAcctOutputOctets() - lastAcctData.getAcctOutputOctets()));
					rptCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + acctData.getAcctOutputPackets() - lastAcctData.getAcctOutputPackets()));
					rptCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + acctData.getAcctSessionTime() - lastAcctData.getAcctSessionTime()));
					rptCtnt.getUserNameSet().add(acctData.getUserName());
					acctUniqIdSet = RptGenHelper.getConnInfoByType(rptCtnt, acctData.getConnectInfo());
					acctUniqIdSet.add(acctData.getAcctUniqueId());
					rptCtnt.getCallingStationIdSet().add(acctData.getCallingStationId());	
					dayProcessed = true;
					break;		
				case DAY:
					int interval = this.intvalMapUtil.getInterval(acctData.getRuckusSsid());
					Date keyTime = DateUtil.truncDateByInterval(acctData.getTmStmp(), interval);
					rptCtnt = RptGenHelper.getRptContentByDate(rptCtntMap,keyTime);
					rptCtnt.setAcctInputOctets((long)(rptCtnt.getAcctInputOctets() + acctData.getAcctInputOctets() - lastAcctData.getAcctInputOctets()));
					rptCtnt.setAcctInputPackets((long)(rptCtnt.getAcctInputPackets() + acctData.getAcctInputPackets() - lastAcctData.getAcctInputPackets()));
					rptCtnt.setAcctOutputOctets((long)(rptCtnt.getAcctOutputOctets() + acctData.getAcctOutputOctets() - lastAcctData.getAcctOutputOctets()));
					rptCtnt.setAcctOutputPackets((long)(rptCtnt.getAcctOutputPackets() + acctData.getAcctOutputPackets() - lastAcctData.getAcctOutputPackets()));
					rptCtnt.setAcctSessionTime((long)(rptCtnt.getAcctSessionTime() + acctData.getAcctSessionTime() - lastAcctData.getAcctSessionTime()));
					rptCtnt.getUserNameSet().add(acctData.getUserName());
					acctUniqIdSet = RptGenHelper.getConnInfoByType(rptCtnt, acctData.getConnectInfo());
					acctUniqIdSet.add(acctData.getAcctUniqueId());
					rptCtnt.getCallingStationIdSet().add(acctData.getCallingStationId());
					if( lastAcctData.getTmStmp()==null)						 
						return ;
					else
						acctDataTmDiff = (int)(acctData.getTmStmp().getTime() - lastAcctData.getTmStmp().getTime())/(1000*60);
					if( acctDataTmDiff > interval){
						Date trunLastDataDate = DateUtil.truncDateByInterval(lastAcctData.getTmStmp(), interval);
						Date beginOfToday = DateUtil.beginOfToday(acctData.getTmStmp());
						trunLastDataDate = DateUtils.addMinutes(trunLastDataDate, interval);
						while(trunLastDataDate.compareTo(keyTime) < 0){
							RptContent rptCtntInMiddle = RptGenHelper.getRptContentByDate(rptCtntMap,trunLastDataDate);							
//							rptCtntInMiddle.setAcctInputOctets((long)(rptCtntInMiddle.getAcctInputOctets() + lastAcctData.getAcctInputOctets() - last2AcctData.getAcctInputOctets()));
//							rptCtntInMiddle.setAcctInputPackets((long)(rptCtntInMiddle.getAcctInputPackets() + lastAcctData.getAcctInputPackets() - last2AcctData.getAcctInputPackets()));
//							rptCtntInMiddle.setAcctOutputOctets((long)(rptCtntInMiddle.getAcctOutputOctets() + lastAcctData.getAcctOutputOctets() - last2AcctData.getAcctOutputOctets()));
//							rptCtntInMiddle.setAcctOutputPackets((long)(rptCtntInMiddle.getAcctOutputPackets() + lastAcctData.getAcctOutputPackets() - last2AcctData.getAcctOutputPackets()));
//							rptCtntInMiddle.setAcctSessionTime((long)(rptCtntInMiddle.getAcctSessionTime() + lastAcctData.getAcctSessionTime() - last2AcctData.getAcctSessionTime()));
							if(trunLastDataDate.compareTo(beginOfToday) >= 0){
								acctUniqIdSet = RptGenHelper.getConnInfoByType(rptCtntInMiddle, lastAcctData.getConnectInfo());
								acctUniqIdSet.add(lastAcctData.getAcctUniqueId());				
								
								rptCtntInMiddle.getCallingStationIdSet().add(lastAcctData.getCallingStationId());
								rptCtntInMiddle.getUserNameSet().add(lastAcctData.getUserName());
							}
							trunLastDataDate = DateUtils.addMinutes(trunLastDataDate,interval);
						}
					}
//					System.out.println("Generating Daily Report");
						break;
			}
		}
	}
	
	public static void main(String[] args) throws Exception {
		String rptDate = args[0];
		String cfgPath = args[1];
		String rptPath = args[2];
		String logPath = args[3];
		
		System.setProperty("cfgfile.path",cfgPath);
		System.setProperty("logfile.path",logPath);
		Logger logger = Logger.getLogger(DbTtest.class);
		
		
		GenRpts rptGenerator = new GenRpts(rptDate,rptPath);
		rptGenerator.buildRptTree();		
		rptGenerator.genRptFromTree();
	}
	
}

