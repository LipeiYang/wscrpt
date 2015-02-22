package com.qualicom.wscrpt.finder;

import gnu.trove.map.hash.THashMap;
import gnu.trove.set.hash.THashSet;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;



public class DBHashSet<T> implements Set{
	static int setID = 0;
	static Map<Integer,String> cacheDataMap = new THashMap<Integer,String>(30000,0.75f);
	static Map<Integer,Set<Integer>> cacheRefMap = new THashMap<Integer,Set<Integer>>(10000000,0.75f);
	public static void resetData(){
		cacheDataMap.clear();
		cacheRefMap.clear();
		cacheDataMap = new THashMap<Integer,String>(30000,0.75f);
		cacheRefMap = new THashMap<Integer,Set<Integer>>(10000000,0.75f);
		Runtime.getRuntime().gc();
	}
	public static Map<Integer,String> getCacheDataMap() {
		return cacheDataMap;
	}
	public static void setCacheDataMap(THashMap<Integer,String> cacheDataMap) {
		DBHashSet.cacheDataMap = cacheDataMap;
	}
	public static Map<Integer, Set<Integer>> getCacheRefMap() {
		return cacheRefMap;
	}
	public static void setCacheRefMap(Map<Integer, Set<Integer>> cacheRefMap) {
		DBHashSet.cacheRefMap = cacheRefMap;
	}
	public int getCurID() {
		return curID;
	}
	public void setCurID(int curID) {
		this.curID = curID;
	}
	private int  curID;
	
	public DBHashSet(){
		curID = setID;
		cacheRefMap.put(curID,new THashSet<Integer>());
		setID++;
	}
	@Override
	public int size() {
		Set refList = cacheRefMap.get(this.curID);
		
		return refList.size();
	}

	@Override
	public boolean isEmpty() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean contains(Object o) {
		
		return false;
	}

	@Override
	public Iterator iterator() {
		// TODO Auto-generated method stub
		Set  ctntList = new THashSet<String>();
		Set<Integer> refList = cacheRefMap.get(this.curID);
		for(int itm : refList){
			ctntList.add(cacheDataMap.get(itm));
		}
		return ctntList.iterator();
	}

	@Override
	public Object[] toArray() {
		Set  ctntList = new THashSet<String>();
		Set<Integer> refList = cacheRefMap.get(this.curID);
		for(int itm : refList){			
			ctntList.add(cacheDataMap.get(itm));
		}
		return ctntList.toArray();
	}

	@Override
	public Object[] toArray(Object[] a) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(Object e) {
		int objId = e.hashCode();
		if(cacheDataMap.get(objId)==null){	
			String obj  = (String)e;
			cacheDataMap.put(objId,obj);			
			Set s = cacheRefMap.get(curID);
			s.add(objId);
			return true;
		}
		else{
		    Set s = cacheRefMap.get(curID);
			s.add(objId);
			return true;
		 }
	}

	@Override
	public boolean remove(Object o) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean containsAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean addAll(Collection c) {
		// TODO Auto-generated method stub
		for(Object obj : c){
			this.add(obj);
		}
		return true;
	}

	@Override
	public boolean retainAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeAll(Collection c) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
	public static void main(String[] args){
		Set basicSet = new HashSet();
		Set basicSet1 = new HashSet();
		DBHashSet dSet1 = new DBHashSet();
		DBHashSet dSet2 = new DBHashSet();
		
		Date startDate = new Date();
		for(int i=0; i<1000;i++){
			basicSet.add("OK"+i);
			//System.out.println(i);
		}
		Date endDate = new Date(); 
		System.out.println((endDate.getTime() - startDate.getTime())/1000.0);
		System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
	
		startDate = new Date();
		for(int i=0; i<1000;i++){
			basicSet1.add("OK"+i);
			//System.out.println(i);
		}
		endDate = new Date(); 
		System.out.println((endDate.getTime() - startDate.getTime())/1000.0);
		System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
		
		basicSet = null;
		basicSet1 = null;
		System.gc();
		
		startDate = new Date();
		for(int i=0; i<1000;i++){
		dSet1.add("OK"+i);
		//System.out.println(i);
		}
		endDate = new Date();		
		System.out.println((endDate.getTime() - startDate.getTime())/1000.0);		
		System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
		System.gc();
		startDate = new Date();
		for(int i=0; i<1000;i++){
			dSet2.add("OK"+i);
			//System.out.println(i);
		}
		endDate = new Date();
		System.out.println((endDate.getTime() - startDate.getTime())/1000.0);
		System.out.println(Runtime.getRuntime().freeMemory()/(1024*1024));
		
		ArrayList list = new ArrayList(dSet2);
		//System.out.println(list.size());
		for(Object itm : list){
			System.out.println(itm);
		}
		
	}
	
	
}
