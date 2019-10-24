package kr.osam.pavlov.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.location.Location;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import kr.osam.pavlov.Missons.GpsCountMission;
import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.Missons.StepCountMisson;
import kr.osam.pavlov.PavlovDBParser;

public class MissionManager extends Service {

    public List<Mission>  missionList = new ArrayList<>();

    private List<Intent>  intentList  = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<manageConn> mConn = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);

    boolean isManagerRunning;

    public class MissionManagerBinder extends Binder{
        public MissionManager getService() { return MissionManager.this; }
    }

    MissionManagerBinder binder;

    PavlovDBParser dbManager = new PavlovDBParser(this);


    public MissionManager() { }

    @Override
    public void onCreate() {
        //missionList.addAll(dbManager.readMission());
        isManagerRunning = false;

        Calendar tmp =  Calendar.getInstance();
        tmp.set(2019,9,24,14,00);

        missionList.add(new StepCountMisson("집에",0, 50, 0, tmp));
        missionList.add(new GpsCountMission("가고",0, 30, 0, tmp, new ArrayList<Location> ()));
        missionList.add(new StepCountMisson("싶다",0, 20000, 0, tmp));

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        //throw new UnsupportedOperationException("Not yet implemented");

        WatcherThread thread = new WatcherThread();
        binder = new MissionManagerBinder();

        isManagerRunning = true;

        initIntent();
        thread.start();

        setServiceOnForeGround();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    class WatcherThread extends Thread
    {
        @Override
        public void run() {
            while (isManagerRunning)
            {
                try {
                    long tmpTime = SystemClock.currentThreadTimeMillis();

                    circuitBraker();
                    setBind();



                    for(Mission curruntMission : missionList)
                    {
                        if(curruntMission.getCondition()==0)
                        {
                            if(mConn.get(curruntMission.getType()).m_service != null)
                            {
                                curruntMission.upDate(mConn.get(curruntMission.getType()).m_service);
                            }
                        }
                    }



                    unsetBind();

                    tmpTime = SystemClock.currentThreadTimeMillis() - tmpTime;
                    sleep((50 - tmpTime)>0 ? (50 - tmpTime) : 0);

                } catch (Exception e) { Log.d("CatchExeption", e.toString()); }
            }
            removeServiceOnForeground();

            super.run();
        }
    }

    public void addMission(Mission _mission) { missionList.add(_mission); }

    private void initIntent()
    {
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent(this, GPSDistanceService.class));
        intentList.add(new Intent(this, StepCounterService.class));

        for(int i = 0; i < 5; i++)
        {
            mConn.add(new manageConn());
        }
    }

    class manageConn implements ServiceConnection{
        public IBinder m_service;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { m_service = service;}
        @Override
        public void onServiceDisconnected(ComponentName name) { m_service = null; }
    }

    private boolean containMission(int _missiontype)
    {
        for(Mission mission : missionList)
        {
            if(mission.getType() == _missiontype
                    && mission.getCondition() == Mission.MISSION_ON_PROGRESS) return true;
        }
        return false;
    }
    private void circuitBraker()
    {
        for(int missiontype = 0; missiontype < Mission.MISSION_NUM_OF_MISSION_TYPE; missiontype++)
        {
            if( containMission(missiontype) && mConn.get(missiontype).m_service==null )
            {
                Log.d("Missions", missiontype + "st Mission Service is On." );
                startService(intentList.get(missiontype));
                //bindService(intentList.get(missiontype),mConn.get(missiontype), BIND_AUTO_CREATE);
            }
            if( (!containMission(missiontype)) && mConn.get(missiontype).m_service!=null )
            {
                Log.d("Missions", missiontype + "st Mission Service is Off." );
                stopService(intentList.get(missiontype));
                //unbindService(mConn.get(missiontype));
            }
        }
    }

    private void setBind()
    {
        for(int missiontype = 0; missiontype < Mission.MISSION_NUM_OF_MISSION_TYPE; missiontype++)
        {
            if( containMission(missiontype) )
            {
                bindService(intentList.get(missiontype),mConn.get(missiontype), BIND_ABOVE_CLIENT);
            }
        }
    }
    private void unsetBind()
    {
        for(int missiontype = 0; missiontype < Mission.MISSION_NUM_OF_MISSION_TYPE; missiontype++)
        {
            if( containMission(missiontype) )
            {
                unbindService(mConn.get(missiontype));
            }
        }

    }

    private void setServiceOnForeGround()
    {
        // 안드로이드 8.0 이상이면 노티피케이션 메시지를 띄우고 포그라운드 서비스로 운영한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("Pavlov", "Pavlov", NotificationManager.IMPORTANCE_DEFAULT);

            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            Notification notification = new NotificationCompat.Builder(this, "Pavlov")
                    .setSmallIcon(android.R.drawable.ic_menu_search)
                    .setContentTitle("Pavlov가 당신을 지켜보고있습니다")
                    .setContentText("Pavlov가 당신의 목표를 응원합니다.")
                    .setAutoCancel(true)
                    .build();

            startForeground(0xFF, notification);
        }
    }

    private void removeServiceOnForeground()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(STOP_FOREGROUND_REMOVE);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(0xFF);
        }
    }

    @Override
    public void onDestroy() {
        isManagerRunning=false;
        SystemClock.sleep(150);
        removeServiceOnForeground();
        super.onDestroy();
    }
}
