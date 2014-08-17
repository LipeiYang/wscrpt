package com.qualicom.wscrpt.vo;

import java.util.Date;
import java.util.Map;
import java.util.Set;

public class RptNode {
	/*
	 * daily key for RptContent
	 * 2014/06/15_00:00:00
	 * 2014/06/15_13:00:00
	 * 2014/06/15_23:00:00
	 * 
	 * weekly key for RptContent
	 * 2014/06/15_23:59:59
	 * 2014/06/14_23:59:59
	 * 2014/06/13_23:59:59
	 * 
	 * monthly key for RptContent
	 * 2014/06/30_23:59:59
	 * 2014/06/29_23:59:59
	 * 
	 * */
	private Map<Date, RptContent> contentMap;
	
	private Map<Date, Set<String>> CncuSessMap;
	
	public Map<Date, Set<String>> getCncuSessMap() {
		return CncuSessMap;
	}

	public void setCncuSessMap(Map<Date, Set<String>> cncuSessMap) {
		CncuSessMap = cncuSessMap;
	}

	/*
	 * String for SSID level key
	 * String for location level key
	 * ApInfo for AP level key
	 * 
	 * */
	private Map<Object, RptNode> hierarchyMap;

	public Map<Date, RptContent> getContentMap() {
		return contentMap;
	}

	public void setContentMap(Map<Date, RptContent> contentMap) {
		this.contentMap = contentMap;
	}

	public Map<Object, RptNode> getHierarchyMap() {
		return hierarchyMap;
	}

	public void setHierarchyMap(Map<Object, RptNode> hierarchyMap) {
		this.hierarchyMap = hierarchyMap;
	}
	
}
