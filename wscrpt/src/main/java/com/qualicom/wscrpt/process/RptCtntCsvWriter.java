package com.qualicom.wscrpt.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

import com.qualicom.wscrpt.utils.DateUtil;
import com.qualicom.wscrpt.vo.RptContent;

public class RptCtntCsvWriter {
	
	File rptFile;
	BufferedWriter writer ;
	
	String loc;
	String ap_des;
	String ap_mac;
	String ssid;
	List<String> dynColmOrder;
	
	public void setLoc(String loc) {
		this.loc = loc;
	}
	public void setAp_des(String ap_des) {
		this.ap_des = ap_des;
	}
	public void setAp_mac(String ap_mac) {
		this.ap_mac = ap_mac;
	}
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}
	public void setDynColmOrder(List<String> dynColmOrder) {
		this.dynColmOrder = dynColmOrder;
	}
	public static RptCtntCsvWriter instance;
	public static RptCtntCsvWriter getInstance(){
		if(instance==null){
			instance = new RptCtntCsvWriter();
		}
		return instance;
	}
	private RptCtntCsvWriter(){
		
	}
	public boolean openFile(File csvFile){
		
		rptFile = csvFile;
				
		try {
			rptFile.createNewFile();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			System.out.println("Create files failed");
			e1.printStackTrace();
			return false;
		}
		try {
			writer = new BufferedWriter(new FileWriter(rptFile));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Create Writer failed");
			e.printStackTrace();
			return false;
		}
		return true;
	}
	private boolean writeCtnt(String dataStr) throws IOException{
		writer.write(dataStr);
		writer.newLine();
		return false;
	}
	public void  writeLine(RptContent rptCtnt, Date date, Map<String,Integer> dynCtnt, int intvl) throws IOException{
		String dataStr = null;
		try {
			if(intvl==-1)
				dataStr = DateUtil.Dt2CSVDay(date); 
			 else
				dataStr = DateUtil.Dt2CSVMinRng(date, intvl);
			 				
			dataStr += "," + 
			(loc==null ? "": loc+"," ) + 
			(ap_des==null ? "": ap_des+"," ) + 
			(ap_mac==null ? "": ap_mac+"," ) + 	
			(ssid==null ? "": ssid+"," ) + 			
			String.valueOf(rptCtnt.getUserNameSet().size()) + "," +
			String.valueOf(rptCtnt.getCallingStationIdSet().size()) + "," +
			String.valueOf(rptCtnt.getAcctInputPackets()) + "," +
			String.valueOf(rptCtnt.getAcctOutputPackets()) + "," +
			String.valueOf(rptCtnt.getAcctInputOctets()) + "," +
			String.valueOf(rptCtnt.getAcctOutputOctets()) + "," +
			String.valueOf(rptCtnt.getAcctSessionTime());
			for(String colName : dynColmOrder){
				dataStr += "," + String.valueOf(dynCtnt.get(colName));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		writeCtnt(dataStr);
	}
	public void writeHeader() throws IOException{
		String headerStr = "";				
		headerStr += "Time," + 
			(loc==null ? "": "Location," ) + 
			(ap_des==null ? "": "AP Description," ) + 
			(ap_mac==null ? "": "AP Mac," ) + 	
			(ssid==null ? "": "SSID," ) + 			
			"No. of Client(Name)," +
			"No. of Client(Mac)," +
			"Packages In," +
			"Packages Out," +
			"Octets In," +
			"Octets Out," +
			"Session Time";
		for(String colName : dynColmOrder){
			headerStr += "," + colName;
		}
		writeCtnt(headerStr);
	}
	public void writeConcurHeader() throws IOException{
		String headerStr = "";				
		headerStr += "Time," + 
			(loc==null ? "": "Location," ) + 
			(ap_des==null ? "": "AP Description," ) + 
			(ap_mac==null ? "": "AP Mac," ) + 	
			(ssid==null ? "": "SSID," ) + 			
			"No. of Concurrent Session/User";
		writeCtnt(headerStr);
	}
	public void  writeSumLine(RptContent rptCtnt,Map<String,Integer>dynCtnt) throws IOException{
		String dataStr = "";				
			dataStr += "," + 
			(loc==null ? "": "," ) + 
			(ap_des==null ? "": "," ) + 
			(ap_mac==null ? "": "," ) + 	
			(ssid==null ? "": "," ) + 			
			String.valueOf(rptCtnt.getUserNameSet().size()) + "," +
			String.valueOf(rptCtnt.getCallingStationIdSet().size()) + "," +
			String.valueOf(rptCtnt.getAcctInputPackets()) + "," +
			String.valueOf(rptCtnt.getAcctOutputPackets()) + "," +
			String.valueOf(rptCtnt.getAcctInputOctets()) + "," +
			String.valueOf(rptCtnt.getAcctOutputOctets()) + "," +
			String.valueOf(rptCtnt.getAcctSessionTime());
			for(String colName : dynColmOrder){
				dataStr += "," + String.valueOf(dynCtnt.get(colName));
			}
		writeCtnt(dataStr);
	}	
	public void  writreConcurLine(Date date, int count) throws IOException{
		String dataStr = null;
		try {
			dataStr = DateUtil.Dt2CSVConcurTime(date) + "," + 
			(loc==null ? "": loc+"," ) + 
			(ap_des==null ? "": ap_des+"," ) + 
			(ap_mac==null ? "": ap_mac+"," ) + 	
			(ssid==null ? "": ssid+"," ) + 
			String.valueOf(count)+ ",";
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		writeCtnt(dataStr);
	}
	public void  writreConcurSumLine(int count) throws IOException{
		String dataStr = null;
			dataStr = "," + 
			(loc==null ? "": "," ) + 
			(ap_des==null ? "": "," ) + 
			(ap_mac==null ? "": "," ) + 	
			(ssid==null ? "": "," ) + 
			String.valueOf(count)+ ",";
			
		writeCtnt(dataStr);
	}
	public void flushFile(){
		try {
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
