package com.zeroaccess;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;
public class HomeFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_home,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){super.onViewCreated(v,s);
        TextView tvScore=v.findViewById(R.id.tv_score_number),tvLabel=v.findViewById(R.id.tv_score_label),tvMsg=v.findViewById(R.id.tv_score_msg),tvTotal=v.findViewById(R.id.tv_total_apps),tvRisky=v.findViewById(R.id.tv_risky_apps),tvLoc=v.findViewById(R.id.tv_loc_count),tvMic=v.findViewById(R.id.tv_mic_count),tvCam=v.findViewById(R.id.tv_cam_count),tvInsLoc=v.findViewById(R.id.tv_insight_loc),tvInsMic=v.findViewById(R.id.tv_insight_mic);
        View btn=v.findViewById(R.id.btn_force_block);
        btn.setOnClickListener(x->{if(getActivity()!=null&&isAdded())startActivity(new Intent(getActivity(),ForceBlockActivity.class));});
        Context ctx=requireContext().getApplicationContext();
        new Thread(()->{try{List<AppInfo> apps=PermissionHelper.getInstalledApps(ctx);int score=PermissionHelper.calcPrivacyScore(apps),loc=PermissionHelper.countAppsWithPerm(apps,"location"),mic=PermissionHelper.countAppsWithPerm(apps,"mic"),cam=PermissionHelper.countAppsWithPerm(apps,"camera"),risky=0;
            for(AppInfo a:apps)if(!a.isSystemApp&&a.getSensitivePermCount()>=2)risky++;
            final int fS=score,fL=loc,fM=mic,fC=cam,fR=risky,fT=apps.size();
            StorageHelper.savePrivacyScore(ctx,score);
            if(!isAdded()||getActivity()==null)return;
            requireActivity().runOnUiThread(()->{if(!isAdded()||getView()==null)return;
                tvTotal.setText(String.valueOf(fT));tvRisky.setText(String.valueOf(fR));tvLoc.setText(String.valueOf(fL));tvMic.setText(String.valueOf(fM));tvCam.setText(String.valueOf(fC));tvScore.setText(String.valueOf(fS));tvInsLoc.setText(fL+" apps have location access");tvInsMic.setText(fM+" apps have microphone access");
                int color;String label,msg;if(fS>=70){color=0xFF22C55E;label="Good";msg="Your privacy is well protected";}else if(fS>=40){color=0xFFE8A440;label="Fair";msg="Some risks detected";}else{color=0xFFE05A5A;label="Poor";msg="Privacy at risk - take action now";}
                tvScore.setTextColor(color);tvLabel.setTextColor(color);tvLabel.setText(label);tvMsg.setText(msg);});}catch(Exception e){e.printStackTrace();}}).start();}
}