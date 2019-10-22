package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class UsageStatsManagerTestActivity extends AppCompatActivity {


    public int checkPermissions()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return -1;
        }
        String[] permissions = {
                Manifest.permission.PACKAGE_USAGE_STATS};
        int chk = checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
        if(chk == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(permissions,0);
            return 1;
        }
        return 0;
    }

    private String getForegroundPackageName() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return null;
        }
        String packageName = null;
        if(checkPermissions() == -1)
        {
            packageName = "Low Version";
        }
        else if(checkPermissions() == 0)
        {
            packageName = "Permission Success";
        }
        UsageStatsManager usageStatsManager = (UsageStatsManager)getSystemService(this.USAGE_STATS_SERVICE);
        final long endTime = System.currentTimeMillis();
        final long beginTime = endTime - 10000;
        final UsageEvents usageEvents = usageStatsManager.queryEvents(beginTime, endTime);
        while (usageEvents.hasNextEvent()) {
            UsageEvents.Event event = new UsageEvents.Event();
            usageEvents.getNextEvent(event);
            if (event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                packageName = event.getPackageName();
            }
        }
        return packageName;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_stats_manager_test);

        final PackageManager pm = getPackageManager();
//get a list of installed apps.
        List<ApplicationInfo> packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        try
        {
            Drawable icon = getPackageManager().getApplicationIcon("com.egert.piano");
            ImageView iv = findViewById(R.id.iv_applist);
            iv.setImageDrawable(icon);
        }
        catch (PackageManager.NameNotFoundException e)
        {
            e.printStackTrace();
        }



        if(Build.VERSION.SDK_INT >= 21)
        {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);


            UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
            Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                    queryAndAggregateUsageStats(System.currentTimeMillis()-System.currentTimeMillis()%86400000, System.currentTimeMillis());

        /*long totalTimeUsageInMillis = lUsageStatsMap.get("kr.osam.pavlov").
                getTotalTimeInForeground();*/

            //String temp = String.valueOf(totalTimeUsageInMillis);

            //Iterator<String> iterator = lUsageStatsMap.keySet().iterator();
            //Iterator<ResolveInfo> listIterator = pkgAppsList.listIterator();

            String temp = "";

            for (ApplicationInfo packageInfo : packages) {
                String packageName = packageInfo.packageName;
                long packageUsedTime = 0;
                if(lUsageStatsMap.containsKey(packageName))
                {
                    packageUsedTime = lUsageStatsMap.get(packageInfo.packageName).getTotalTimeInForeground()/1000;
                }

                temp += packageName + ": " + packageUsedTime + " 초\n";

                //Log.d(TAG, "Source dir : " + packageInfo.sourceDir);
                //Log.d(TAG, "Launch Activity :" + pm.getLaunchIntentForPackage(packageInfo.packageName));
            }
            /*while(listIterator.hasNext()) {
                String pkgName = listIterator.next().resolvePackageName;
                temp += pkgName + ": " + lUsageStatsMap.get(pkgName).getTotalTimeInForeground()/1000 + " 초\n";
            }*/




            TextView tv = findViewById(R.id.tv_appusetime);
            tv.setText(temp);
        }

    }
}
