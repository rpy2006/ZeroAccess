package com.zeroaccess;
import android.view.*;import android.widget.*;
import androidx.annotation.NonNull;import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
public class AppAdapter extends RecyclerView.Adapter<AppAdapter.VH> {
    public interface OnClick{void onClick(AppInfo a);}
    private List<AppInfo> data=new ArrayList<>();private final OnClick cb;
    public AppAdapter(OnClick cb){this.cb=cb;}
    public void setData(List<AppInfo> d){data=new ArrayList<>(d);notifyDataSetChanged();}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_app,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){AppInfo a=data.get(pos);h.icon.setImageDrawable(a.icon);h.name.setText(a.appName);h.perms.setText(a.getPermSummary());int c=a.getSensitivePermCount();if(c==0){h.status.setText("Safe");h.status.setTextColor(0xFF22C55E);}else if(c<=2){h.status.setText(c+" perms");h.status.setTextColor(0xFFE8A440);}else{h.status.setText(c+" perms");h.status.setTextColor(0xFFE05A5A);}h.itemView.setOnClickListener(v->cb.onClick(a));}
    @Override public int getItemCount(){return data.size();}
    static class VH extends RecyclerView.ViewHolder{ImageView icon;TextView name,perms,status;VH(View v){super(v);icon=v.findViewById(R.id.iv_app_icon);name=v.findViewById(R.id.tv_app_name);perms=v.findViewById(R.id.tv_app_perms);status=v.findViewById(R.id.tv_status);}}
}