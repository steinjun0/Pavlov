package kr.osam.pavlov;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.osam.pavlov.receiver.AlarmReceiver;
import kr.osam.pavlov.receiver.ScreenOffReceiver;
import kr.osam.pavlov.receiver.ScreenOnReceiver;

public class ScreenReceiverService extends Service {

    BroadcastReceiver screenOnReceiver = new ScreenOnReceiver();
    BroadcastReceiver screenOffReceiver = new ScreenOffReceiver();
    ScreenReceiver screenReceiver;

    IBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();


        IntentFilter onFilter = new IntentFilter();
        IntentFilter offFilter = new IntentFilter();
        onFilter.addAction(Intent.ACTION_SCREEN_ON);
        offFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnReceiver, onFilter);
        registerReceiver(screenOffReceiver, offFilter);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("알림 제목");
        builder.setContentText("알람 세부 텍스트");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }

// id값은
// 정의해야하는 각 알림의 고유한 int값
       //notificationManager.notify(1, builder.build());
        startForeground(1,builder.build());

        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScreenOffReceiver.SCREEN_OFF_NOTIFICATION);
        filter.addAction(ScreenOnReceiver.SCREEN_ON_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_FAIL_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION);
        this.registerReceiver(screenReceiver, filter);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    class LocalBinder extends Binder {

        public ScreenReceiverService getService() {
            return ScreenReceiverService.this; //서비스 객체 자기자신을 반환하는 형태
        }
    }

    public class ScreenReceiver extends BroadcastReceiver {
        long time = 0;
        long presentOnTime = 0;
        long presentOffTime = 0;
        long durationOff = 0;
        long durationOn = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("test", "Custom Broadcast OnReceive");
            //화면 켜짐/꺼짐, 미션 성공/실패 분기
            if (ScreenOnReceiver.SCREEN_ON_NOTIFICATION.equals(intent.getAction())) {
                //intent로 ScreenOn 정보 받기
                presentOnTime = intent.getLongExtra("presentTime", 0 );
                String onLatest = "On Latest" + intent.getStringExtra("present");
                String onPast = "On " + intent.getStringExtra("past");
                //화면에 띄우기
                //textScreenOn.setText(onLatest);
                // textPastScreenOn.setText(onPast);

                Log.d("test", intent.getStringExtra("name")+" onReceive");
                //저장된 사용시간 불러오기
                SimpleDateFormat sdf = new SimpleDateFormat("MM / dd / HH:mm:ss");
                SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
                if(time == 0) {
                    time = pref.getLong("setTime", 0);
                    Log.d("test_load_setTime", "setTime = " + sdf.format(time));
                }
                //화면 꺼져 있던 시간 구하기
                if(presentOffTime != 0){
                    durationOff = presentOnTime - presentOffTime;
                }
                Log.d("test_duration_Off", sdf.format(durationOff));


            } else if (ScreenOffReceiver.SCREEN_OFF_NOTIFICATION.equals(intent.getAction())) {
                //intent로 ScreenOff 정보 받기
                presentOffTime = intent.getLongExtra("presentTime", 0 );
                String offLatest = "Off Latest "+ intent.getStringExtra("present");
                String offPast = "Off Past " + intent.getStringExtra("past");
                Log.d("test", intent.getStringExtra("name")+" offReceive");
                //화면 켜져있던 시간 구하기
                durationOn = presentOffTime - presentOnTime;

                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent("com.example.mission1.receiver.ALARM_ON");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                0,
                                mAlarmIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
                //알람을 위한 AlarmManager 선언 및 설정
                //화면이 꺼졌으므로 미션 실패 알람을 취소(꺼져있을 때는 미션을 실패할 수 없다)

            }
            else if (AlarmReceiver.MISSTION_FAIL_NOTIFICATION.equals(intent.getAction())){
                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent("com.example.mission1.receiver.ALARM_ON");
                mAlarmIntent.putExtra("flag","fail");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                1,
                                mAlarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                //알람을 위한 AlarmManager 선언 및 설정
                //성공 알람 취소

            }
            else if (AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION.equals(intent.getAction())){
                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent("com.example.mission1.receiver.ALARM_ON");
                mAlarmIntent.putExtra("flag","fail");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                0,
                                mAlarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                //알람을 위한 AlarmManager 선언 및 설정
                //실패 알람 취소
            }
        }

        public long getTime(){
            return time;
        }
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(screenOnReceiver);
        this.unregisterReceiver(screenOffReceiver);
    }
}
