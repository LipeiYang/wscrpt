package com.qualicom.wscrpt.utils;

import com.qualicom.wscrpt.domain.AcctData;

public class AcctDataUtil 
{
	public static String getRealCalledStationId(AcctData acctData)
	{
		if (null == acctData || null == acctData.getCalledStationId())
		{
			return "";
		}
		else
		{
			return acctData.getCalledStationId().split(":")[0].intern();
			
		}
	}
}
