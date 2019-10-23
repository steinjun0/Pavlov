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

import java.util.Calendar;

import kr.osam.pavlov.receiver.AlarmReceiver;
import kr.osam.pavlov.receiver.ScreenOffReceiver;
import kr.osam.pavlov.receiver.ScreenOnReceiver;

public class ScreenReceiverService extends Service {

    TimeCheckerJSON dataInService = new TimeCheckerJSON();

    BroadcastReceiver screenOnReceiver = new ScreenOnReceiver();
    BroadcastReceiver screenOffReceiver = new ScreenOffReceiver();

    public ScreenReceiverService() {
    }

    IBinder ScreenBinder = new LocalBinder();

    class LocalBinder extends Binder {
        ScreenReceiverService getService() { // 서비스 객체를 리턴
            return ScreenReceiverService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return ScreenBinder;
    }

    public void setData(TimeCheckerJSON json){
        this.dataInService = json;
        Log.d("test1","set Json: setTime: "+dataInService.setTime);
    }
    public TimeCheckerJSON getData(){
        Log.d("test1","get Json: setTime: "+dataInService.setTime);
        return this.dataInService;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        ScreenReceiver screenReceiver = new ScreenReceiver();

        IntentFilter onFilter = new IntentFilter();
        IntentFilter offFilter = new IntentFilter();
        onFilter.addAction(Intent.ACTION_SCREEN_ON);
        offFilter.addAction(Intent.ACTION_SCREEN_OFF);
        registerReceiver(screenOnReceiver, onFilter);
        registerReceiver(screenOffReceiver, offFilter);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "default");

        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setContentTitle("Pavlov");
        builder.setContentText("핸드폰 사용량 측정중");

        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationManager.createNotificationChannel(new NotificationChannel("default", "기본 채널", NotificationManager.IMPORTANCE_DEFAULT));
        }else builder.setSmallIcon(R.mipmap.ic_launcher);

// id값은
// 정의해야하는 각 알림의 고유한 int값
       //notificationManager.notify(1, builder.build());
        startForeground(1,builder.build());

        Log.d("test", "onResume");
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScreenOffReceiver.SCREEN_OFF_NOTIFICATION);
        filter.addAction(ScreenOnReceiver.SCREEN_ON_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_FAIL_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION);
        this.registerReceiver(screenReceiver, filter);
        Log.d("test", "registerReceiver");
    }

    public class ScreenReceiver extends BroadcastReceiver {
        long time = 0;
        long presentOnTime = 0;
        long presentOffTime = 0;
        long durationOff = 0;
        long durationOn = 0;
        AlarmManager mAlarmManager;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("test", "Custom Broadcast OnReceive");
            //화면 켜짐/꺼짐, 미션 성공/실패 분기
            if (ScreenOnReceiver.SCREEN_ON_NOTIFICATION.equals(intent.getAction())) {
                //intent로 ScreenOn 정보 받기
                presentOnTime = intent.getLongExtra("presentTime", 0 );

                //저장된 사용시간 불러오기
                SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
                if(time == 0) {
                    time = pref.getLong("setTime", 0);
                }
                //화면 꺼져 있던 시간 구하기
                if(presentOffTime != 0){
                    durationOff = presentOnTime - presentOffTime;
                }
                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent("com.example.mission1.receiver.ALARM_ON");
                mAlarmIntent.putExtra("flag","fail");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                0,
                                mAlarmIntent,
                                PendingIntent.FLAG_CANCEL_CURRENT
                        );
                //알람을 위한 AlarmManager 선언 및 설정
                //기존 세팅시간 + 화면꺼진시간으로 알람시간 지연시켜서 재등록
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        time + durationOff,
                        mPendingIntent
                );
                //세팅시간 변경(누적을 위해)
                time = time + durationOff;

                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(time);
                dataInService.setTime = time;

            } else if (ScreenOffReceiver.SCREEN_OFF_NOTIFICATION.equals(intent.getAction())) {
                //intent로 ScreenOff 정보 받기
                presentOffTime = intent.getLongExtra("presentTime", 0 );
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
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.cancel(mPendingIntent);
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
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.cancel(mPendingIntent);
                dataInService.totalUsingTime = dataInService.setTime;
                dataInService.isWorking = 3;
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
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.cancel(mPendingIntent);

                dataInService.isWorking = 1;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(screenOnReceiver);
        this.unregisterReceiver(screenOffReceiver);
    }
}
