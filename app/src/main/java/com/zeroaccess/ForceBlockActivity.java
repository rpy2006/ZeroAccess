package com.zeroaccess;
import android.content.Intent;import android.os.Bundle;import android.provider.Settings;import android.view.View;import android.widget.*;
import androidx.appcompat.app.AlertDialog;import androidx.appcompat.app.AppCompatActivity;
import java.util.List;
public class ForceBlockActivity extends AppCompatActivity {
    private int mode=0;
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_force_block);
        findViewById(R.id.iv_back).setOnClickListener(v->finish());
        RadioButton rbS=findViewById(R.id.rb_strict),rbB=findViewById(R.id.rb_balanced),rbC=findViewById(R.id.rb_custom);
        View llS=findViewById(R.id.ll_mode_strict),llB=findViewById(R.id.ll_mode_balanced),llC=findViewById(R.id.ll_mode_custom);
        llS.setOnClickListener(v->{mode=0;rbS.setChecked(true);rbB.setChecked(false);rbC.setChecked(false);});llB.setOnClickListener(v->{mode=1;rbS.setChecked(false);rbB.setChecked(true);rbC.setChecked(false);});llC.setOnClickListener(v->{mode=2;rbS.setChecked(false);rbB.setChecked(false);rbC.setChecked(true);});
        View llP=findViewById(R.id.ll_progress);TextView tvP=findViewById(R.id.tv_progress_label);View btnEx=findViewById(R.id.btn_execute_block);
        btnEx.setOnClickListener(v->new AlertDialog.Builder(this).setTitle("Confirm").setMessage("This opens Android permission settings for each risky app. You must revoke permissions manually.").setPositiveButton("Proceed",(d,w)->block(llP,tvP,btnEx)).setNegativeButton("Cancel",null).show());}
    private void block(View prog,TextView label,View btn){prog.setVisibility(View.VISIBLE);btn.setEnabled(false);android.content.Context appCtx=getApplicationContext();int m=mode;
        new Thread(()->{try{List<AppInfo> apps=PermissionHelper.getInstalledApps(appCtx);int n=0;for(AppInfo a:apps){if(m==1&&a.isSystemApp)continue;if(a.getSensitivePermCount()==0)continue;final int p=++n;final String nm=a.appName;if(isFinishing()||isDestroyed())return;runOnUiThread(()->{if(!isFinishing())label.setText("Processing "+nm+" ("+p+")");});try{Thread.sleep(150);}catch(InterruptedException ig){}}final int tot=n;if(isFinishing()||isDestroyed())return;runOnUiThread(()->{if(isFinishing()||isDestroyed())return;prog.setVisibility(View.GONE);btn.setEnabled(true);new AlertDialog.Builder(this).setTitle("Done!").setMessage("Found "+tot+" apps. Opening system app manager.").setPositiveButton("Open",(d,w)->{try{startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));}catch(Exception e){e.printStackTrace();}}).setNegativeButton("Close",null).show();});}catch(Exception e){e.printStackTrace();runOnUiThread(()->{if(!isFinishing()&&!isDestroyed()){prog.setVisibility(View.GONE);btn.setEnabled(true);}});}}).start();}
}