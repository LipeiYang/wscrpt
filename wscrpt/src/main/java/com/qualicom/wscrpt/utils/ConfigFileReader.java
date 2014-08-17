package com.qualicom.wscrpt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

public class ConfigFileReader {
	
	private Map<String,List<String>> cfgCtnt;
	private Map<String,List<String[]>> cfgArrCtnt;
	
	public ConfigFileReader(String inFilePath,Boolean orderRev) throws IOException {
		
		cfgCtnt = new HashMap<String,List<String>>(); 
		
		File f = new File(System.getProperty("cfgfile.path")+"/"+inFilePath);
//		FileReader fr;
//	
//		fr = new FileReader(f);
//		
//		BufferedReader buf = new BufferedReader(fr);
//		String line;
		Boolean skipHeader = true;
		
		
		 CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), CSVFormat.DEFAULT);
		 for (CSVRecord csvRecord : parser) {
			 if(skipHeader){
					skipHeader=false;
					continue;
				}
				if(orderRev){	
					this.addToMap(csvRecord.get(1),csvRecord.get(0));
				}
				else
					this.addToMap(csvRecord.get(0),csvRecord.get(1));
			 
		 }
			
//		fr.close();		
		
	}
public ConfigFileReader(String inFilePath) throws IOException {
		
	cfgArrCtnt = new HashMap<String,List<String[]>>();
		
		File f = new File(System.getProperty("cfgfile.path")+"/"+inFilePath);
//		FileReader fr;
//	
//		fr = new FileReader(f);
//		
//		BufferedReader buf = new BufferedReader(fr);
//		String line;
		Boolean skipHeader = true;
		
		
		 CSVParser parser = CSVParser.parse(f, Charset.defaultCharset(), CSVFormat.DEFAULT);
		 for (CSVRecord csvRecord : parser) {
			 if(skipHeader){
					skipHeader=false;
					continue;
				}
			 	
				this.addToStrArrMap(csvRecord.get(0),new String[]{csvRecord.get(1),csvRecord.get(2)});
				
			 
		 }
			
//		fr.close();		
		
	}
	private void addToMap(String key,String value){
		if(cfgCtnt.containsKey(key)){
			cfgCtnt.get(key).add(value);
		}
		else{
			List<String> tmpList = new ArrayList<String>();
			tmpList.add((String)value);
			cfgCtnt.put(key,tmpList);
		}
	}
	private void addToStrArrMap(String key,String[] value){
		if(cfgArrCtnt.containsKey(key)){
			cfgArrCtnt.get(key).add(value);
		}
		else{
			List<String[]> tmpList = new ArrayList<String[]>();
			tmpList.add(value);
			cfgArrCtnt.put(key,tmpList);
		}
	}
	public Map<String,List<String>> getConfigMap(){
		return cfgCtnt;
	}
	public Map<String,List<String[]>> getArrConfigMap(){
		return cfgArrCtnt;
	}
	public List<String> getConfigCtnt(String key){
		return cfgCtnt.get(key);
	}
	
}
