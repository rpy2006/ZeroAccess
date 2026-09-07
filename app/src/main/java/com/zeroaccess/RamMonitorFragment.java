// PATH: app/src/main/java/com/zeroaccess/RamMonitorFragment.java
package com.zeroaccess;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.*;
import android.provider.Settings;
import android.view.*;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.*;

import java.util.*;

public class RamMonitorFragment extends Fragment {

    private RamAppAdapter adapter;
    private List<RamAppItem> items = new ArrayList<>();
    private TextView tvTotalUsed, tvTotalFree, tvTotalBar, tvUsedPct;
    private View totalBarFill;
    private View btnKillAll;
    private ProgressBar scanSpinner;
    private TextView tvScanHint;
    private boolean scanning = false;

    @Nullable @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ram_monitor, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View v, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(v, savedInstanceState);

        tvTotalUsed  = v.findViewById(R.id.tv_ram_used);
        tvTotalFree  = v.findViewById(R.id.tv_ram_free);
        tvUsedPct    = v.findViewById(R.id.tv_ram_pct_total);
        tvScanHint   = v.findViewById(R.id.tv_scan_hint);
        totalBarFill = v.findViewById(R.id.v_total_bar_fill);
        btnKillAll   = v.findViewById(R.id.btn_kill_all);
        scanSpinner  = v.findViewById(R.id.scan_spinner);

        RecyclerView rv = v.findViewById(R.id.rv_ram_apps);
        rv.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new RamAppAdapter(items, 1, this::killApp);
        rv.setAdapter(adapter);

        v.findViewById(R.id.btn_refresh).setOnClickListener(x -> scan());

        btnKillAll.setOnClickListener(x -> {
            if (!isAdded()) return;
            if (items.isEmpty()) { toast("No background apps to kill"); return; }
            List<RamAppItem> bg = new ArrayList<>();
            for (RamAppItem i : items)
                if (!"Foreground".equals(i.processState)) bg.add(i);
            if (bg.isEmpty()) { toast("No background processes found"); return; }

            new AlertDialog.Builder(requireContext())
                .setTitle("Kill " + bg.size() + " Background Apps")
                .setMessage("This will force-stop all background and cached processes. Apps can restart automatically.")
                .setPositiveButton("Kill All", (d, w) -> killAll(bg))
                .setNegativeButton("Cancel", null)
                .show();
        });

