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
import java.util.List;

import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.PavlovDBParser;

public class MissionManager extends Service {

    public List<Mission>  missionList = new ArrayList<>();
    private List<Intent>  intentList  = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<manageConn> mConn = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<Boolean> connectionStatus = new ArrayList<>();

    boolean isManagerRunning;

    public class MissionManagerBinder extends Binder{
        public MissionManager getService() { return MissionManager.this; }
    }

    MissionManagerBinder binder;

    PavlovDBParser dbManager = new PavlovDBParser(this);


    //생성자
    public MissionManager() { }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //throw new UnsupportedOperationException("Not yet implemented");
//        Calendar tmp =  Calendar.getInstance();
//        tmp.set(2019,10,24,23,00);

        binder = new MissionManagerBinder();

        isManagerRunning = true;

        //initIntent();
        initializeLists();
        ThreadAction manageMissions = new ThreadAction() ;
        Thread serviceThread = new Thread(manageMissions) ;
        serviceThread.start() ;

        setServiceOnForeGround();

        return super.onStartCommand(intent, flags, startId);
    }
    private void initializeLists()
    {
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent(this, AppUseTimeCheckService.class));
        intentList.add(new Intent(this, GPSDistanceService.class));
        intentList.add(new Intent(this, StepCounterService.class));

        for(int i = 0; i < 5; i++)
        {
            mConn.add(new manageConn());
            connectionStatus.add(false);
        }
    }


    @Override
    public IBinder onBind(Intent intent) {

        return binder;
    }

    class ThreadAction implements Runnable {

        @Override
        public void run() {
            while (isManagerRunning)
            {
                Log.d("WatcherThread", "running");
                setServiceLife();

                for(Mission currentMission : missionList)
                {
                    Log.d("MissionStatus", String.valueOf(currentMission.getCondition()));
                    if((currentMission.getCondition()==0) && connectionStatus.get(currentMission.getType()))
                    {

                        currentMission.upDate(mConn.get(currentMission.getType()).m_service);
                    }
                }

                try
                {
                    Thread.sleep(1000);
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }


            }
            //removeServiceOnForeground();
        }
    }

    private void setServiceLife()
    {
        for(int missionType = 0; missionType < Mission.MISSION_NUM_OF_MISSION_TYPE; missionType++)
        {
            if(containMission(missionType) && !connectionStatus.get(missionType))
            {
                //startService(intentList.get(missionType));
                bindService(intentList.get(missionType),mConn.get(missionType), BIND_AUTO_CREATE);
                connectionStatus.set(missionType, true);
            }
            else if(!containMission(missionType) && connectionStatus.get(missionType))
            {
                //stopService(intentList.get(missionType));
                unbindService(mConn.get(missionType));
                connectionStatus.set(missionType, false);
            }
        }
    }

    //missionList에 condition이 Mission.MISSION_ON_PROGRESS이고 missionType이 _missiontype인 미션이 있을 경우 true를 반환
    private boolean containMission(int _missiontype)
    {
        for(Mission mission : missionList)
        {
            if((mission.getType() == _missiontype) && (mission.getCondition() == Mission.MISSION_ON_PROGRESS))
            {
                return true;
            }
        }
        return false;
    }


//    class WatcherThread extends Thread
//    {
//        @Override
//        public void run() {
//            while (isManagerRunning)
//            {
//                //Log.d("WatcherThread", "running");
//                try {
//                    long tmpTime = SystemClock.currentThreadTimeMillis();
//
//                    setServiceLife();
//
//                    for(Mission curruntMission : missionList)
//                    {
//                        Log.d("MissionStatus", String.valueOf(curruntMission.getCondition()));
//                        if(curruntMission.getCondition()==0)
//                        {
//                            curruntMission.upDate(mConn.get(curruntMission.getType()).m_service);
//                        }
//                    }
//
////                    tmpTime = SystemClock.currentThreadTimeMillis() - tmpTime;
////                    sleep((50 - tmpTime)>0 ? (50 - tmpTime) : 0);
//
//                    sleep(1000);
//                } catch (Exception e) { /*Log.d("CatchExeption", e.toString());*/ }
//            }
//            removeServiceOnForeground();
//
//            super.run();
//        }
//    }

    public void addMission(Mission _mission) {
        missionList.add(_mission);
        Log.d("MissionManager", _mission.getMissionID() + "th mission added");
    }

    private void initIntent()
    {
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent("TODO"));
        intentList.add(new Intent(this, AppUseTimeCheckService.class));
        intentList.add(new Intent(this, GPSDistanceService.class));
        intentList.add(new Intent(this, StepCounterService.class));

        for(int i = 0; i < 5; i++)
        {
            mConn.add(new manageConn());
        }
    }

    class manageConn implements ServiceConnection{
        public IBinder m_service = null;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { m_service = service;}
        @Override
        public void onServiceDisconnected(ComponentName name) { m_service = null; }
    }


    private void circuitBraker()
    {
        for(int missiontype = 0; missiontype < Mission.MISSION_NUM_OF_MISSION_TYPE; missiontype++)
        {
            if( containMission(missiontype) && mConn.get(missiontype).m_service==null )
            {
                Log.d("MissionManager", missiontype + "st Mission Service is On.");
                startService(intentList.get(missiontype));
                //bindService(intentList.get(missiontype),mConn.get(missiontype), BIND_AUTO_CREATE);
            }
            if( (!containMission(missiontype)) && mConn.get(missiontype).m_service!=null )
            {
                Log.d("MissionManager", missiontype + "st Mission Service is Off.");
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

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pavlov");
            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("Pavlov가 당신을 지켜보고있습니다");
            builder.setContentText("Pavlov가 당신의 목표를 응원합니다.");
            builder.setAutoCancel(true);
            Notification notification = builder.build();
            // 현재 노티피케이션 메시즈를 포그라운드 서비스의 메시지로 등록한다.
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