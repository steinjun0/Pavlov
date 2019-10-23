package kr.osam.pavlov;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.HashMap;
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

    UpdateAppUseTime thread;

    IBinder mBinder =  new AppUseBinder();



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
            if (event.getEventType() == UsageEvents.Event.ACTIVITY_RESUMED) {
                packageName = event.getPackageName();
            }
        }
        return packageName;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        //1초마다 앱 사용시간 업데이트
        if(Build.VERSION.SDK_INT < 21)
            return;
        usageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);

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
                Log.d("ServiceLoop", tempPkgName);
                Log.d("ServiceLoop", String.valueOf(usageStatsMap.get(tempPkgName).getTotalTimeInForeground() + tempPkgUseTime));

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
        return mBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return super.onStartCommand(intent, flags, startId);
    }

    public class AppUseBinder extends Binder{

        public AppUseTimeCheckService getService(){    return AppUseTimeCheckService.this;  }

    }

    public int getTime(String pkgName)
    {
        //패키지 이름을 패러미터로 전달하면 해당 패키지의 사용 시간을 반환합니다.
        //안드로이드 버전이 낮아 사용할 수 없으면 -1, 해당 패키지가 실행기록에 없으면 -2를 반환합니다.

        int time = -1;
        if(Build.VERSION.SDK_INT > 20)
        {
            if(usageStatsMap.containsKey(pkgName))
                time = (int) usageStatsMap.get(pkgName).getTotalTimeInForeground();
            else
                time = -2;
        }


        return time;
    }


}