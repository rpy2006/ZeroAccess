package com.zeroaccess;
import android.view.*;import android.widget.*;
import androidx.annotation.NonNull;import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
public class AlertAdapter extends RecyclerView.Adapter<AlertAdapter.VH> {
    private final List<AlertItem> data;
    public AlertAdapter(List<AlertItem> d){data=d;}
    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_alert,p,false));}
    @Override public void onBindViewHolder(@NonNull VH h,int pos){AlertItem it=data.get(pos);
        int iconRes,iconColor;String low=it.title.toLowerCase();
        if(low.contains("location")){iconRes=R.drawable.ic_location;iconColor=0xFFE8A440;}
        else if(low.contains("mic")||low.contains("audio")){iconRes=R.drawable.ic_microphone;iconColor=0xFF8B5CF6;}
        else if(low.contains("camera")){iconRes=R.drawable.ic_camera;iconColor=0xFF5B8DEF;}
        else if(low.contains("contact")){iconRes=R.drawable.ic_contacts;iconColor=0xFF22C55E;}
        else if(low.contains("storage")){iconRes=R.drawable.ic_storage;iconColor=0xFF22C4D0;}
        else{iconRes=R.drawable.ic_warning;iconColor=0xFFE8A440;}
        h.icon.setImageResource(iconRes);h.icon.setColorFilter(iconColor);
        h.title.setText(it.title);h.detail.setText(it.detail);h.time.setText(it.time);
        int bar;String label;switch(it.riskLevel){case AlertItem.RISK_HIGH:bar=0xFFE05A5A;label="HIGH";h.risk.setTextColor(0xFFE05A5A);break;case AlertItem.RISK_MEDIUM:bar=0xFFE8A440;label="MED";h.risk.setTextColor(0xFFE8A440);break;default:bar=0xFF22C55E;label="LOW";h.risk.setTextColor(0xFF22C55E);break;}
        h.bar.setBackgroundColor(bar);h.risk.setText(label);}
    @Override public int getItemCount(){return data.size();}
    static class VH extends RecyclerView.ViewHolder{ImageView icon;TextView title,detail,time,risk;View bar;VH(View v){super(v);icon=v.findViewById(R.id.iv_alert_icon);title=v.findViewById(R.id.tv_alert_title);detail=v.findViewById(R.id.tv_alert_detail);time=v.findViewById(R.id.tv_alert_time);risk=v.findViewById(R.id.tv_risk_level);bar=v.findViewById(R.id.v_risk_bar);}}
}