package com.qualicom.wscrpt.process;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
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
import com.qualicom.wscrpt.finder.DBHashSet;
import com.qualicom.wscrpt.utils.AcctDataUtil;
import com.qualicom.wscrpt.utils.ApMapUtil;
import com.qualicom.wscrpt.utils.CacheAcctDataPool;
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
	
	long mem_log = Long.MAX_VALUE;
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
			Logger.getLogger(GenRpts.class).error("Parse Date: "+ irptDate + " failed");
			e1.printStackTrace();
			throw e1;
			
		}
		
		context = new ClassPathXmlApplicationContext("META-INF/spring/applicationContext.xml");
		
		rptTypDateMap = new THashMap<RptTyp,List<Date>>();
		
		factory = context;
		intvalMapUtil = context.getBean("intervalUtil",IntervalMapUtil.class);
		intvalConcurMapUtil = context.getBean("concurIntervalUtil",IntervalMapUtil.class);
		apMapUtil = context.getBean("mapUtil",ApMapUtil.class);
	}
	public boolean buildRptTree(RptTyp genTyp) throws Exception{
		
		Map<String,Set<RptTyp>> rptDateTypeMap = RptGenHelper.createRptDateTypeMap(rptDate,genTyp);
		if(rptDateTypeMap.size()==0){
			return false;
		}
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
		
		return true;
	}
	public void genRptFromTree() throws IOException, ParseException{
		Map<Object,RptNode> hMap = rptTree.getHierarchyMap();
		if(hMap!=null)
			genRptSsidNode(hMap,rptPath);
		System.out.println(mem_log/1024/1024);
	}
	
	private void genRptSsidNode(Map<Object,RptNode> hMap,String rptPath) throws IOException, ParseException{
		Set ssidSet = hMap.keySet();
		for(Object ssid : ssidSet){		
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
			System.out.println("SSID LEVEL:"+Runtime.getRuntime().freeMemory()/(1024*1024));
			if(hMap.get(ssid).getContentMap()!=null){
				outputRptNodeCtnt(hMap.get(ssid).getContentMap(),dateUnderSsidDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)ssid+"_"));
				hMap.get(ssid).getContentMap().clear();
			}
			if(hMap.get(ssid).getHierarchyMap()!=null){
				genRptLocNode(hMap.get(ssid).getHierarchyMap(),dateUnderSsidDir.getAbsolutePath());
				hMap.get(ssid).getHierarchyMap().clear();
			}
			if(hMap.get(ssid).getCncuSessMap()!=null){
				outputRptNodeConcur(hMap.get(ssid).getCncuSessMap(),dateUnderSsidDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)ssid+"_"));
				hMap.get(ssid).getCncuSessMap().clear();
			}
			writer.setSsid(null);
			mem_log = Runtime.getRuntime().freeMemory() < mem_log ? Runtime.getRuntime().freeMemory() :mem_log;

			hMap.put(ssid,null);
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
			if(hMap.get(loc).getContentMap()!=null){
				outputRptNodeCtnt(hMap.get(loc).getContentMap(),locDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)loc+"_"));
				hMap.get(loc).getContentMap().clear();
			}
			if(hMap.get(loc).getHierarchyMap()!=null){
				genRptApNode(hMap.get(loc).getHierarchyMap(),locDir.getAbsolutePath());
				hMap.get(loc).getHierarchyMap().clear();
			}
			if(hMap.get(loc).getCncuSessMap()!=null){
				outputRptNodeConcur(hMap.get(loc).getCncuSessMap(),locDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar((String)loc+"_"));
				hMap.get(loc).getCncuSessMap().clear();
			}
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
			if(hMap.get(apInfo).getContentMap()!=null){
				outputRptNodeCtnt(hMap.get(apInfo).getContentMap(),apDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar(apInfo.getApMac()+"_"));
				hMap.get(apInfo).getContentMap().clear();
			}
			if(hMap.get(apInfo).getCncuSessMap()!=null){
				outputRptNodeConcur(hMap.get(apInfo).getCncuSessMap(),apDir.getAbsolutePath(),LinuxSpecialCharFilter.removeSpecChar(apInfo.getApMac()+"_"));
				hMap.get(apInfo).getCncuSessMap().clear();
			}
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
		Map<String,Set<String>> connCountSum = null;
		
		for(RptTyp rptTyp : rptTypOfDay){
			switch (rptTyp) {
				case MONTH:
					genReportDaily(rptCtntMap,rptTyp,outputDir,prefix,"Monthly.csv");					 
					break;
				case WEEK:
					genReportDaily(rptCtntMap,rptTyp,outputDir,prefix,"Weekly.csv");
					break;
				case DAY:
					rptFile = new File(rptNamePrefix+"Daily.csv");
					sumCtnt = new RptContent();
					writer.openFile(rptFile);
					connCountSum = new THashMap<String,Set<String>>();
					connInfoSet = new THashSet<String>();
					Date targetDate = DateUtil.beginOfToday(rptDate);
					Date endDate = DateUtils.addDays(targetDate, 1);
					//scan available connection info
					while(targetDate.compareTo(endDate) < 0){
						RptContent rptCtnt = rptCtntMap.get(targetDate);
						if(rptCtnt==null){
							targetDate = DateUtils.addMinutes(targetDate,out_intvl);
							continue;
						}
						connInfoSet.addAll(rptCtnt.getConnInfoMap().keySet());
						for(String connInfo: rptCtnt.getConnInfoMap().keySet()){
							if(!connCountSum.keySet().contains(connInfo)){
								connCountSum.put(connInfo, new THashSet<String>());
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
						connCountTmp =  new THashMap<String,Integer>();
						RptContent rptCtnt = rptCtntMap.get(targetDate);
						if(rptCtnt==null){
							rptCtnt =  new RptContent();
						}	
						for(String connInfo : connInfoSet){
							Set<String> connTypeSet = RptGenHelper.getConnInfoByType(rptCtnt,connInfo);
							Set<String> connCount = connCountSum.get(connInfo);
							if(connTypeSet!=null){
								connCount.addAll(connTypeSet);
								connCountTmp.put(connInfo,connTypeSet.size());
							}
							else
								connCountTmp.put(connInfo,0);
							//connCountSum.put(connInfo, connCount);							
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
					writer.writeSumLine(sumCtnt,connCountSum);
					writer.flushFile();
					break;
			}
			
		}
	}
	private void genReportDaily(Map<Date,RptContent> rptCtntMap,RptTyp rptTyp,String outputDir,String prefix,String rptNameTimeSuffix) throws IOException, ParseException{
		String rptNamePrefix = outputDir+"/"+prefix+DateUtil.DtToStr(rptDate)+"_";
		File rptFile;
		RptContent sumCtnt = new RptContent();
		List<Date> dayList;
		Set<String> connInfoSet = null;
		List<String> connInfoOrder = null;
		Map<String,Integer> connCountTmp = null;
		Map<String,Set<String>> connCountSum = null;
		dayList = rptTypDateMap.get(rptTyp);
		 rptFile = new File(rptNamePrefix+rptNameTimeSuffix);
		 sumCtnt = new RptContent();
		 writer.openFile(rptFile);
		 connInfoSet = new THashSet<String>();
		
		 connCountSum = new THashMap<String,Set<String>>();
		 for(Date date : dayList){
				RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));
				if(rptCtnt==null){
					continue;
				}
				connInfoSet.addAll(rptCtnt.getConnInfoMap().keySet());
				//init newly added conn info
				for(String connInfo: rptCtnt.getConnInfoMap().keySet()){
					if(!connCountSum.keySet().contains(connInfo)){
						connCountSum.put(connInfo, new THashSet<String>());
					}
				}
		 }
		 connInfoOrder = new ArrayList<String>(connInfoSet);
		 Collections.sort(connInfoOrder);
		 writer.setDynColmOrder(connInfoOrder);
		 writer.writeHeader();
		for(Date date : dayList){
			connCountTmp =  new THashMap<String,Integer>();						
			RptContent rptCtnt = rptCtntMap.get(DateUtil.getDayEnd(date));						
			if(rptCtnt==null){
				rptCtnt =  new RptContent();
			}
			
			for(String connInfo : connInfoSet){
				Set<String> connTypeSet = RptGenHelper.getConnInfoByType(rptCtnt,connInfo);
				Set<String> connCount = connCountSum.get(connInfo);
				if(connTypeSet!=null){
					connCount.addAll(connTypeSet);
					connCountTmp.put(connInfo,connTypeSet.size());
				}
				else
					connCountTmp.put(connInfo,0);
				//connCountSum.put(connInfo, connCount);

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
		writer.writeSumLine(sumCtnt,connCountSum);
		writer.flushFile();
		
	}
	
	private void generateRptByDay(String date) {
		int curRecOffset = 0;
		List<AcctData> adList = AcctDataFinder.findAcctData(date, curRecOffset, pageSize);
		
		while(adList.size()!=0){
			for(AcctData acctData : adList){
				AcctData lastAcctData = CacheAcctDataPool.getInstance().searchLastAcctData(acctData);	
				RptNode ssidNode = RptGenHelper.getRptNodeByObj(rptTree,acctData.getRuckusSsid());		
				//System.out.println(Runtime.getRuntime().freeMemory());
				mem_log = Runtime.getRuntime().freeMemory() < mem_log ? Runtime.getRuntime().freeMemory() :mem_log;
				addAcctDataToRptNode(ssidNode, acctData,lastAcctData, "ssid");		
				
				if(acctData.getSessionStatus().equals("E")){
					CacheAcctDataPool.getInstance().removeFromPool(acctData);
				}
				else{
					CacheAcctDataPool.getInstance().addToPool(acctData);
				}
				
			}
			curRecOffset += pageSize;
			adList= AcctDataFinder.findAcctData(date, curRecOffset, pageSize);
		}
	}
	private  void addAcctDataToRptNode(RptNode curNode , AcctData acctData,AcctData lastAcctData,String rptLevel){
		//if curNode dont contain the data's ssid, add one
		processAcctData(RptGenHelper.getRptNodeContent(curNode),acctData,lastAcctData);
		processAcctDataConcurSess(RptGenHelper.getRptConcurSessMap(curNode),acctData,lastAcctData);
		
		if(rptLevel.equals("ssid")){
			//add to Rpt Content				
			RptNode locNode = RptGenHelper.getRptNodeByObj(curNode,apMapUtil.getLocation(AcctDataUtil.getRealCalledStationId(acctData)));
			addAcctDataToRptNode(locNode,acctData,lastAcctData,"location");
		}
		if(rptLevel.equals("location")){
			//add to Rpt Content						
			RptNode apNode = RptGenHelper.getRptNodeByObj(curNode,apMapUtil.getApInfo(AcctDataUtil.getRealCalledStationId(acctData)));
			addAcctDataToRptNode(apNode,acctData,lastAcctData,"ap");

		}
		if(rptLevel.equals("ap")){
			return ;
		}
			
	}
	private void processAcctDataConcurSess(Map<Date,Set<String>> rptConcurCtntMap,AcctData acctData,AcctData lastAcctData ){
		
		int interval = this.intvalConcurMapUtil.getInterval(acctData.getRuckusSsid());
		if( lastAcctData.getTmStmp()==null)						 
			return ;					
		Date concurSnapTime = DateUtil.truncDateByInterval(acctData.getTmStmp(), interval);
		Date beginOfToday = DateUtil.beginOfToday(acctData.getTmStmp());
			while(concurSnapTime.compareTo(acctData.getTmStmp()) <= 0 && concurSnapTime.compareTo(lastAcctData.getTmStmp())>=0){
				if(concurSnapTime.compareTo(beginOfToday) >= 0){
					Set<String> conSessInMiddle = RptGenHelper.getRptConcurSess(rptConcurCtntMap,concurSnapTime);							
					conSessInMiddle.add(lastAcctData.getAcctSessionId());
				}
				concurSnapTime = DateUtils.addMinutes(concurSnapTime,-interval);
			}
		
	}
	private void processAcctData(Map<Date,RptContent> rptCtntMap, AcctData acctData,AcctData lastAcctData){
		RptContent rptCtnt;
		Set<String> acctUniqIdSet;
		int acctDataTmDiff ;
		
		if(rptTypOfDay.contains(RptTyp.MONTH) || rptTypOfDay.contains(RptTyp.WEEK)){
		
					rptCtnt = RptGenHelper.getRptContentByDate(rptCtntMap, DateUtil.getDayEnd(acctData.getTmStmp()));					
					processCtntData(rptCtnt,acctData,lastAcctData);
		}
		
		if(rptTypOfDay.contains(RptTyp.DAY)){
			
					int interval = this.intvalMapUtil.getInterval(acctData.getRuckusSsid());
					Date keyTime = DateUtil.truncDateByInterval(acctData.getTmStmp(), interval);
					rptCtnt = RptGenHelper.getRptContentByDate(rptCtntMap,keyTime);
					
					processCtntData(rptCtnt,acctData,lastAcctData);
	
					if( lastAcctData.getTmStmp()==null)						 
						return;
					else
						acctDataTmDiff = (int)(acctData.getTmStmp().getTime() - lastAcctData.getTmStmp().getTime())/(1000*60);
					if( acctDataTmDiff > interval){
						Date trunLastDataDate = DateUtil.truncDateByInterval(lastAcctData.getTmStmp(), interval);
						Date beginOfToday = DateUtil.beginOfToday(acctData.getTmStmp());
						trunLastDataDate = DateUtils.addMinutes(trunLastDataDate, interval);
						while(trunLastDataDate.compareTo(keyTime) < 0){
							RptContent rptCtntInMiddle = RptGenHelper.getRptContentByDate(rptCtntMap,trunLastDataDate);							
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
						
			
		}
	}
	private void processCtntData(RptContent rptCtnt,AcctData acctData,AcctData lastAcctData){
		Set<String> acctUniqIdSet;
		long acctInputOctets,acctInputPackets,acctOutputOctets,acctOutputPackets,acctSessionTime;

		if(acctData.getAcctInputOctets() >= lastAcctData.getAcctInputOctets())
			acctInputOctets = (long)(rptCtnt.getAcctInputOctets() + acctData.getAcctInputOctets() - lastAcctData.getAcctInputOctets());
		else	
			acctInputOctets = (long)(rptCtnt.getAcctInputOctets() + acctData.getAcctInputOctets() );
		
		if(acctData.getAcctInputPackets() >= lastAcctData.getAcctInputPackets())
			acctInputPackets = (long)(rptCtnt.getAcctInputPackets() + acctData.getAcctInputPackets() - lastAcctData.getAcctInputPackets());
		else	
			acctInputPackets = (long)(rptCtnt.getAcctInputPackets() + acctData.getAcctInputPackets() );
		
		if(acctData.getAcctOutputOctets() >= lastAcctData.getAcctOutputOctets())
			acctOutputOctets = (long)(rptCtnt.getAcctOutputOctets() + acctData.getAcctOutputOctets() - lastAcctData.getAcctOutputOctets());
		else	
			acctOutputOctets = (long)(rptCtnt.getAcctOutputOctets() + acctData.getAcctOutputOctets() );
		
		if(acctData.getAcctOutputPackets() >= lastAcctData.getAcctOutputPackets())
			acctOutputPackets = (long)(rptCtnt.getAcctOutputPackets() + acctData.getAcctOutputPackets() - lastAcctData.getAcctOutputPackets());
		else	
			acctOutputPackets = (long)(rptCtnt.getAcctOutputPackets() + acctData.getAcctOutputPackets() );
		if(acctData.getAcctSessionTime() >= lastAcctData.getAcctSessionTime())
			acctSessionTime = (long)(rptCtnt.getAcctSessionTime() + acctData.getAcctSessionTime() - lastAcctData.getAcctSessionTime());
		else	
			acctSessionTime = (long)(rptCtnt.getAcctSessionTime() + acctData.getAcctSessionTime() );
		
		rptCtnt.setAcctInputOctets(acctInputOctets);
		rptCtnt.setAcctInputPackets(acctInputPackets);
		rptCtnt.setAcctOutputOctets(acctOutputOctets);
		rptCtnt.setAcctOutputPackets(acctOutputPackets);
		rptCtnt.setAcctSessionTime(acctSessionTime);
		
		rptCtnt.getUserNameSet().add(acctData.getUserName());
		acctUniqIdSet = RptGenHelper.getConnInfoByType(rptCtnt, acctData.getConnectInfo());
		acctUniqIdSet.add(acctData.getAcctUniqueId());
		rptCtnt.getCallingStationIdSet().add(acctData.getCallingStationId());	
	}
	public static void main(String[] args) throws Exception {
		String rptDate = args[0];
		String cfgPath = args[1];
		String rptPath = args[2];
		String logPath = args[3];
		
		System.setProperty("cfgfile.path",cfgPath);
		System.setProperty("logfile.path",logPath);
		
		Date rptDateTday = DateUtil.str2Dt(rptDate);
		Date boundDay = DateUtil.getDaysBeforeToday(30);
		if(rptDateTday.compareTo(boundDay) < 0){
			Logger boundDaylogger = Logger.getLogger("Main");
			boundDaylogger.error("Input Date " + rptDate + " out of acceptable boundary: " + DateUtil.DtToStr(boundDay));
			return;
		}
		
		GenRpts rptGenerator = new GenRpts(rptDate,rptPath);
		RptTyp[] genOrder = new RptTyp[]{RptTyp.DAY,RptTyp.WEEK,RptTyp.MONTH};
		for(RptTyp typ : genOrder){
			if(rptGenerator.buildRptTree(typ))		
				rptGenerator.genRptFromTree();
			System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
			DBHashSet.resetData();
			System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
		}
	}
	
}

