package com.zeroaccess;
import android.app.*;
import android.content.Intent;
import android.os.IBinder;
import androidx.core.app.NotificationCompat;
public class PermissionMonitorService extends Service {
    private static final String CH="zeroaccess";
    @Override public int onStartCommand(Intent i,int f,int id){
        NotificationChannel ch=new NotificationChannel(CH,"ZeroAccess",NotificationManager.IMPORTANCE_LOW);
        getSystemService(NotificationManager.class).createNotificationChannel(ch);
        startForeground(1,new NotificationCompat.Builder(this,CH).setContentTitle("ZeroAccess").setContentText("Monitoring active").setSmallIcon(android.R.drawable.ic_lock_lock).setPriority(NotificationCompat.PRIORITY_LOW).build());
        return START_STICKY;}
    @Override public IBinder onBind(Intent i){return null;}
}