package com.zeroaccess;
public class AlertItem {
    public static final int RISK_HIGH=0,RISK_MEDIUM=1,RISK_LOW=2;
    public String title,detail,time,icon;
    public int riskLevel;
    public AlertItem(String title,String detail,String time,int risk,String icon){this.title=title;this.detail=detail;this.time=time;this.riskLevel=risk;this.icon=icon;}
}