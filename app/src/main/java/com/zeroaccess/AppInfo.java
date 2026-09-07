package com.zeroaccess;
import android.graphics.drawable.Drawable;
public class AppInfo {
    public String appName, packageName;
    public Drawable icon;
    public boolean hasCamera, hasMic, hasLocation, hasStorage, hasContacts, isSystemApp;
    public int getSensitivePermCount() { int c=0; if(hasCamera)c++; if(hasMic)c++; if(hasLocation)c++; if(hasStorage)c++; if(hasContacts)c++; return c; }
    public String getPermSummary() { StringBuilder sb=new StringBuilder(); if(hasCamera)sb.append("Camera "); if(hasMic)sb.append("Mic "); if(hasLocation)sb.append("Location "); if(hasStorage)sb.append("Storage "); if(hasContacts)sb.append("Contacts "); return sb.length()>0?sb.toString().trim():"No sensitive permissions"; }
}