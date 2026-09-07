package com.zeroaccess;
import android.content.Context;
import android.os.Bundle;
import android.text.*;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class AppsFragment extends Fragment {
    private AppAdapter adapter;private AlertAdapter alertAdapter;
    private List<AppInfo> all=new ArrayList<>();private List<AlertItem> alerts=new ArrayList<>();
    @Nullable @Override public View onCreateView(@NonNull LayoutInflater i,@Nullable ViewGroup c,@Nullable Bundle s){return i.inflate(R.layout.fragment_apps_alerts,c,false);}
    @Override public void onViewCreated(@NonNull View v,@Nullable Bundle s){super.onViewCreated(v,s);
        LinearLayout tA=v.findViewById(R.id.tab_apps),tAl=v.findViewById(R.id.tab_alerts);
        View pA=v.findViewById(R.id.panel_apps),pAl=v.findViewById(R.id.panel_alerts);
        TextView ttA=v.findViewById(R.id.tv_tab_apps),ttAl=v.findViewById(R.id.tv_tab_alerts);
        View iA=v.findViewById(R.id.indicator_apps),iAl=v.findViewById(R.id.indicator_alerts);
        tA.setOnClickListener(x->{pA.setVisibility(View.VISIBLE);pAl.setVisibility(View.GONE);ttA.setTextColor(0xFF5B8DEF);ttAl.setTextColor(0xFF3D4060);iA.setVisibility(View.VISIBLE);iAl.setVisibility(View.INVISIBLE);});
        tAl.setOnClickListener(x->{pA.setVisibility(View.GONE);pAl.setVisibility(View.VISIBLE);ttA.setTextColor(0xFF3D4060);ttAl.setTextColor(0xFF5B8DEF);iA.setVisibility(View.INVISIBLE);iAl.setVisibility(View.VISIBLE);});
        RecyclerView rvA=v.findViewById(R.id.rv_apps);TextView tvC=v.findViewById(R.id.tv_app_count);EditText et=v.findViewById(R.id.et_search);
        adapter=new AppAdapter(app->{if(getActivity()!=null&&isAdded())try{startActivity(AppDetailActivity.intentFor(getActivity(),app));}catch(Exception e){e.printStackTrace();}});
        rvA.setLayoutManager(new LinearLayoutManager(getContext()));rvA.setAdapter(adapter);
        Context ctx=requireContext().getApplicationContext();
        new Thread(()->{try{List<AppInfo> ld=PermissionHelper.getInstalledApps(ctx);ld.sort(Comparator.comparing(a->a.appName));if(!isAdded()||getActivity()==null)return;getActivity().runOnUiThread(()->{if(!isAdded()||getView()==null)return;all=ld;adapter.setData(all);tvC.setText(all.size()+" apps");});}catch(Exception e){e.printStackTrace();}}).start();
        
        // FIXED: Renamed loop variable from 'a' to 'app'
        et.addTextChangedListener(new TextWatcher(){
            @Override public void beforeTextChanged(CharSequence s,int a,int b,int c){}
            @Override public void onTextChanged(CharSequence s,int a,int b,int c){
                if(!isAdded())return;
                String q=s.toString().toLowerCase().trim();
                List<AppInfo> f=new ArrayList<>();
                for(AppInfo app:all)
                    if(app.appName.toLowerCase().contains(q))f.add(app);
                adapter.setData(f);
                tvC.setText(f.size()+" apps");
            }
            @Override public void afterTextChanged(Editable s){}
        });

        RecyclerView rvAl=v.findViewById(R.id.rv_alerts);TextView tvN=v.findViewById(R.id.tv_no_alerts),tvCl=v.findViewById(R.id.tv_clear_alerts);
        alertAdapter=new AlertAdapter(alerts);rvAl.setLayoutManager(new LinearLayoutManager(getContext()));rvAl.setAdapter(alertAdapter);
        List<AlertItem> saved=StorageHelper.loadAlerts(ctx);
        if(saved!=null&&!saved.isEmpty()){alerts.addAll(saved);}else{String now=new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new java.util.Date());alerts.add(new AlertItem("Location Accessed","com.example.maps",now,AlertItem.RISK_HIGH,"loc"));alerts.add(new AlertItem("Microphone Accessed","com.example.social",now,AlertItem.RISK_MEDIUM,"mic"));alerts.add(new AlertItem("Camera Accessed","com.example.camera",now,AlertItem.RISK_LOW,"cam"));alerts.add(new AlertItem("Contacts Read","com.example.app",now,AlertItem.RISK_MEDIUM,"con"));alerts.add(new AlertItem("Storage Accessed","com.example.files",now,AlertItem.RISK_LOW,"sto"));StorageHelper.saveAlerts(ctx,alerts);}
        tvN.setVisibility(alerts.isEmpty()?View.VISIBLE:View.GONE);rvAl.setVisibility(alerts.isEmpty()?View.GONE:View.VISIBLE);alertAdapter.notifyDataSetChanged();
        tvCl.setOnClickListener(x->{if(!isAdded())return;alerts.clear();alertAdapter.notifyDataSetChanged();tvN.setVisibility(View.VISIBLE);rvAl.setVisibility(View.GONE);StorageHelper.saveAlerts(ctx,new ArrayList<>());});}
}