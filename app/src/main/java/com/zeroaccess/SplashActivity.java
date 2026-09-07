package com.zeroaccess;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import androidx.appcompat.app.AppCompatActivity;
public class SplashActivity extends AppCompatActivity {
    @Override protected void onCreate(Bundle s){super.onCreate(s);setContentView(R.layout.activity_splash);
        View logo=findViewById(R.id.iv_logo),name=findViewById(R.id.tv_name),tag=findViewById(R.id.tv_tagline),ver=findViewById(R.id.tv_version);
        fadeIn(logo,0);fadeIn(name,300);fadeIn(tag,600);fadeIn(ver,900);
        logo.postDelayed(()->{SharedPreferences p=getSharedPreferences("zeroaccess",MODE_PRIVATE);startActivity(new Intent(this,p.getBoolean("onboarded",false)?MainActivity.class:OnboardingActivity.class));},1800);}
    private void fadeIn(View v,long d){v.postDelayed(()->{AlphaAnimation a=new AlphaAnimation(0f,1f);a.setDuration(500);a.setFillAfter(true);v.startAnimation(a);v.setAlpha(1f);},d);}
}