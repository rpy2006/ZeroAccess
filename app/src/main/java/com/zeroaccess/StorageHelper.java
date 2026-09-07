package com.zeroaccess;
import android.content.Context;
import android.content.SharedPreferences;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
public class StorageHelper {
    private static final String P="zeroaccess_data",KA="alerts",KN="night",KW="work",KB="block",KS="score";
    private static SharedPreferences p(Context c){return c.getSharedPreferences(P,Context.MODE_PRIVATE);}
    public static void saveAlerts(Context c,List<AlertItem> a){try{JSONArray ar=new JSONArray();for(AlertItem x:a){JSONObject o=new JSONObject();o.put("t",x.title);o.put("d",x.detail);o.put("ti",x.time);o.put("r",x.riskLevel);o.put("i",x.icon);ar.put(o);}p(c).edit().putString(KA,ar.toString()).apply();}catch(Exception e){e.printStackTrace();}}
    public static List<AlertItem> loadAlerts(Context c){List<AlertItem> l=new ArrayList<>();try{String j=p(c).getString(KA,null);if(j==null)return l;JSONArray ar=new JSONArray(j);for(int i=0;i<ar.length();i++){JSONObject o=ar.getJSONObject(i);l.add(new AlertItem(o.getString("t"),o.getString("d"),o.getString("ti"),o.getInt("r"),o.getString("i")));}}catch(Exception e){e.printStackTrace();}return l;}
    public static void saveNightMode(Context c,boolean v){p(c).edit().putBoolean(KN,v).apply();}
    public static boolean getNightMode(Context c){return p(c).getBoolean(KN,false);}
    public static void saveWorkMode(Context c,boolean v){p(c).edit().putBoolean(KW,v).apply();}
    public static boolean getWorkMode(Context c){return p(c).getBoolean(KW,false);}
    public static void saveBlockActive(Context c,boolean v){p(c).edit().putBoolean(KB,v).apply();}
    public static boolean getBlockActive(Context c){return p(c).getBoolean(KB,false);}
    public static void savePrivacyScore(Context c,int v){p(c).edit().putInt(KS,v).apply();}
    public static int getPrivacyScore(Context c){return p(c).getInt(KS,-1);}
}