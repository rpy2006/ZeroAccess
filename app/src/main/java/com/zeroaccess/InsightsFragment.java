package com.zeroaccess;
import android.content.Context;
import android.os.Bundle;
import android.view.*;
import android.widget.TextView;
import androidx.annotation.NonNull;import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import java.util.List;
public class InsightsFragment extends Fragment {
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_insights,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){super.onViewCreated(v,s);
        TextView tL=v.findViewById(R.id.tv_loc_apps),tM=v.findViewById(R.id.tv_mic_apps),tC=v.findViewById(R.id.tv_cam_apps),tS=v.findViewById(R.id.tv_stor_apps),tCo=v.findViewById(R.id.tv_cont_apps);
        View bL=v.findViewById(R.id.bar_location),bM=v.findViewById(R.id.bar_mic),bC=v.findViewById(R.id.bar_camera),bS=v.findViewById(R.id.bar_storage),bCo=v.findViewById(R.id.bar_contacts);
        Context ctx=requireContext().getApplicationContext();
        new Thread(()->{try{List<AppInfo> apps=PermissionHelper.getInstalledApps(ctx);int tot=Math.max(1,apps.size()),loc=PermissionHelper.countAppsWithPerm(apps,"location"),mic=PermissionHelper.countAppsWithPerm(apps,"mic"),cam=PermissionHelper.countAppsWithPerm(apps,"camera"),stor=PermissionHelper.countAppsWithPerm(apps,"storage"),cont=PermissionHelper.countAppsWithPerm(apps,"contacts");
            if(!isAdded()||getActivity()==null)return;
            getActivity().runOnUiThread(()->{if(!isAdded()||getView()==null)return;tL.setText(String.valueOf(loc));tM.setText(String.valueOf(mic));tC.setText(String.valueOf(cam));tS.setText(String.valueOf(stor));tCo.setText(String.valueOf(cont));bar(bL,loc,tot);bar(bM,mic,tot);bar(bC,cam,tot);bar(bS,stor,tot);bar(bCo,cont,tot);});}catch(Exception e){e.printStackTrace();}}).start();}
    private void bar(View b,int n,int t){b.post(()->{if(!isAdded()||b.getParent()==null)return;View p=(View)b.getParent();int w=p.getWidth();if(w>0){ViewGroup.LayoutParams lp=b.getLayoutParams();lp.width=(int)((n*1.0/t)*w);b.setLayoutParams(lp);}});}
}