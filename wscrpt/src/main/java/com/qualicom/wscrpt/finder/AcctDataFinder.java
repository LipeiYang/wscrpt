package com.qualicom.wscrpt.finder;

import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import com.qualicom.wscrpt.domain.AcctData;

public class AcctDataFinder {
	
    @SuppressWarnings("unchecked")
	public static List<AcctData> findAcctData(String suffix, int firstResult, int maxResults) {
        EntityManager em = AcctData.entityManager();
        Query q = em.createNativeQuery("select * from acct_data_"+suffix,AcctData.class);
        return q.setFirstResult(firstResult).setMaxResults(maxResults).getResultList();
    }
    
    public static AcctData findLastOne(String suffix, String acctUniqueId)
    {
    	EntityManager em = AcctData.entityManager();
    	Query q = em.createNativeQuery("select * from acct_data_"+suffix+" where acct_unique_id = :acctUniqueId order by tm_stmp desc", AcctData.class);
    	q.setParameter("acctUniqueId", acctUniqueId);
    	return (AcctData) q.getResultList().get(0);
    }

}
