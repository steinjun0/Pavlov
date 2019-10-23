package kr.osam.pavlov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AppSelectActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    AppUseTimeCheckService appUseTimeCheckService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_select);

        //서비스 커넥션 생성
        final ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //서비스와 연결되었을 때 호출되는 메서드
                AppUseTimeCheckService.AppUseBinder appUseBinder = (AppUseTimeCheckService.AppUseBinder) service;
                appUseTimeCheckService = appUseBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        final Intent service_intent = new Intent(AppSelectActivity.this, AppUseTimeCheckService.class);
        bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);



        final PackageManager pm = getPackageManager();

        //설치된 패키지들의 리스트
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<Drawable> icons = new ArrayList<Drawable>();

        if(Build.VERSION.SDK_INT >= 21)
        {
            //startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);

            for (ApplicationInfo packageInfo : packages) {
                String packageName = packageInfo.packageName;
                try
                {
                    icons.add(getPackageManager().getApplicationIcon(packageName));
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

        }

        // Prepare grid view
        GridView gridView = findViewById(R.id.gv_appselect);
        PackageIconGridViewAdapter gridAdapter = new PackageIconGridViewAdapter(this, R.layout.item_packageicon, icons);

        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String temp = "-1";
                if(Build.VERSION.SDK_INT >= 21)
                {
                    if(appUseTimeCheckService.getlUsageStatsMap().get(packages.get(position).packageName) != null)
                        temp = String.valueOf(appUseTimeCheckService.getlUsageStatsMap().get(packages.get(position).packageName).getTotalTimeInForeground());
                }
                Log.d("getServiceData", temp);
            }
        });
    }

}