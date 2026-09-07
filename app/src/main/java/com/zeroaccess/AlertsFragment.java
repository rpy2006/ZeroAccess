package com.zeroaccess;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AlertsFragment extends Fragment {

    private final List<AlertItem> alerts = new ArrayList<>();
    private AlertAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_alerts, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        RecyclerView rv  = v.findViewById(R.id.rv_alerts);
        TextView tvNone  = v.findViewById(R.id.tv_no_alerts);
        TextView tvClear = v.findViewById(R.id.tv_clear_alerts);

        adapter = new AlertAdapter(alerts);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        rv.setAdapter(adapter);

        // Seed with demo alerts if empty
        if (alerts.isEmpty()) {
            String now = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());
            alerts.add(new AlertItem("Location Accessed",   "com.example.maps",   now, AlertItem.RISK_HIGH,   "\uD83D\uDCCD"));
            alerts.add(new AlertItem("Microphone Accessed", "com.example.social", now, AlertItem.RISK_MEDIUM, "\uD83C\uDFA4"));
            alerts.add(new AlertItem("Camera Accessed",     "com.example.camera", now, AlertItem.RISK_LOW,    "\uD83D\uDCF7"));
            alerts.add(new AlertItem("Contacts Read",       "com.example.app",    now, AlertItem.RISK_MEDIUM, "\uD83D\uDC65"));
            alerts.add(new AlertItem("Storage Accessed",    "com.example.files",  now, AlertItem.RISK_LOW,    "\uD83D\uDCC1"));
        }

        tvNone.setVisibility(alerts.isEmpty() ? View.VISIBLE : View.GONE);
        rv.setVisibility(alerts.isEmpty() ? View.GONE : View.VISIBLE);
        adapter.notifyDataSetChanged();

        tvClear.setOnClickListener(x -> {
            if (!isAdded()) return;
            alerts.clear();
            adapter.notifyDataSetChanged();
            tvNone.setVisibility(View.VISIBLE);
            rv.setVisibility(View.GONE);
        });
    }
}
