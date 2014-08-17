package com.qualicom.wscrpt.process;

import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.qualicom.wscrpt.utils.DateUtil;
import com.qualicom.wscrpt.utils.RptTyp;
import com.qualicom.wscrpt.vo.RptContent;
import com.qualicom.wscrpt.vo.RptNode;

public class RptGenHelper {
	public static RptNode getRptNodeByObj(RptNode node,Object obj){
		RptNode nodeToGet = RptGenHelper.getRptNodeHierarchy(node).get(obj);
		if(nodeToGet==null){
			nodeToGet =  new RptNode();
			RptGenHelper.getRptNodeHierarchy(node).put(obj,nodeToGet);
		}
		return nodeToGet;
	}
	public static Set<String> getRptConcurSess(RptNode curNode,Date d){
		Map<Date,Set<String>> conCurSessMap = RptGenHelper.getRptConcurSessMap(curNode);
		Set<String> sessSet = conCurSessMap.get(d);
		if (sessSet==null){
			sessSet = new HashSet<String>();
			conCurSessMap.put(d, sessSet);
		}
		return sessSet;
	}
	public static Set<String> getConnInfoByType(RptContent ctnt, String connType){
		Set<String> acctUniqIdSet = RptGenHelper.getConnInfoMap(ctnt).get(connType);
		if(acctUniqIdSet==null){
			acctUniqIdSet = new HashSet<String>();
			ctnt.getConnInfoMap().put(connType, acctUniqIdSet);
		}
		return acctUniqIdSet;
	}
	public static Map<String,Set<String>> getConnInfoMap(RptContent ctnt){
		Map <String,Set<String>> connInfoMap = ctnt.getConnInfoMap();
		if(connInfoMap==null){
			connInfoMap = new HashMap<String,Set<String>>();
			ctnt.setConnInfoMap(connInfoMap);
		}
		return connInfoMap;
	}
	public static Map<Date,Set<String>> getRptConcurSessMap(RptNode curNode){
		Map<Date,Set<String>> conCurSessMap = curNode.getCncuSessMap();
		if(conCurSessMap==null){
			conCurSessMap = new HashMap<Date,Set<String>>();
			curNode.setCncuSessMap(conCurSessMap);
		}
		return conCurSessMap;
	}
	public static Map<Date,RptContent> getRptNodeContent(RptNode node){
		Map<Date,RptContent> dateCtntMap = node.getContentMap();
		if(dateCtntMap == null){
			dateCtntMap = new HashMap<Date,RptContent>();	
			node.setContentMap(dateCtntMap);
		}
		return dateCtntMap;
	}
	public static RptContent getRptContentByDate(Map<Date,RptContent> rptCtntMap,Date d){
		RptContent dateCtnt = rptCtntMap.get(d);
		if(dateCtnt == null){
			dateCtnt = new RptContent();	
			rptCtntMap.put(d, dateCtnt);
		}
		return dateCtnt;
	}
	public static Map<Object,RptNode> getRptNodeHierarchy(RptNode node){
		Map<Object,RptNode> typeHirayMap = node.getHierarchyMap();
		if(typeHirayMap == null){
			typeHirayMap = new HashMap<Object,RptNode>();	
			node.setHierarchyMap(typeHirayMap); 
		}
		return typeHirayMap;
	}
	private static void addToRptTypSet(List<String> dayOfPeriodRptList,HashMap<String,Set<RptTyp>> rptTypeDateMap,RptTyp targetTyp ){
		for(String date : dayOfPeriodRptList){
			Set<RptTyp> rptTypOfDate = rptTypeDateMap.get(date);
			if(rptTypOfDate==null){
				rptTypOfDate = new HashSet<RptTyp>();
				rptTypeDateMap.put(date, rptTypOfDate);
			}
			rptTypOfDate.add(targetTyp);
		}
	}
	public static HashMap<String,Set<RptTyp>> createRptDateTypeMap(Date inRptDate){
		Set<RptTyp> rptTypeGenable = DateUtil.getRptTyps(inRptDate);
		HashMap<String,Set<RptTyp>> rptTypeDateMap = new HashMap<String,Set<RptTyp>>();
		for(RptTyp dateType : rptTypeGenable){
			if(dateType.compareTo(RptTyp.MONTH)==0){
				List<String> dayOfMonthRptList = Arrays.asList(DateUtil.getDays4Mth(inRptDate));
				addToRptTypSet(dayOfMonthRptList,rptTypeDateMap,RptTyp.MONTH);
			}
			if(dateType.compareTo(RptTyp.WEEK)==0){
				List<String> dayOfWeekRptList = Arrays.asList(DateUtil.getDays4Wek(inRptDate));
				addToRptTypSet(dayOfWeekRptList,rptTypeDateMap,RptTyp.WEEK);
			}
			if(dateType.compareTo(RptTyp.DAY)==0){
				List<String> dayRptList = Arrays.asList(new String[]{DateUtil.DtToStr(inRptDate)});
				addToRptTypSet(dayRptList,rptTypeDateMap,RptTyp.DAY);
			}
		}
		return rptTypeDateMap;
		
	}
	
}
