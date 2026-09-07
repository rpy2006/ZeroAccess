package com.zeroaccess;
import android.content.*;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import com.google.android.material.switchmaterial.SwitchMaterial;
public class SettingsFragment extends Fragment {
    private static final String IG="https://www.instagram.com/rohiit.md",EM="rohitprasadyadav06@gmail.com";
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_settings,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){super.onViewCreated(v,s);Context ctx=requireContext();
        TextView tvU=v.findViewById(R.id.tv_usage_status),tvA=v.findViewById(R.id.tv_accessibility_status),tvN=v.findViewById(R.id.tv_notification_status);refresh(tvU,tvA,tvN);
        v.findViewById(R.id.ll_usage_access).setOnClickListener(x->{if(!isAdded())return;try{startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));}catch(Exception e){e.printStackTrace();}});
        v.findViewById(R.id.ll_accessibility).setOnClickListener(x->{if(!isAdded())return;try{startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));}catch(Exception e){e.printStackTrace();}});
        v.findViewById(R.id.ll_notification).setOnClickListener(x->{if(!isAdded())return;try{Intent i2=new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS);i2.putExtra(Settings.EXTRA_APP_PACKAGE,ctx.getPackageName());startActivity(i2);}catch(Exception e){e.printStackTrace();}});
        View btnB=v.findViewById(R.id.btn_master_block);TextView tvBL=v.findViewById(R.id.tv_block_label),tvBS=v.findViewById(R.id.tv_block_status);ImageView ivBI=v.findViewById(R.id.iv_block_icon);
        updateBlock(tvBL,ivBI,tvBS,StorageHelper.getBlockActive(ctx));
        btnB.setOnClickListener(x->{if(!isAdded())return;boolean active=StorageHelper.getBlockActive(ctx);if(!active){new AlertDialog.Builder(requireContext()).setTitle("Block All Permissions").setMessage("ZeroAccess will open Android system settings so you can manually revoke Camera, Microphone, Location, Contacts, and Storage access for each app.").setPositiveButton("Proceed",(d,w)->{StorageHelper.saveBlockActive(ctx,true);updateBlock(tvBL,ivBI,tvBS,true);try{startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));}catch(Exception e){e.printStackTrace();}toast("Opening settings");}).setNegativeButton("Cancel",null).show();}else{StorageHelper.saveBlockActive(ctx,false);updateBlock(tvBL,ivBI,tvBS,false);toast("Block state cleared");}});
        v.findViewById(R.id.btn_block_location).setOnClickListener(x->openS("Location"));v.findViewById(R.id.btn_block_camera).setOnClickListener(x->openS("Camera"));v.findViewById(R.id.btn_block_mic).setOnClickListener(x->openS("Microphone"));v.findViewById(R.id.btn_block_contacts).setOnClickListener(x->openS("Contacts"));v.findViewById(R.id.btn_block_storage).setOnClickListener(x->openS("Storage"));
        SwitchMaterial swN=v.findViewById(R.id.sw_night_mode),swW=v.findViewById(R.id.sw_work_mode);swN.setChecked(StorageHelper.getNightMode(ctx));swW.setChecked(StorageHelper.getWorkMode(ctx));
        swN.setOnCheckedChangeListener((b,c2)->{StorageHelper.saveNightMode(ctx,c2);toast(c2?"Night block enabled":"Night block disabled");});swW.setOnCheckedChangeListener((b,c2)->{StorageHelper.saveWorkMode(ctx,c2);toast(c2?"Work mode enabled":"Work mode disabled");});
        v.findViewById(R.id.ll_instagram).setOnClickListener(x->{if(!isAdded())return;try{startActivity(new Intent(Intent.ACTION_VIEW,Uri.parse(IG)));}catch(Exception e){toast("Could not open Instagram");}});
        v.findViewById(R.id.ll_email).setOnClickListener(x->{if(!isAdded())return;try{Intent i2=new Intent(Intent.ACTION_SENDTO);i2.setData(Uri.parse("mailto:"+EM));i2.putExtra(Intent.EXTRA_SUBJECT,"ZeroAccess Feedback");startActivity(Intent.createChooser(i2,"Send Email"));}catch(Exception e){copy(EM);toast("Email copied");}});
        v.findViewById(R.id.ll_email).setOnLongClickListener(x->{if(!isAdded())return false;copy(EM);toast("Email copied!");return true;});
        v.findViewById(R.id.ll_about).setOnClickListener(x->{if(!isAdded())return;new AlertDialog.Builder(requireContext()).setTitle("ZeroAccess v1.0.0").setMessage("Privacy utility for Android.\nDeveloped by Yadav Enterprises.\n\nAll data stored locally. Nothing sent externally.").setPositiveButton("OK",null).show();});}
    private void updateBlock(TextView lbl,ImageView icon,TextView status,boolean active){if(lbl==null)return;if(active){if(icon!=null)icon.setImageResource(R.drawable.ic_unlock);lbl.setText("UNBLOCK ALL PERMISSIONS");lbl.setTextColor(0xFF22C55E);if(status!=null){status.setText("Block active");status.setTextColor(0xFF22C55E);}}else{if(icon!=null)icon.setImageResource(R.drawable.ic_lock);lbl.setText("BLOCK ALL PERMISSIONS");lbl.setTextColor(0xFFE05A5A);if(status!=null){status.setText("All permissions currently allowed");status.setTextColor(0xFF3D4060);}}}
    private void openS(String t){if(!isAdded())return;toast("Opening settings to manage "+t);try{startActivity(new Intent(Settings.ACTION_APPLICATION_SETTINGS));}catch(Exception e){e.printStackTrace();}}
    private void copy(String t){if(!isAdded()||getContext()==null)return;ClipboardManager cm=(ClipboardManager)requireContext().getSystemService(Context.CLIPBOARD_SERVICE);if(cm!=null)cm.setPrimaryClip(ClipData.newPlainText("text",t));}
    private void toast(String m){if(!isAdded()||getContext()==null)return;Toast.makeText(requireContext(),m,Toast.LENGTH_SHORT).show();}
    @Override public void onResume(){super.onResume();if(!isAdded()||getView()==null)return;View v=getView();refresh(v.findViewById(R.id.tv_usage_status),v.findViewById(R.id.tv_accessibility_status),v.findViewById(R.id.tv_notification_status));}
    private void refresh(TextView u,TextView a,TextView n){if(!isAdded()||getContext()==null)return;setS(u,PermissionHelper.hasUsageStatsPermission(requireContext()));setS(a,PermissionHelper.isAccessibilityServiceEnabled(requireContext()));setS(n,false);}
    private void setS(TextView tv,boolean on){if(tv==null)return;tv.setText(on?"Enabled":"Disabled");tv.setTextColor(on?0xFF22C55E:0xFFE05A5A);}
}