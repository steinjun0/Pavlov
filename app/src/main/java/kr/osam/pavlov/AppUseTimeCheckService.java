package kr.osam.pavlov;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

//모든 어플리케이션의 현황을 List로 저장해 둔다 -> 설치된 어플리케이션이 변동되면? 업데이트 버튼 등을 통해 새로 받아오기
//검사할 어플리케이션의 패키지 이름을 List로 저장해 둔다
//검사할 어플리케이션의 사용 제한수치를 저장한다

//1분마다 어플리케이션의 사용 현황을 받아온다
// 어플리케이션을 검사하고 사용량을 브리핑한다
public class AppUseTimeCheckService extends Service {

    UsageStatsManager mUsageStatsManager;
    Map<String, UsageStats> lUsageStatsMap;
    Map<String, Long> checkTargetData;

    IBinder mBinder =  new AppUseBinder();



    @Override
    public void onCreate() {
        super.onCreate();
        //UsageStatsManager 생성
        if(Build.VERSION.SDK_INT >= 21)
        {

        }
        //1분마다 앱 사용시간 업데이트
        Timer timer = new Timer();
        //timer.schedule(timerTask, 0, 60000);
        timer.schedule(timerTask, 0, 1000);
    }
    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            updateAppUseTime();
        }
    };

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

    private boolean updateAppUseTime()
    {
        if(Build.VERSION.SDK_INT < 21)
        {
            return false;
        }
        //오늘 사용한 어플리케이션들의 데이터
        mUsageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
        lUsageStatsMap = mUsageStatsManager.queryAndAggregateUsageStats(System.currentTimeMillis()-System.currentTimeMillis()%86400000, System.currentTimeMillis());
        Log.d("ServiceLoop", String.valueOf(lUsageStatsMap.get("kr.osam.pavlov").getTotalTimeInForeground()));
        return true;
    }

    public class AppUseBinder extends Binder{

        public AppUseTimeCheckService getService(){    return AppUseTimeCheckService.this;  }

        /*public Map<String, Long> getCheckTarget()
        {
            return checkTargetData;
        }
        public void addCheckTarget(String pkgName, long limitTime)
        {
            checkTargetData.put(pkgName, limitTime);
        }*/
    }

    /*public long getUsedTime(String pkgName)
    {
        if(Build.VERSION.SDK_INT < 21)
        {
            return -1;
        }
        long time = lUsageStatsMap.get(pkgName).getTotalTimeInForeground();

        return time;
    }*/

    public Map<String, UsageStats> getlUsageStatsMap()
    {
        return lUsageStatsMap;
    }
}