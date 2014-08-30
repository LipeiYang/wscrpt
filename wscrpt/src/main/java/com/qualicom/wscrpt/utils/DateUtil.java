package com.qualicom.wscrpt.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.apache.commons.lang3.time.DateUtils;

public class DateUtil {
	
	private final static SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat sdfMinute = new SimpleDateFormat("HH:mm");
	private final static SimpleDateFormat sdfConCur = new SimpleDateFormat("HHmm");
	private final static String csvMinSep = "-";
	private final static SimpleDateFormat sdfDaily = new SimpleDateFormat("yyyyMMdd");
	private final static SimpleDateFormat sdfRptKey = new SimpleDateFormat("yyyy/MM/dd_HH:mm:ss");
	
	public static Date str2Dt(String s) throws ParseException
	{
		return sdf.parse(s);
	}
	public static Date strKey2Dt(String s) throws ParseException
	{
		return sdfRptKey.parse(s);
	}
	public static String Dt2CSVMinRng(Date date,int intvl) throws ParseException
	{
		String t_begin = sdfMinute.format(date);
		String t_end = null;
		Date d_end = DateUtils.addMinutes(date, intvl);
		if(d_end.compareTo(DateUtil.getDayEnd(date)) < 0 )
			t_end = sdfMinute.format(d_end);
		else
			t_end =  sdfMinute.format(DateUtils.addSeconds(DateUtil.getDayEnd(date),1));
		return t_begin + csvMinSep + t_end;
	}
	public static String Dt2CSVDay(Date date) throws ParseException
	{
		return sdfDaily.format(date);
	}
	public static String Dt2CSVConcurTime(Date date) throws ParseException
	{
		if(date==null)
			return "";
		else
			return sdfConCur.format(date);
	}
	public static Date truncDateByInterval(Date d, int interval){
		Date beginOfToday = beginOfToday(d);
		long tDiff = d.getTime()-beginOfToday.getTime();
		int trunMinDiff = (int)((tDiff/1000/60)/(interval));
		return DateUtils.addMinutes(beginOfToday, trunMinDiff*interval);
	}
	public static Date beginOfToday(Date d){
		return DateUtils.truncate(d, Calendar.DATE);
	}
	
	public static String DtToKeyStr(Date d) {
		return sdfRptKey.format(d);
	}
	
	public static String DtToStr(Date d) {
		return sdf.format(d);
	}
	
	public static String[] getDays4Mth(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return getDays(c,c.get(Calendar.DAY_OF_MONTH));
	}
	
	public static String[] getDays4Wek(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		return getDays(c,7);
	}
	
	public static Set<RptTyp> getRptTyps(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		Set<RptTyp> s = new HashSet<RptTyp>();
		s.add(RptTyp.DAY);
		if(c.get(Calendar.DAY_OF_WEEK)==Calendar.SUNDAY)
		{
			s.add(RptTyp.WEEK);
		}
		if(c.getActualMaximum(Calendar.DAY_OF_MONTH)==c.get(Calendar.DAY_OF_MONTH))
		{
			s.add(RptTyp.MONTH);
		}
		return s;
	}
	public static Date getDaysBeforeToday(int d){
		if( d < 0 ){
			return beginOfToday(new Date());
		}
		return DateUtils.addDays(beginOfToday(new Date()),-d);
	}
	
	
	private static String[] getDays(Calendar c, int days)
	{
		String[] ls = new String[days];
		for(int i=0;i<days;i++)
		{
			if(i!=0) c.add(Calendar.DAY_OF_YEAR, -1);
			ls[i]=sdf.format(c.getTime());
		}
		return ls;
	}
	
	public static Date getDayEnd(Date d)
	{
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		c.set(Calendar.HOUR_OF_DAY, 23);
	    c.set(Calendar.MINUTE, 59);
	    c.set(Calendar.SECOND, 59);
	    c.set(Calendar.MILLISECOND, 000);
	    return c.getTime();
	}
	
	public static void main(String[] args) throws ParseException
	{
		String[] s1 = getDays4Mth(DateUtil.str2Dt("20140731"));
		String[] s2 = getDays4Wek(DateUtil.str2Dt("20140803"));
		Set<RptTyp> s3 = getRptTyps(DateUtil.str2Dt("20140731"));
		Set<RptTyp> s4 = getRptTyps(DateUtil.str2Dt("20140803"));
		Set<RptTyp> s5 = getRptTyps(DateUtil.str2Dt("20140804"));
		System.out.println(DateUtil.getDayEnd(new Date()));
	}
}
