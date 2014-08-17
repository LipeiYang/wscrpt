package com.qualicom.wscrpt.vo;

public class ApInfo {
	
	public static ApInfo OTHERS = new ApInfo("others","");
	
	public ApInfo(String apMac, String apDesc) {
		super();
		this.apMac = apMac;
		this.apDesc = apDesc;
	}
	
	private String apMac;
	private String apDesc;
	
	public String getApMac() {
		return apMac;
	}
	public void setApMac(String apMac) {
		this.apMac = apMac;
	}
	public String getApDesc() {
		return apDesc;
	}
	public void setApDesc(String apDesc) {
		this.apDesc = apDesc;
	}
}