        scan();
    }

    // ── SCAN ──────────────────────────────────────────────────────────────

    private void scan() {
        if (scanning || !isAdded()) return;
        scanning = true;
        if (scanSpinner != null) scanSpinner.setVisibility(View.VISIBLE);
        if (tvScanHint  != null) tvScanHint.setText("Scanning memory...");

        Context appCtx = requireContext().getApplicationContext();

        new Thread(() -> {
            List<RamAppItem> result = new ArrayList<>();
            long totalPssKb = 0;

            try {
                ActivityManager am = (ActivityManager) appCtx.getSystemService(Context.ACTIVITY_SERVICE);
                PackageManager  pm = appCtx.getPackageManager();

                // ── System RAM info ────────────────────────────────────────
                ActivityManager.MemoryInfo memInfo = new ActivityManager.MemoryInfo();
                am.getMemoryInfo(memInfo);
                final long totalRam   = memInfo.totalMem  / 1024; // KB
                final long availRam   = memInfo.availMem  / 1024; // KB
                final long usedRamKb  = totalRam - availRam;

                // ── Running processes ──────────────────────────────────────
                List<ActivityManager.RunningAppProcessInfo> procs = am.getRunningAppProcesses();
                if (procs == null) procs = new ArrayList<>();

                // Batch-fetch memory info for all pids at once (much faster)
                int[] pids = new int[procs.size()];
                for (int i = 0; i < procs.size(); i++) pids[i] = procs.get(i).pid;
                Debug.MemoryInfo[] memInfos = am.getProcessMemoryInfo(pids);

                for (int i = 0; i < procs.size(); i++) {
                    ActivityManager.RunningAppProcessInfo proc = procs.get(i);
                    if (proc.pkgList == null || proc.pkgList.length == 0) continue;

                    String pkg = proc.pkgList[0];
                    // Skip our own process
                    if (pkg.equals(appCtx.getPackageName())) continue;

                    long pss     = memInfos[i].getTotalPss();     // KB
                    long privDir = memInfos[i].getTotalPrivateDirty(); // KB
                    if (pss < 100) continue; // skip tiny kernel threads

                    RamAppItem item = new RamAppItem();
                    item.pid        = proc.pid;
                    item.packageName = pkg;
                    item.pssKb      = pss;
                    item.privateKb  = privDir;
                    item.importance = proc.importance;

                    // App name + icon
                    try {
                        android.content.pm.ApplicationInfo ai = pm.getApplicationInfo(pkg, 0);
                        item.appName = pm.getApplicationLabel(ai).toString();
                        item.icon    = pm.getApplicationIcon(pkg);
                    } catch (Exception e) {
                        item.appName = pkg;
                        item.icon    = pm.getDefaultActivityIcon();
                    }

                    // Process state label
                    item.processState = stateLabel(proc.importance);

                    totalPssKb += pss;
                    result.add(item);
                }

                // Sort by PSS descending (highest RAM usage first)
                result.sort((a, b) -> Long.compare(b.pssKb, a.pssKb));

                final List<RamAppItem> finalResult = result;
                final long finalTotalPss = totalPssKb;
                final long finalTotalRam = totalRam;
                final long finalUsed     = usedRamKb;
                final long finalFree     = availRam;

                if (!isAdded() || getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (!isAdded() || getView() == null) return;

                    items.clear();
                    items.addAll(finalResult);
                    adapter.update(items, finalTotalPss > 0 ? finalTotalPss : 1);

                    // Update total RAM bar
                    int usedPct = (int) (finalUsed * 100L / Math.max(1, finalTotalRam));
                    tvTotalUsed.setText(fmtMb(finalUsed));
                    tvTotalFree.setText(fmtMb(finalFree) + " free");
                    tvUsedPct.setText(usedPct + "%");

                    totalBarFill.post(() -> {
                        View parent = (View) totalBarFill.getParent();
                        if (parent != null) {
                            ViewGroup.LayoutParams lp = totalBarFill.getLayoutParams();
                            lp.width = (int) (parent.getWidth() * usedPct / 100f);
                            totalBarFill.setLayoutParams(lp);
                        }
                    });

                    int barColor = usedPct >= 80 ? 0xFFE05A5A : usedPct >= 60 ? 0xFFE8A440 : 0xFF5B8DEF;
                    totalBarFill.setBackgroundColor(barColor);
                    tvUsedPct.setTextColor(barColor);

                    String hint = finalResult.size() + " processes using " + fmtMb(finalTotalPss);
                    tvScanHint.setText(hint);
                    if (scanSpinner != null) scanSpinner.setVisibility(View.GONE);
                    scanning = false;
                });

            } catch (Exception e) {
                e.printStackTrace();
                if (!isAdded() || getActivity() == null) return;
                getActivity().runOnUiThread(() -> {
                    if (!isAdded()) return;
                    scanning = false;
                    if (scanSpinner != null) scanSpinner.setVisibility(View.GONE);
                    tvScanHint.setText("Scan failed — check permissions");
                });
            }
        }).start();
    }

    // ── KILL ONE ──────────────────────────────────────────────────────────

    private void killApp(RamAppItem item) {
        if (!isAdded()) return;
        new AlertDialog.Builder(requireContext())
            .setTitle("Kill " + item.appName + "?")
            .setMessage("RAM used: " + item.getPssFormatted() + "\n\n" +
                        "Kill background process to free RAM? The app may restart automatically.")
            .setPositiveButton("Kill", (d, w) -> {
                try {
                    ActivityManager am = (ActivityManager) requireContext()
                        .getSystemService(Context.ACTIVITY_SERVICE);
                    am.killBackgroundProcesses(item.packageName);
                    toast(item.appName + " killed");
                    // Remove from list immediately for feedback
                    items.remove(item);
                    adapter.notifyDataSetChanged();
                    // Re-scan after short delay
                    new Handler(Looper.getMainLooper()).postDelayed(this::scan, 800);
                } catch (Exception e) {
                    e.printStackTrace();
                    // Fallback: open app settings
                    openAppSettings(item.packageName);
                }
            })
            .setNeutralButton("Open Settings", (d, w) -> openAppSettings(item.packageName))
            .setNegativeButton("Cancel", null)
            .show();
    }

    // ── KILL ALL BACKGROUND ───────────────────────────────────────────────

    private void killAll(List<RamAppItem> bg) {
        if (!isAdded()) return;
        if (scanSpinner != null) scanSpinner.setVisibility(View.VISIBLE);
        tvScanHint.setText("Killing " + bg.size() + " processes...");

        Context appCtx = requireContext().getApplicationContext();
        new Thread(() -> {
            int killed = 0;
            try {
                ActivityManager am = (ActivityManager)
                    appCtx.getSystemService(Context.ACTIVITY_SERVICE);
                for (RamAppItem it : bg) {
                    if ("Foreground".equals(it.processState)) continue;
                    try {
                        am.killBackgroundProcesses(it.packageName);
                        killed++;
                        Thread.sleep(30);
                    } catch (Exception e) { e.printStackTrace(); }
                }
            } catch (Exception e) { e.printStackTrace(); }

            final int k = killed;
            if (!isAdded() || getActivity() == null) return;
            getActivity().runOnUiThread(() -> {
                if (!isAdded()) return;
                toast("Killed " + k + " background processes");
                new Handler(Looper.getMainLooper()).postDelayed(this::scan, 1000);
            });
        }).start();
    }

    // ── HELPERS ───────────────────────────────────────────────────────────

    private String stateLabel(int importance) {
        if (importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
            return "Foreground";
        if (importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_SERVICE)
            return "Service";
        if (importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE)
            return "Visible";
        if (importance <= ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED)
            return "Background";
        return "Cached";
    }

    private String fmtMb(long kb) {
        if (kb >= 1024) return String.format("%.1f GB", kb / 1024f / 1024f);
        return (kb / 1024) + " MB";
    }

    private void openAppSettings(String pkg) {
        if (!isAdded()) return;
        try {
            Intent i = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            i.setData(Uri.fromParts("package", pkg, null));
            startActivity(i);
        } catch (Exception e) { e.printStackTrace(); }
    }

    private void toast(String msg) {
        if (!isAdded() || getContext() == null) return;
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show();
    }
}