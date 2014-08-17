package com.qualicom.wscrpt.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ConfigFileReader {
	
	private Map<String,List<String>> cfgCtnt;
	
	public ConfigFileReader(String inFilePath,Boolean orderRev) throws IOException {
		
		cfgCtnt = new HashMap<String,List<String>>(); 
		
		File f = new File(System.getProperty("cfgfile.path")+"/"+inFilePath);
		FileReader fr;
	
		fr = new FileReader(f);
		
		BufferedReader buf = new BufferedReader(fr);
		String line;
		Boolean skipHeader = true;
		while((line=buf.readLine())!=null){
			if(skipHeader){
				skipHeader=false;
				continue;
			}
			if(line.length()==0){
				continue;
			}
			
			String[] ctntArry = line.split(",",2);
			if(orderRev){	
				this.addToMap( ctntArry[1].replace("\"", ""),ctntArry[0].replace("\"", ""));
			}
			else
				this.addToMap( ctntArry[0].replace("\"", ""),ctntArry[1].replace("\"", ""));
		}		
		fr.close();		
		
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
	public Map<String,List<String>> getConfigMap(){
		return cfgCtnt;
	}
	public List<String> getConfigCtnt(String key){
		return cfgCtnt.get(key);
	}
	
}
