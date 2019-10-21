package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.ActivityManager;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;

import java.util.Iterator;
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

        if(Build.VERSION.SDK_INT >= 21)
        {
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);


            UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
            Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                    queryAndAggregateUsageStats(0, System.currentTimeMillis());

        /*long totalTimeUsageInMillis = lUsageStatsMap.get("kr.osam.pavlov").
                getTotalTimeInForeground();*/

            //String temp = String.valueOf(totalTimeUsageInMillis);

            Iterator<String> iterator = lUsageStatsMap.keySet().iterator();
            String temp = "";
            while(iterator.hasNext()) {
                String key = iterator.next();
                temp += key + " ";
            }


            TextView tv = findViewById(R.id.tv_appusetime);
            tv.setText(temp);
        }

    }
}
