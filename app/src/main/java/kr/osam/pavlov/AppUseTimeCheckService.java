package kr.osam.pavlov;

import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

// 서비스 클래스를 구현하려면, Service 를 상속받는다
public class AppUseTimeCheckService extends Service {

    //변수
    private String packageName;
    private long todayAppUseTime;


    private boolean updateAppUseTime()
    {
        if(Build.VERSION.SDK_INT < 21)
        {
            return false;
        }
        UsageStatsManager mUsageStatsManager = (UsageStatsManager) this.getSystemService(this.USAGE_STATS_SERVICE);
        //오늘 사용한 어플리케이션들의 데이터
        Map<String, UsageStats> lUsageStatsMap = mUsageStatsManager.
                queryAndAggregateUsageStats(System.currentTimeMillis()-System.currentTimeMillis()%86400000, System.currentTimeMillis());

        this.todayAppUseTime = lUsageStatsMap.get(this.packageName).getTotalTimeInForeground();
        return true;
    }

    TimerTask timerTask = new TimerTask() {
        @Override
        public void run() {
            updateAppUseTime();
        }
    };

    //생성자
    AppUseTimeCheckService(String packageName, long usedTime)
    {
        this.packageName = packageName;
        this.todayAppUseTime = usedTime;
    }


    @Override
    public IBinder onBind(Intent intent) {
        //최종적인 어플리케이션 사용 시간을 받아올 것
        Log.d("service", "AppUseTimeCheckService Binded");
        return null;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        // 서비스에서 가장 먼저 호출됨(최초에 한번만)

        Log.d("service", "AppUseTimeCheckService Created");
        //1분마다 앱 사용시간 업데이트
        Timer timer = new Timer();
        timer.schedule(timerTask, 0, 600000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // 서비스가 종료될 때 실행

        Log.d("service", "AppUseTimeCheckService ended");
    }

    public String getPackageName()
    {
        return this.packageName;
    }

    public long getUsedTime()
    {
        return this.todayAppUseTime;
    }
}