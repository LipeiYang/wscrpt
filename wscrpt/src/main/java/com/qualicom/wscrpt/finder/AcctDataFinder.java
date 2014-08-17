package com.qualicom.wscrpt.finder;

import java.util.Collections;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.apache.log4j.Logger;

import com.qualicom.wscrpt.domain.AcctData;
 
public class AcctDataFinder {
	
    @SuppressWarnings("unchecked")
	public static List<AcctData> findAcctData(String suffix, int firstResult, int maxResults) {
    	try
    	{
	        EntityManager em = AcctData.entityManager();
	        Query q = em.createNativeQuery("select * from acct_data_"+suffix,AcctData.class);
	        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    	}catch(Exception e)
    	{
    		Logger log = Logger.getLogger(AcctDataFinder.class);
    		log.error("findAcctData error suffix:"+suffix+" firstResult:"+firstResult+" maxResults:"+maxResults);
    		if(log.isDebugEnabled())
    		{
    			e.printStackTrace();
    		}
    		return Collections.EMPTY_LIST;
    	}
    }
    
    public static AcctData findLastOne(String suffix, String acctUniqueId)
    {
    	try {
    		EntityManager em = AcctData.entityManager();
    		Query q = em.createNativeQuery("select * from acct_data_"+suffix+" where acct_unique_id = :acctUniqueId order by tm_stmp desc", AcctData.class);
    		q.setParameter("acctUniqueId", acctUniqueId);
    		return (AcctData) q.getResultList().get(0);
    	}
    	catch(Exception e)
    	{
    		Logger log = Logger.getLogger(AcctDataFinder.class);
    		log.error("findLastOne error suffix:"+suffix+" acctUniqueId:"+acctUniqueId);
    		if(log.isDebugEnabled())
    		{
    			e.printStackTrace();
    		}
    		return null;
    	}
    }

}
