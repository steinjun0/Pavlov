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
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class UsageStatsManagerTestActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    AppUseTimeCheckService appUseTimeCheckService;

    TextView tv = findViewById(R.id.tv_appusetime);

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            //서비스와 연결되었을 때 호출되는 메서드
            Log.d("appUseTimeService", "Binding Connected");
            AppUseTimeCheckService.AppUseBinder appUseBinder = (AppUseTimeCheckService.AppUseBinder) service;
            appUseTimeCheckService = appUseBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("appUseTimeService", "Binding Disconnected");
        }
    };

    //어플리케이션을 선택하는 다이얼로그 생성 메소드
    private void showAlertDialog(ArrayList<Drawable> icons, final Context context) {

        // Prepare grid view
        GridView gridView = new GridView(this);
        PackageIconGridViewAdapter gridAdapter = new PackageIconGridViewAdapter(this, R.layout.item_packageicon, icons);

        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //서비스 생성


                //새로운 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                TextView dialogTv = new TextView(context);
                //dialogTv.setText(String.valueOf(appUseTimeCheckService.getUsedTime(packages.get(position).packageName)));
                builder.setView(dialogTv);
                builder.setTitle(packages.get(position).packageName);
                builder.show();
            }
        });

        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("App Selection");
        builder.show();
    }

    //PACKAGE_USAGE_STATS권한을 갖고 있는지 체크
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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_usage_stats_manager_test);

        //////////////////////
        //나중에 지울 놈들
        //서비스 관련 테스트용





        //////////////////////

        final PackageManager pm = getPackageManager();

        //설치된 패키지들의 리스트
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<Drawable> icons = new ArrayList<Drawable>();

        if(Build.VERSION.SDK_INT >= 21)
        {
            //startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);

            //오늘 사용한 패키지들의 사용 시간
            /*UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
            Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                    queryAndAggregateUsageStats(System.currentTimeMillis()-System.currentTimeMillis()%86400000, System.currentTimeMillis());

            //사용 시간 정보 보여주기, 디버그용
            String temp = String.valueOf(lUsageStatsMap.get("kr.osam.pavlov").getTotalTimeInForeground()/1000);*/

            /*for (ApplicationInfo packageInfo : installedPackages) {
                String packageName = packageInfo.packageName;
                long packageUsedTime = 0;
                if(lUsageStatsMap.containsKey(packageName))
                {
                    packageUsedTime = lUsageStatsMap.get(packageInfo.packageName).getTotalTimeInForeground()/1000;
                }

                temp += packageName + ": " + packageUsedTime + " 초\n";

                try
                {
                    icons.add(getPackageManager().getApplicationIcon(packageName));
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }
            }*/

            //appUseTimeCheckService = new AppUseTimeCheckService();

            Intent service_intent = new Intent(this, AppUseTimeCheckService.class);
            bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);
            Log.d("customService", "//////////after appUseTimeCheckService");

            //String name = appUseTimeCheckService.getPackageName();//서비스쪽 메소드로 값 전달 받아 호출

            //String temp = String.valueOf(appUseTimeCheckService.getUsedTime());

            //tv.setText(temp);
            Log.d("customService", "//////////before appUseTimeCheckService");


            //앱 선택 창 띄워주기
            //showAlertDialog(icons, this);
        }
        else
        {

        }

    }

    @Override
    public void onDestroy() {

        unbindService(serviceConnection);

        super.onDestroy();
    }
}


//실행되고 있는 패키지 이름 가져오기
    /*private String getForegroundPackageName() {

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
    }*/