package com.zeroaccess;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.*;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;
import com.google.android.material.button.MaterialButton;
public class OnboardingActivity extends AppCompatActivity {
    private static final int[] ICONS={R.drawable.ic_shield,R.drawable.ic_block,R.drawable.ic_notification,R.drawable.ic_verified};
    private static final String[] TITLES={"Monitor All Permissions","Block With One Tap","Real-Time Alerts","Setup Permissions"};
    private static final String[] DESCS={"Get complete visibility into which apps access your camera, microphone, location, storage, and contacts.","Force-block all sensitive permissions instantly across every app with a single tap.","Receive instant notifications when any app accesses sensitive permissions in the background.","Grant Usage Access to enable full monitoring. Your data never leaves your device."};
    private ViewPager2 vp;private MaterialButton btn;private View[] dots;private int cur=0;
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_onboarding);
        vp=findViewById(R.id.vp_onboarding);btn=findViewById(R.id.btn_action);TextView skip=findViewById(R.id.tv_skip);
        dots=new View[]{findViewById(R.id.dot0),findViewById(R.id.dot1),findViewById(R.id.dot2),findViewById(R.id.dot3)};
        vp.setAdapter(new SlideAdapter());
        vp.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback(){@Override public void onPageSelected(int p){cur=p;updateDots();btn.setText(p==TITLES.length-1?"Get Started":"Next");}});
        btn.setOnClickListener(v->{if(cur<TITLES.length-1)vp.setCurrentItem(cur+1);else goMain();});
        skip.setOnClickListener(v->goMain());}
    private void goMain(){getSharedPreferences("zeroaccess",MODE_PRIVATE).edit().putBoolean("onboarded",true).apply();startActivity(new Intent(this,MainActivity.class));}
    private void updateDots(){for(int i=0;i<dots.length;i++)dots[i].setBackgroundColor(i==cur?0xFF5B8DEF:0xFF3D4060);}
    class SlideAdapter extends RecyclerView.Adapter<SlideAdapter.VH>{
        @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup p,int t){return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.slide_page,p,false));}
        @Override public void onBindViewHolder(@NonNull VH h,int pos){h.icon.setImageResource(ICONS[pos]);h.title.setText(TITLES[pos]);h.desc.setText(DESCS[pos]);}
        @Override public int getItemCount(){return TITLES.length;}
        class VH extends RecyclerView.ViewHolder{ImageView icon;TextView title,desc;VH(View v){super(v);icon=v.findViewById(R.id.iv_slide_icon);title=v.findViewById(R.id.tv_title);desc=v.findViewById(R.id.tv_desc);}}
    }
}