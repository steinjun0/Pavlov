package kr.osam.pavlov.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.Missons.StepCountMisson;
import kr.osam.pavlov.PavlovDBParser;

import static android.util.Log.d;

public class MissionManager extends Service {

    public List<Mission>  missionList = new ArrayList<>();

    private List<Intent>  intentList  = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<manageConn> mConn = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);

    boolean isManagerRunning;

    PavlovDBParser dbManager = new PavlovDBParser(this);


    public MissionManager() { }

    @Override
    public void onCreate() {
        //missionList.addAll(dbManager.readMission());
        isManagerRunning = false;
        super.onCreate();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        //throw new UnsupportedOperationException("Not yet implemented");
        Calendar tmp =  Calendar.getInstance();
        tmp.set(2019,10,23,10,00);
        missionList.add(new StepCountMisson("jip",0, 2000, 0, 0x4, tmp));
        missionList.add(new StepCountMisson("gagosipda",0, 4000, 0, 0x4, tmp));

        WatcherThread thread = new WatcherThread();

        isManagerRunning = true;

        initIntent();
        thread.start();

        setServiceOnForeGround();

        return new MissionManagerBinder();
    }

    @Override
    public void onDestroy() {
        isManagerRunning=false;
        removeServiceOnForeground();
        super.onDestroy();
    }

    public void addMission(Mission _mission) { missionList.add(_mission); }

    private boolean containMission(int _missiontype)
    {
        for(Mission mission : missionList) { if(mission.getType() == _missiontype) return true; Log.d("Test", " " + mission.getType()); }
        return false;
    }
    private void circuitBraker()
    {
        for(int missiontype = 0; missiontype < Mission.MISSION_NUM_OF_MISSION_TYPE; missiontype++)
        {
            if( containMission(missiontype) && mConn.get(missiontype).m_service==null )
            {
                bindService(intentList.get(missiontype),mConn.get(missiontype), BIND_AUTO_CREATE);
            }
            if( !containMission(missiontype) && mConn.get(missiontype).m_service!=null )
            {
                unbindService(mConn.get(missiontype));
            }
        }
    }

    class MissionManagerBinder extends Binder{
        MissionManager getService() { return MissionManager.this; }
    }

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

    class WatcherThread extends Thread
    {
        @Override
        public void run() {
            while (isManagerRunning)
            {
                try {
                    long tmpTime = SystemClock.currentThreadTimeMillis();

                    circuitBraker();

                    //TODO:
                    for(Mission curruntMission : missionList)
                    {
                        if(curruntMission.getCondition()==0)
                        {
                            curruntMission.upDate(mConn.get(curruntMission.getType()).m_service);
                            Log.d("test","" +((StepCounterService.StepCounterBinder)mConn.get(curruntMission.getType()).m_service).getService().getSteps());
                        }
                    }

                    tmpTime = SystemClock.currentThreadTimeMillis() - tmpTime;
                    sleep((100 - tmpTime)>0 ? (100 - tmpTime) : 0);
                } catch (Exception e) { Log.d("CatchExeption", e.toString()); }
            }

            removeServiceOnForeground();

            super.run();
        }
    }

    private void setServiceOnForeGround()
    {
        // 안드로이드 8.0 이상이면 노티피케이션 메시지를 띄우고 포그라운드 서비스로 운영한다.
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            NotificationChannel channel = new NotificationChannel("Pavlov", "Pavlov", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pavlov");
            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("Pavlov가 당신을 지켜보고있습니다");
            builder.setContentText("Pavlov가 당신의 목표를 응원합니다.");
            builder.setAutoCancel(true);
            Notification notification = builder.build();
            // 현재 노티피케이션 메시즈를 포그라운드 서비스의 메시지로 등록한다.
            startForeground(0x00, notification);
        }
    }
    private void removeServiceOnForeground()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(STOP_FOREGROUND_REMOVE);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(0x00);
        }
    }
}
