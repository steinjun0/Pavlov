package kr.osam.pavlov;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.TimePickerDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.osam.pavlov.receiver.AlarmReceiver;
import kr.osam.pavlov.receiver.ScreenOffReceiver;
import kr.osam.pavlov.receiver.ScreenOnReceiver;

import static kr.osam.pavlov.Missons.Mission.MISSION_FAILED;
import static kr.osam.pavlov.Missons.Mission.MISSION_ON_PROGRESS;
import static kr.osam.pavlov.Missons.Mission.MISSION_READY;
import static kr.osam.pavlov.Missons.Mission.MISSION_SUCCES;
import static kr.osam.pavlov.Missons.Mission.MISSION_TYPE_USEAGE_DEVICE;

public class TimeCheckerActivity extends AppCompatActivity {

    String titleTCA = "TimeChecker";
    int serviceIDTCA = 1;
    int isWorkingTCA = MISSION_READY;
    TimeCheckerJSON dataTCA;

    final static int THREAD_FINISH_TIME=0;
    final static int THREAD_LEFT_TIME = 1;

    TextView textLeftTime;
    TextView textFinishTime;
    Button buttonSetAlarm;
    Button buttonSetFinish;

    AlarmManager mAlarmManager;

    Intent receiverServiceIntent;
    ScreenReceiver screenReceiver;

    CheckingTimeThread timeThread = null;
    WritingTimeHandler writingTimeHandler = null;

