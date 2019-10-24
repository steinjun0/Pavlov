package kr.osam.pavlov.Services;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Map;

public class AppUseTimeCheckService extends Service {

    /*
    모든 어플리케이션의 사용 시간을 확인하는 서비스입니다.
    .getTime(String pkgName) 메서드로 해당 패키지의 사용 시간을 int형으로 반환받을 수 있습니다.
     */

    UsageStatsManager usageStatsManager;
    Map<String, UsageStats> usageStatsMap;
    String tempPkgName = "kr.osam.pavlov";
    long tempPkgUseTime;
    //boolean serviceStatus = false;

    UpdateAppUseTime thread;

    AppUseBinder appUseBinder =  new AppUseBinder();

    public class AppUseBinder extends Binder{

        public AppUseTimeCheckService getService(){    return AppUseTimeCheckService.this;  }

    }

    private String recentlyUsedPkgName() {

        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return null;
        }
        String packageName = "";
        long endTime = System.currentTimeMillis();
        long beginTime = endTime - 10000;

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
    public void onCreate() {
        super.onCreate();
        Log.d("AppUseTimeCheckService", "onCreate()");
        //1초마다 앱 사용시간 업데이트
        if(Build.VERSION.SDK_INT < 21)
            return;
        usageStatsManager = (UsageStatsManager) this.getSystemService(USAGE_STATS_SERVICE);

        thread  = new UpdateAppUseTime();
        thread.start();
    }

    class UpdateAppUseTime extends Thread {

        public void run(){
            while (true) {

                if(Build.VERSION.SDK_INT < 21)
                {
                    return;
                }
                //오늘 사용한 어플리케이션들의 데이터

                usageStatsMap = usageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis()-System.currentTimeMillis()%86400000, System.currentTimeMillis());

                String temp = recentlyUsedPkgName();
                Log.d("AppUseTimeCheckService", tempPkgName);
//                Log.d("AppUseTimeCheckService", String.valueOf(usageStatsMap.get(tempPkgName).getTotalTimeInForeground() + tempPkgUseTime));

                if(temp == "" || temp.equals(tempPkgName))
                {
                    tempPkgUseTime += 1000;
                }
                else
                {
                    tempPkgName = temp;
                    tempPkgUseTime = 1000;
                }

                try{
                    Thread.sleep(1000);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d("AppUseTimeCheckService", "onBind()");


        return appUseBinder;
    }

    @Override
    public void onDestroy() {
        Log.d("AppUseTimeCheckService", "onDestroy");
        thread.interrupt();
        super.onDestroy();
        //serviceStatus = false;
        // 서비스가 종료될 때 실행
    }

    public int getTime(String pkgName)
    {
        //패키지 이름을 패러미터로 전달하면 해당 패키지의 사용 시간을 반환합니다.
        //안드로이드 버전이 낮아 사용할 수 없으면 -1, 해당 패키지가 실행기록에 없으면 -2를 반환합니다.

        int time = -1;
        if(Build.VERSION.SDK_INT > 20)
        {
            if(usageStatsMap.containsKey(pkgName))
            {
                time = (int) usageStatsMap.get(pkgName).getTotalTimeInForeground();
                if(tempPkgName.equals(pkgName))
                    time += tempPkgUseTime;
            }

            else
                time = -2;
        }


        return time;
    }


}