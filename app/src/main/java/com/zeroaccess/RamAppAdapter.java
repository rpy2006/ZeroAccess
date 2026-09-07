// PATH: app/src/main/java/com/zeroaccess/RamAppAdapter.java
package com.zeroaccess;

import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

public class RamAppAdapter extends RecyclerView.Adapter<RamAppAdapter.VH> {

    public interface OnKill { void kill(RamAppItem item); }

    private List<RamAppItem> data;
    private long totalRamKb;
    private final OnKill cb;

    public RamAppAdapter(List<RamAppItem> data, long totalRamKb, OnKill cb) {
        this.data = data;
        this.totalRamKb = totalRamKb;
        this.cb = cb;
    }

    public void update(List<RamAppItem> d, long total) {
        this.data = d;
        this.totalRamKb = total;
        notifyDataSetChanged();
    }

    @NonNull @Override
    public VH onCreateViewHolder(@NonNull ViewGroup p, int t) {
        return new VH(LayoutInflater.from(p.getContext()).inflate(R.layout.item_ram_app, p, false));
    }

    @Override
    public void onBindViewHolder(@NonNull VH h, int pos) {
        RamAppItem it = data.get(pos);

        if (it.icon != null) h.icon.setImageDrawable(it.icon);
        h.name.setText(it.appName);
        h.pss.setText(it.getPssFormatted());
        h.priv.setText("Private: " + it.getPrivateFormatted());
        h.state.setText(it.processState);

        // State colour
        switch (it.processState) {
            case "Foreground": h.state.setTextColor(0xFF22C55E); break;
            case "Service":    h.state.setTextColor(0xFFE8A440); break;
            default:           h.state.setTextColor(0xFF5B8DEF); break;
        }

        // FIX: copy to final so lambda can capture it
        int rawPct = totalRamKb > 0 ? (int) ((it.pssKb * 100L) / totalRamKb) : 0;
        final int pct = Math.max(1, Math.min(100, rawPct));

        // RAM bar fill
        h.barFill.post(() -> {
            View parent = (View) h.barFill.getParent();
            if (parent != null) {
                ViewGroup.LayoutParams lp = h.barFill.getLayoutParams();
                lp.width = (int) (parent.getWidth() * pct / 100f);
                h.barFill.setLayoutParams(lp);
            }
        });

        // Bar colour: red if >5%, amber if >2%, blue otherwise
        int barColor = pct >= 5 ? 0xFFE05A5A : pct >= 2 ? 0xFFE8A440 : 0xFF5B8DEF;
        h.barFill.setBackgroundColor(barColor);
        h.pss.setTextColor(barColor);

        h.pct.setText(pct + "%");

        // Kill button — hide for foreground apps
        if ("Foreground".equals(it.processState)) {
            h.btnKill.setVisibility(View.GONE);
        } else {
            h.btnKill.setVisibility(View.VISIBLE);
            h.btnKill.setOnClickListener(v -> cb.kill(it));
        }
    }

    @Override public int getItemCount() { return data.size(); }

    static class VH extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView name, pss, priv, state, pct;
        View barFill;
        ImageView btnKill;

        VH(View v) {
            super(v);
            icon    = v.findViewById(R.id.iv_ram_icon);
            name    = v.findViewById(R.id.tv_ram_name);
            pss     = v.findViewById(R.id.tv_ram_pss);
            priv    = v.findViewById(R.id.tv_ram_private);
            state   = v.findViewById(R.id.tv_ram_state);
            pct     = v.findViewById(R.id.tv_ram_pct);
            barFill = v.findViewById(R.id.v_ram_bar_fill);
            btnKill = v.findViewById(R.id.iv_kill_btn);
        }
    }
}
