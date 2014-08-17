package com.qualicom.wscrpt.vo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class RptContent {
	
	private Set<String> userNameSet;
	private Set<String> callingStationIdSet;
    private Long acctSessionTime ;
    private Long acctInputOctets ;
    private Long acctOutputOctets  ;
    private Long acctInputPackets ;
    private Long acctOutputPackets ;
	//<connect info, Set<acct unique id>>
    private Map<String, Set<String>> connInfoMap;
  


	public RptContent() {
    	acctSessionTime = new Long(0);
    	acctInputOctets = new Long(0);
    	acctOutputOctets = new Long(0);
    	acctInputPackets = new Long(0);
    	acctOutputPackets = new Long(0);
    	callingStationIdSet = new HashSet<String>();
    	userNameSet = new HashSet<String>();
    	connInfoMap = new HashMap<String,Set<String>>();
	}

	  public Map<String, Set<String>> getConnInfoMap() {
			return connInfoMap;
		}

	public void setConnInfoMap(Map<String, Set<String>> connInfoMap) {
		this.connInfoMap = connInfoMap;
	}
    
	public Set<String> getUserNameSet() {
		return userNameSet;
	}
	public void setUserNameSet(Set<String> userNameSet) {
		this.userNameSet = userNameSet;
	}
	public Set<String> getCallingStationIdSet() {
		return callingStationIdSet;
	}
	public void setCallingStationIdSet(Set<String> callingStationIdSet) {
		this.callingStationIdSet = callingStationIdSet;
	}
	public Long getAcctSessionTime() {
		return acctSessionTime;
	}
	public void setAcctSessionTime(Long acctSessionTime) {
		this.acctSessionTime = acctSessionTime;
	}
	public Long getAcctInputOctets() {
		return acctInputOctets;
	}
	public void setAcctInputOctets(Long acctInputOctets) {
		this.acctInputOctets = acctInputOctets;
	}
	public Long getAcctOutputOctets() {
		return acctOutputOctets;
	}
	public void setAcctOutputOctets(Long acctOutputOctets) {
		this.acctOutputOctets = acctOutputOctets;
	}
	public Long getAcctInputPackets() {
		return acctInputPackets;
	}
	public void setAcctInputPackets(Long acctInputPackets) {
		this.acctInputPackets = acctInputPackets;
	}
	public Long getAcctOutputPackets() {
		return acctOutputPackets;
	}
	public void setAcctOutputPackets(Long acctOutputPackets) {
		this.acctOutputPackets = acctOutputPackets;
	}

}
