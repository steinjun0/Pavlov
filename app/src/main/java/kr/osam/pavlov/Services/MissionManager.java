package kr.osam.pavlov.Services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.JsonParser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kr.osam.pavlov.Missons.AppUseTimeMission;
import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.PavlovDBHelper;

public class MissionManager extends Service {

    //모든 미션은 이곳에 저장되고 관리됨
    public List<Mission>  missionList = new ArrayList<>();

    //서비스 바인딩을 위한 리스트들
    private List<Intent>  intentList  = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<MissionServiceConnection> mConn = new ArrayList<>(Mission.MISSION_NUM_OF_MISSION_TYPE);
    private List<Boolean> connectionStatus = new ArrayList<>();

    //미션매니저의 실행 여부를 알려주는 변수
    boolean isManagerRunning;

    //바인더 클래스
    public class MissionManagerBinder extends Binder{
        public MissionManager getService() { return MissionManager.this; }
    }

    //바인더 인스턴스
    MissionManagerBinder binder;


    //DB 클래스
//    PavlovDBHelper dbManager = new PavlovDBHelper(this);
//    SQLiteDatabase missionDB = dbManager.getWritableDatabase();



    //생성자
    public MissionManager() { }

    @Override
    public void onCreate() {
        super.onCreate();

        //미션매니저가 시작되면
        //바인더 초기화
        binder = new MissionManagerBinder();

        isManagerRunning = true;

        //리스트 초기화 및 DB에서 값 받아오기
        initializeLists();

        //반복문 스레드 시작
        ThreadAction manageMissions = new ThreadAction() ;
        Thread serviceThread = new Thread(manageMissions) ;
        serviceThread.start() ;

        //서비스를 ForeGround로 올린다
        setServiceOnForeGround();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        //throw new UnsupportedOperationException("Not yet implemented");
//        Calendar tmp =  Calendar.getInstance();
//        tmp.set(2019,10,24,23,00);



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
            mConn.add(new MissionServiceConnection());
            connectionStatus.add(false);
        }

        //모든 미션 가져오기
        //dbSelect();
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
                //미션들의 컨디션에 따라 서비스의 상태를 관리해준다
                setServiceLife();
                Log.d("Mission Number", ""+missionList.size());

                //모든 미션에 대해
                for(Mission currentMission : missionList)
                {
                    Log.d("MissionStatus", String.valueOf(currentMission.getCondition()));
                    if((currentMission.getCondition()==0) && connectionStatus.get(currentMission.getType()))
                    {
                        //미션의 컨디션이 진행중이면 매 반복마다 업데이트
                        currentMission.upDate(mConn.get(currentMission.getType()).m_service);
                    }
                }

                //매초 반복
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
        //모든 미션타입에 대해
        for(int missionType = 0; missionType < Mission.MISSION_NUM_OF_MISSION_TYPE; missionType++)
        {

            if(containMission(missionType) && !connectionStatus.get(missionType))
            {
                //만약 진행중인 해당 미션타입의 미션이 있고, 해당 서비스의 connectionStatus가 false라면 서비스 바인드
                //startService(intentList.get(missionType));
                bindService(intentList.get(missionType),mConn.get(missionType), BIND_AUTO_CREATE);
                connectionStatus.set(missionType, true);
            }
            else if(!containMission(missionType) && connectionStatus.get(missionType))
            {
                //만약 진행중인 해당 미션타입의 미션이 없고, 해당 서비스의 connectionStatus가 true라면 서비스 언바인드
                //stopService(intentList.get(missionType));
                unbindService(mConn.get(missionType));
                connectionStatus.set(missionType, false);
            }
        }
    }

    //condition이 0인 미션중 해당 미션타입의 미션이 존재하면 true
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

    public void addMission(Mission _mission) {
        //DB와 메모리에 초기 데이터 저장
        missionList.add(_mission);
        //dbInsert(_mission);
    }

    class MissionServiceConnection implements ServiceConnection{
        public IBinder m_service = null;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { m_service = service;}
        @Override
        public void onServiceDisconnected(ComponentName name) { m_service = null; }
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
        super.onDestroy();

        isManagerRunning=false;
        SystemClock.sleep(150);
        removeServiceOnForeground();

        //DB 에 최종 데이터 저장



//        for(Mission mission : missionList)
//        {
//            //dbUpdate(mission);
//        }
//        missionDB.close();
//        dbManager.close();
    }


//    //DB관련 메서드들
//    void dbInsert(Mission mission) {
//        ContentValues contentValues = new ContentValues();
//
//
//        int type = mission.getType();
//        JSONObject json = mission.getJSON();
//
//        contentValues.put("TYPE", type);
//        contentValues.put("JSON", JsonToStr(json));
//
//        //생성된 id는 Mission 객체에 저장
//        mission.setId((int)missionDB.insert("MISSIONS", null, contentValues));
//    }
//    void dbUpdate(Mission mission) {
//        ContentValues contentValues = new ContentValues();
//
//
//        int type = mission.getType();
//        JSONObject json = mission.getJSON();
//
//        contentValues.put("TYPE", type);
//        contentValues.put("JSON", JsonToStr(json));
//
//        String[] id = {String.valueOf(mission.getMissionID())};
//        //생성된 id는 Mission 객체에 저장
//        missionDB.update("MISSIONS", contentValues, "id=?", id);
//    }
//    void dbSelect()
//    {
//        Cursor c = missionDB.query("MISSIONS", null, null, null, null, null, null);
//        while(c.moveToNext()) {
//            Mission selectMission = null;
//            int type = c.getInt(c.getColumnIndex("TYPE"));
//            JSONObject jsonData;
//            switch(type)
//            {
//                case Mission.MISSION_TYPE_ALARM:
//                    break;
//                case Mission.MISSION_TYPE_USEAGE_DEVICE:
//                    break;
//                case Mission.MISSION_TYPE_USEAGE_APP:
//                    jsonData = strToJson(c.getString(c.getColumnIndex("JSON")));
//                    selectMission = new AppUseTimeMission();
//                    selectMission.setJSON(jsonData);
//                    break;
//                case Mission.MISSION_TYPE_WALK_DISTANCE:
//                    break;
//                case Mission.MISSION_TYPE_WALK_STEPCOUNT:
//                    break;
//            }
//            if(selectMission != null)
//            {
//                missionList.add(selectMission);
//            }
//
//        }
//
//    }
//
//    JSONObject strToJson(String jsonStr)
//    {
//        JsonParser parser = new JsonParser();
//        Object obj = parser.parse( jsonStr );
//
//        return (JSONObject) obj;
//    }
//
//    String JsonToStr(JSONObject json)
//    {
//        return json.toString().replace("{", "").replace("}", "");
//    }
}