    ScreenReceiverService receiverBinderService = new ScreenReceiverService();
    TimeCheckerService timeCheckerService = new TimeCheckerService();
    Intent TCSIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_checker);

        dataTCA = new TimeCheckerJSON();

        textLeftTime = (TextView) findViewById(R.id.textLeftTime);
        textFinishTime = (TextView) findViewById(R.id.textFinishTime);
        buttonSetAlarm = (Button) findViewById(R.id.buttonSetAlarm);
        buttonSetFinish = (Button) findViewById(R.id.buttonSetFinish);


        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ScreenOffReceiver.SCREEN_OFF_NOTIFICATION);
        filter.addAction(ScreenOnReceiver.SCREEN_ON_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_FAIL_NOTIFICATION);
        filter.addAction(AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION);
        this.registerReceiver(screenReceiver, filter);
        screenReceiver.isRegistered = 1;
        Log.d("test", "registerReceiver");


        receiverServiceIntent = new Intent(this, ScreenReceiverService.class);
        ScreenReceiverService screenReceiverService = new ScreenReceiverService();
        if(Build.VERSION.SDK_INT>=26) {
            startForegroundService(receiverServiceIntent);
        }else{
            startService(receiverServiceIntent);
        }
        writingTimeHandler = new WritingTimeHandler();

        Intent intent = new Intent(this, ScreenReceiverService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d("test", "onResume");
        IntentFilter filter = new IntentFilter();
        if(screenReceiver.isRegistered==0) {
            filter.addAction(ScreenOffReceiver.SCREEN_OFF_NOTIFICATION);
            filter.addAction(ScreenOnReceiver.SCREEN_ON_NOTIFICATION);
            filter.addAction(AlarmReceiver.MISSTION_FAIL_NOTIFICATION);
            filter.addAction(AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION);
            this.registerReceiver(screenReceiver, filter);
            Log.d("test", "registerReceiver");
            screenReceiver.isRegistered = 1;
        }

        Intent intent = new Intent(this, ScreenReceiverService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);


        dataTCA = receiverBinderService.getData();

        Log.d("test", "bindService");
        unbindService(conn);
        Log.d("test", "unbindService");
        Calendar temp = Calendar.getInstance();
        temp.setTimeInMillis(dataTCA.setTime);

/*        timeThread = new CheckingTimeThread(temp);
        if(timeThread != null) {
            Log.d("test","onResume state: "+timeThread.getState());
            if (timeThread.getState() == Thread.State.RUNNABLE || timeThread.getState() == Thread.State.TIMED_WAITING || timeThread.getState() == Thread.State.WAITING || timeThread.getState() == Thread.State.TERMINATED) {
                Calendar temp = Calendar.getInstance();
                temp.setTimeInMillis(dataTCA.setTime);
                timeThread = new CheckingTimeThread(temp);
                timeThread.start();
            }
        }*/
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("test1", "onPause");
        Intent intent = new Intent(this, ScreenReceiverService.class);
        bindService(intent, conn, Context.BIND_AUTO_CREATE);
        Log.d("test1", "bindService");
        Log.d("test1", "setTime" + dataTCA.setTime);
        receiverBinderService.setData(dataTCA);
        unbindService(conn);
        Log.d("test", "unbindServie");
        if(screenReceiver.isRegistered==1) {
            this.unregisterReceiver(screenReceiver);
            screenReceiver.isRegistered = 0;
            Log.d("test","unregisterReceiver");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(timeThread != null) {
            //if (timeThread.getState() == Thread.State.RUNNABLE || timeThread.getState() == Thread.State.TIMED_WAITING || timeThread.getState() == Thread.State.WAITING || timeThread.getState() == Thread.State.TERMINATED) {
            timeThread.interrupt();
            //}
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onClickSetAlarm(View view){
        ////알람 기능구현 시작////
        //알람을 위한 시간 설정
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {

            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                //입력받은 시간 확인용 Toast 생성
                String msg = String.format("%d 시 %d 분", hour, min);
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

                //현재시간(now)과 설정시간(cal)을 선언
                //cal은 now값에 입력값을 더한것으로 초기화
                Calendar now = Calendar.getInstance();
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, now.get(Calendar.HOUR_OF_DAY) + hour);
                cal.set(Calendar.MINUTE, now.get(Calendar.MINUTE) + min);
                Log.d("test1","onClickSetAlarm1: "+dataTCA.setTime);
                dataTCA.setTime = cal.getTimeInMillis();
                Log.d("test1","onClickSetAlarm2: "+dataTCA.setTime);
                Log.d("test1", "set Alarm dataTCA.setTime: " + dataTCA.setTime);

                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                mAlarmIntent.setAction("com.example.mission1.receiver.ALARM_ON");
                mAlarmIntent.putExtra("flag","fail");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                0,
                                mAlarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );

                //알람을 위한 AlarmManager 선언 및 설정
                //mAlarmManager는 MainAcitivty에 선언(다른 메소드에서도 같은 매니저를 쓰기위해. 근데 상관없는듯)
                //설정시간(cal)로 실패 알람 설정
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(),mPendingIntent);
                ////알람 기능구현 종료////
                //설정시간을 preference에 저장
                //왜냐하면, AlarmManager는 알람정보를 받기만하고 출력할수 없기 때문에 어딘가에 직접저장해야한다.
                SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("setTime",dataTCA.setTime);
                editor.apply();
                try {
                    if (timeThread.getState() == Thread.State.RUNNABLE || timeThread.getState() == Thread.State.TIMED_WAITING || timeThread.getState() == Thread.State.WAITING || timeThread.getState() == Thread.State.TERMINATED) {
                        Log.d("test", "Runnable");
                        timeThread.interrupt();
                        Calendar temp = Calendar.getInstance();
                        temp.setTimeInMillis(dataTCA.setTime);
                        timeThread = new CheckingTimeThread(temp);
                        timeThread.start();
                    }
                    Log.d("test", "timeThread = not null " + timeThread.getState());
                }catch(Exception e){
                    Calendar temp = Calendar.getInstance();
                    temp.setTimeInMillis(dataTCA.setTime);
                    timeThread = new CheckingTimeThread(temp);
                    timeThread.start();
                    Log.d("test", "timeThread = null");
                }

                Calendar temp = Calendar.getInstance();
                dataTCA.startTime = temp.getTimeInMillis();
                dataTCA.isWorking = MISSION_ON_PROGRESS;

                receiverBinderService.setData(dataTCA);

            }
        },
                0,
                0,
                true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

        dialog.show();
    }


    public void onClickSetFinish(View view){
        ////알람 기능구현 시작////
        //알람을 위한 시간 설정
        TimePickerDialog dialog = new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int hour, int min) {
                //입력받은 시간 확인용 Toast 생성
                String tst = String.format("%d 시 %d 분", hour, min);
                Toast.makeText(getApplicationContext(), tst, Toast.LENGTH_SHORT).show();
                //입력받은 시간 저장용 Calendar 선언 및 초기화
                Calendar cal = Calendar.getInstance();
                Calendar now = Calendar.getInstance();
                cal.set(Calendar.HOUR_OF_DAY, hour);
                cal.set(Calendar.MINUTE, min);
                if(cal.compareTo(now) == -1){
                    cal.set(Calendar.DATE, now.get(Calendar.DATE) + 1);
                }
                SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/HH:mm:ss");

                SharedPreferences finishPref = getSharedPreferences("preference", MODE_PRIVATE);
                SharedPreferences.Editor editor = finishPref.edit();
                editor.putLong("finishTime",cal.getTimeInMillis());
                editor.commit();

                Log.d("test_time_of_finish", sdf.format(cal.getTime()));

                //알람을 위한 Intent, PendingIntent 선언
                Intent mAlarmIntent = new Intent(getApplicationContext(), AlarmReceiver.class);
                mAlarmIntent.setAction("com.example.mission1.receiver.ALARM_ON");
                mAlarmIntent.putExtra("flag","success");
                PendingIntent mPendingIntent =
                        PendingIntent.getBroadcast(
                                getApplicationContext(),
                                1,
                                mAlarmIntent,
                                PendingIntent.FLAG_UPDATE_CURRENT
                        );
                //알람을 위한 AlarmManager 선언 및 설정
                //mAlarmManager는 MainAcitivty에 선언
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), mPendingIntent);
                dataTCA.finishTime = cal.getTimeInMillis();
                ////알람 기능구현 종료////

                Message msg = setMsg("finish",cal);

                msg.what = TimeCheckerActivity.THREAD_FINISH_TIME;
                writingTimeHandler.sendMessage(msg);

            }
        },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

        dialog.show();
    }


    public class ScreenReceiver extends BroadcastReceiver {
        long time = 0;
        long presentOnTime = 0;
        long presentOffTime = 0;
        long durationOff = 0;
        long durationOn = 0;
        int isRegistered = 0;
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d("test", "Custom Broadcast OnReceive");
            //화면 켜짐/꺼짐, 미션 성공/실패 분기
            if (ScreenOnReceiver.SCREEN_ON_NOTIFICATION.equals(intent.getAction())) {
                if(timeThread != null) {
                    Log.d("test","onScreenOn state: "+timeThread.getState());
                    if (timeThread.getState() == Thread.State.RUNNABLE || timeThread.getState() == Thread.State.TIMED_WAITING || timeThread.getState() == Thread.State.WAITING || timeThread.getState() == Thread.State.TERMINATED) {
                        Calendar temp = Calendar.getInstance();
                        temp.setTimeInMillis(dataTCA.setTime);
                        Log.d("test1", "Screen On Noti dataTCA.setTime: " + dataTCA.setTime);
                        timeThread = new CheckingTimeThread(temp);
                        timeThread.start();
                    }
                }

            } else if (ScreenOffReceiver.SCREEN_OFF_NOTIFICATION.equals(intent.getAction())) {
                if(timeThread != null) {
                    timeThread.interrupt();
                }
            }
            else if (AlarmReceiver.MISSTION_FAIL_NOTIFICATION.equals(intent.getAction())){
                textLeftTime.setText("00:00:00");
                timeThread.interrupt();

                dataTCA.isWorking = MISSION_FAILED;

                bindService(TCSIntent, TimeCheckerConn, Context.BIND_AUTO_CREATE);
                Calendar temp = Calendar.getInstance();
                timeCheckerService.setData((int)dataTCA.finishTime, (int)(dataTCA.setTime-temp.getTimeInMillis()),MISSION_TYPE_USEAGE_DEVICE, MISSION_FAILED);

                unbindService(TimeCheckerConn);
            }
            else if (AlarmReceiver.MISSTION_SUCCESS_NOTIFICATION.equals(intent.getAction())){
                timeThread.interrupt();

                dataTCA.isWorking = MISSION_SUCCES;

                bindService(TCSIntent, TimeCheckerConn, Context.BIND_AUTO_CREATE);
                Calendar temp = Calendar.getInstance();
                timeCheckerService.setData((int)dataTCA.finishTime, (int)(dataTCA.setTime-temp.getTimeInMillis()),MISSION_TYPE_USEAGE_DEVICE, MISSION_SUCCES);
                unbindService(TimeCheckerConn);
            }
        }
    }

    class CheckingTimeThread extends Thread{
        //Handler handler;
        public Calendar cal;
        CheckingTimeThread(Calendar cal){
            //this.handler = handler;
            this.cal = cal;
        }

        @Override
        public void run() {
            super.run();
            TCSIntent = new Intent(getApplicationContext(), TimeCheckerService.class);

            while(cal.compareTo(Calendar.getInstance()) >= 0 ) {
                bindService(TCSIntent, TimeCheckerConn, Context.BIND_AUTO_CREATE);
                Log.d("test", "after binding");
                timeCheckerService.printData();
                Calendar temp = Calendar.getInstance();
                timeCheckerService.setData((int)dataTCA.finishTime, (int)(dataTCA.setTime-temp.getTimeInMillis()),MISSION_TYPE_USEAGE_DEVICE, MISSION_ON_PROGRESS);
                Log.d("test", "after setData");
                timeCheckerService.printData();
                Log.d("test","Activity Thread");
                unbindService(TimeCheckerConn);
                Log.d("test", "after unbinding");
                timeCheckerService.printData();

                Log.d("test", "Thread Running");
                Bundle bundle = new Bundle();
                bundle.putSerializable("set",cal);
                Message msg = new Message();
                msg.setData(bundle);
                msg.what=TimeCheckerActivity.THREAD_LEFT_TIME;

                try {
                    Log.d("test", "sendMessage");
                    writingTimeHandler.sendMessage(msg);
                    Log.d("test", "msg.what = " + msg.what);
                    Thread.sleep(100);
                }catch(InterruptedException e){
                    Log.d("test","Thread return");
                    break;
                }
            }
        }
        public void setCal(Calendar cal){
            this.cal = cal;
        }
    }

    class WritingTimeHandler extends Handler {
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            Log.d("test","Handler Receive");
            switch (msg.what) {
                case TimeCheckerActivity.THREAD_FINISH_TIME:
                    Log.d("test","Handle Message FINISH_TIME");
                    Bundle finishBundle;
                    finishBundle = msg.getData();
                    Calendar finisthCal = (Calendar) finishBundle.getSerializable("finish");
                    SimpleDateFormat finishSdf = new SimpleDateFormat("MM/dd/kk:mm");
                    textFinishTime.setText(finishSdf.format(finisthCal.getTime()));
                    break;
                case TimeCheckerActivity.THREAD_LEFT_TIME:
                    Log.d("test","Handle Message LEFT_TIME");
                    Bundle setBundle;
                    setBundle = msg.getData();
                    Calendar setCal = (Calendar) setBundle.getSerializable("set");
                    Calendar now = Calendar.getInstance();
                    long diff = setCal.getTimeInMillis() - now.getTimeInMillis();
                    int sec = (int)(diff/1000)%60;
                    int min = (int)((diff/1000)/60)%60;
                    int hr = (int)(((diff/1000)/60)/60)%60;
                    Log.d("test","time: "+hr+":"+min+":"+sec);
                    textLeftTime.setText(hr+":"+min+":"+sec);
                    break;
            }
        }
    }


    ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("test", "serviceConnected");
            ScreenReceiverService.LocalBinder localBinder = (ScreenReceiverService.LocalBinder) service;
            receiverBinderService = localBinder.getService();
            try {
                if (receiverBinderService.getData().setTime != 0) {
                    dataTCA = receiverBinderService.getData();
                    Log.d("test1", "onBind: " + dataTCA);
                    Log.d("test", "bindService");
                    unbindService(conn);
                    Log.d("test", "unbindService");
                    Calendar temp = Calendar.getInstance();
                    temp.setTimeInMillis(dataTCA.setTime);
                    timeThread = new CheckingTimeThread(temp);
                    timeThread.start();
                }
            }catch(NullPointerException e){
                Log.d("test", "onServiceConnected: receiverBinderService = null");}
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("test", "serviceDisconnected");
        }
    };
    ServiceConnection TimeCheckerConn = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            TimeCheckerService.LocalBinder localBinder = (TimeCheckerService.LocalBinder) service;
            timeCheckerService = localBinder.getService();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d("test", "serviceDisconnected");
        }
    };


    public void stopThread(CheckingTimeThread thread){
        if(thread == null) {
            Log.d("test", "thread already null");
        }else{
            thread.interrupt();
            thread = null;

            Log.d("test","thread interrupt");
        }
    }

    public Message setMsg( String stringHash, Serializable obj){
        Bundle bundle = new Bundle();
        bundle.putSerializable("finish",obj);
        Message msg = new Message();
        msg.setData(bundle);
        return msg;
    }

    class TransferJSONService extends Service{
        String title = "TimeChecker";
        int serviceID = 1;
        int isWorking = dataTCA.isWorking;
        TimeCheckerJSON data = dataTCA;

        IBinder binder = new LocalBinder();
        @Nullable
        @Override
        public IBinder onBind(Intent intent) {
            return binder;
        }

        class LocalBinder extends Binder {

            public TransferJSONService getService() {
                return TransferJSONService.this; //서비스 객체 자기자신을 반환하는 형태
            }
        }

        @Override
        public void onCreate() {
            super.onCreate();
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
        }


    }

}


