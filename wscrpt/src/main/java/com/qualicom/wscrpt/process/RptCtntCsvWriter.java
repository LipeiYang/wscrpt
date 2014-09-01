package com.qualicom.wscrpt.process;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import com.qualicom.wscrpt.utils.DateUtil;
import com.qualicom.wscrpt.vo.RptContent;

public class RptCtntCsvWriter {
	
	File rptFile;
	BufferedWriter writer ;
	CSVPrinter printer;
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
	public boolean openFile(File csvFile) throws IOException{
		
		rptFile = csvFile;
		rptFile.createNewFile();		
		writer = new BufferedWriter(new FileWriter(rptFile));
		printer = new CSVPrinter(writer, CSVFormat.DEFAULT);
		return true;
	}
	private void writeCtnt(List<String> dataStr) throws IOException{
		printer.printRecord(dataStr);
	}
	public void  writeLine(RptContent rptCtnt, Date date, Map<String,Integer> dynCtnt, int intvl) throws IOException, ParseException{
		List<String> csvDataList = new ArrayList<String>();
		String dataStr;
		try {
			if(intvl==-1)
				dataStr = DateUtil.Dt2CSVDay(date); 
			 else
				dataStr = DateUtil.Dt2CSVMinRng(date, intvl);
				 				
			csvDataList.add(dataStr);
			csvDataList.addAll(genCtntPrefix()); 			
			csvDataList.add(String.valueOf(rptCtnt.getUserNameSet().size()));
			csvDataList.add(String.valueOf(rptCtnt.getCallingStationIdSet().size()));
			csvDataList.add(String.valueOf(rptCtnt.getAcctInputPackets()));
			csvDataList.add(String.valueOf(rptCtnt.getAcctOutputPackets()));
			csvDataList.add(String.valueOf(rptCtnt.getAcctInputOctets()));
			csvDataList.add(String.valueOf(rptCtnt.getAcctOutputOctets()));
			csvDataList.add(String.valueOf(rptCtnt.getAcctSessionTime()));
			for(String colName : dynColmOrder){
				csvDataList.add(String.valueOf(dynCtnt.get(colName)));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		writeCtnt(csvDataList);
	}
	public void writeHeader() throws IOException{
		List<String> csvDataList = buildBasicHeader();		
		csvDataList.add("No. of Client(Name)");
		csvDataList.add("No. of Client(Mac)");
		csvDataList.add("Packages In");
		csvDataList.add("Packages Out"); 
		csvDataList.add("Octets In");
		csvDataList.add("Octets Out");
		csvDataList.add("Session Time");
		for(String colName : dynColmOrder){
			csvDataList.add(colName);
		}
		writeCtnt(csvDataList);
	}
	public void writeConcurHeader() throws IOException{
		List<String> csvDataList = buildBasicHeader();		
		csvDataList.add("No. of Concurrent Session/User");
		writeCtnt(csvDataList);
	}
	private List<String> buildBasicHeader(){
		List<String> csvDataList = new ArrayList<String>();
		csvDataList.add("Time");
		if(loc!=null) 
			csvDataList.add("Location");
		if(ap_des!=null )
			csvDataList.add("AP Description"); 			
		if(ap_mac!=null)
			csvDataList.add("AP Mac"); 	
		if(ssid!=null) 
			csvDataList.add("SSID");
		return csvDataList;
	}
	private List<String> genCtntPrefix(){
		List<String> csvDataList = new ArrayList<String>();
		if(loc!=null)csvDataList.add(loc);
		if(ap_des!=null )
			csvDataList.add(ap_des); 			
		if(ap_mac!=null)
			csvDataList.add(ap_mac); 	
		if(ssid!=null)
			csvDataList.add(ssid); 
		return csvDataList;
	}
	private List<String> buildSumLinePrefix(){
		List<String> csvDataList = new ArrayList<String>();
		csvDataList.add("");
		if(loc!=null) 
			csvDataList.add("");
		if(ap_des!=null )
			csvDataList.add(""); 			
		if(ap_mac!=null)
			csvDataList.add(""); 	
		if(ssid!=null) 
			csvDataList.add("");
		return csvDataList;
	}
	public void  writeSumLine(RptContent rptCtnt,Map<String,Set<String>>dynCtnt) throws IOException{
		List<String> sumLine = buildSumLinePrefix();
		sumLine.add(String.valueOf(rptCtnt.getUserNameSet().size()));
		sumLine.add(String.valueOf(rptCtnt.getCallingStationIdSet().size()));
		sumLine.add(String.valueOf(rptCtnt.getAcctInputPackets()));
		sumLine.add(String.valueOf(rptCtnt.getAcctOutputPackets()));
		sumLine.add(String.valueOf(rptCtnt.getAcctInputOctets()));
		sumLine.add(String.valueOf(rptCtnt.getAcctOutputOctets()));
		sumLine.add(String.valueOf(rptCtnt.getAcctSessionTime()));
			for(String colName : dynColmOrder){
				sumLine.add(String.valueOf(dynCtnt.get(colName).size()));
			}
		writeCtnt(sumLine);
	}	
	public void  writreConcurLine(Date date, int count) throws IOException, ParseException{
		List<String> csvDataList = new ArrayList<String>();
		try {			 				
			csvDataList.add(DateUtil.Dt2CSVConcurTime(date));
			csvDataList.addAll(genCtntPrefix());
			csvDataList.add(String.valueOf(count));
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			throw e;
		} 
		writeCtnt(csvDataList);
	}
	public void  writreConcurSumLine(int count) throws IOException{
		List<String> csvDataLine = buildSumLinePrefix();
			csvDataLine.add(String.valueOf(count));			
		writeCtnt(csvDataLine);
	}
	public void flushFile() throws IOException{
			writer.flush();
			writer.close();
	}
}
