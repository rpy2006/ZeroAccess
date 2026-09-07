// PATH: app/src/main/java/com/zeroaccess/RamAppItem.java
package com.zeroaccess;

import android.graphics.drawable.Drawable;

public class RamAppItem {
    public String appName;
    public String packageName;
    public Drawable icon;
    public int pid;
    public long pssKb;       // Proportional Set Size – actual RAM used
    public long privateKb;   // Private dirty pages – exclusively owned RAM
    public String processState; // "Background", "Service", "Cached", "Foreground"
    public int importance;   // ActivityManager.RunningAppProcessInfo.importance

    public String getPssFormatted() {
        if (pssKb >= 1024) return String.format("%.1f MB", pssKb / 1024f);
        return pssKb + " KB";
    }

    public String getPrivateFormatted() {
        if (privateKb >= 1024) return String.format("%.1f MB", privateKb / 1024f);
        return privateKb + " KB";
    }
}