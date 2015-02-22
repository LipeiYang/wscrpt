package com.qualicom.wscrpt.vo;

import gnu.trove.map.hash.THashMap;

import java.util.Map;
import java.util.Set;

import com.qualicom.wscrpt.finder.DBHashSet;

public class RptContent {
	
	private Set<String> userNameSet;
	private Set<String> callingStationIdSet;
    private long acctSessionTime ;
    private long acctInputOctets ;
    private long acctOutputOctets  ;
    private long acctInputPackets ;
    private long acctOutputPackets ;
	//<connect info, Set<acct unique id>>
    private Map<String, Set<String>> connInfoMap;
  


	public RptContent() {
    	acctSessionTime = 0;
    	acctInputOctets = 0;
    	acctOutputOctets = 0;
    	acctInputPackets = 0;
    	acctOutputPackets = 0;
    	callingStationIdSet = new DBHashSet<String>();
    	userNameSet = new DBHashSet<String>();
    	connInfoMap = new THashMap<String,Set<String>>();
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
	public long getAcctSessionTime() {
		return acctSessionTime;
	}
	public void setAcctSessionTime(long acctSessionTime) {
		this.acctSessionTime = acctSessionTime;
	}
	public long getAcctInputOctets() {
		return acctInputOctets;
	}
	public void setAcctInputOctets(long acctInputOctets) {
		this.acctInputOctets = acctInputOctets;
	}
	public long getAcctOutputOctets() {
		return acctOutputOctets;
	}
	public void setAcctOutputOctets(long acctOutputOctets) {
		this.acctOutputOctets = acctOutputOctets;
	}
	public long getAcctInputPackets() {
		return acctInputPackets;
	}
	public void setAcctInputPackets(long acctInputPackets) {
		this.acctInputPackets = acctInputPackets;
	}
	public long getAcctOutputPackets() {
		return acctOutputPackets;
	}
	public void setAcctOutputPackets(long acctOutputPackets) {
		this.acctOutputPackets = acctOutputPackets;
	}

}
