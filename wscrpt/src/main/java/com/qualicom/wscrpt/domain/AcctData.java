package com.qualicom.wscrpt.domain;
import org.springframework.roo.addon.javabean.RooJavaBean;
import org.springframework.roo.addon.jpa.activerecord.RooJpaActiveRecord;
import org.springframework.roo.addon.tostring.RooToString;
import java.util.Date;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import org.springframework.format.annotation.DateTimeFormat;

@RooJavaBean
@RooToString
@RooJpaActiveRecord
public class AcctData {

    /**
     */
    private String acctUniqueId;

    /**
     */
    private String acctSessionId;

    /**
     */
    private String nasIpAddress;

    /**
     */
    private String ruckusSsid;

    /**
     */
    private String callingStationId;

    /**
     */
    private String userName;

    /**
     */
    private String connectInfo;

    /**
     */
    private String sessionStatus;

    /**
     */
    private String calledStationId;

    /**
     */
    private Integer acctSessionTime;

    /**
     */
    private Long acctInputOctets;

    /**
     */
    private Long acctOutputOctets;

    /**
     */
    private Long acctInputPackets;

    /**
     */
    private Long acctOutputPackets;

    /**
     */
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(style = "M-")
    private Date tmStmp;
}
