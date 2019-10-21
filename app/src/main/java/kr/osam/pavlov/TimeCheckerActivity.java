package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import kr.osam.pavlov.receiver.AlarmReceiver;
import kr.osam.pavlov.receiver.ScreenOffReceiver;
import kr.osam.pavlov.receiver.ScreenOnReceiver;

public class TimeCheckerActivity extends AppCompatActivity {

    TextView textLeftTime;
    TextView textFinishTime;
    Button buttonSetAlarm;
    Button buttonSetFinish;

    AlarmManager mAlarmManager;

    Intent receiverServiceIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_checker);

        textLeftTime = (TextView) findViewById(R.id.textLeftTime);
        textFinishTime = (TextView) findViewById(R.id.textFinishTime);
        buttonSetAlarm = (Button) findViewById(R.id.buttonSetAlarm);
        buttonSetFinish = (Button) findViewById(R.id.buttonSetFinish);

        receiverServiceIntent = new Intent(this, ScreenReceiverService.class);
        startService(receiverServiceIntent);
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
                SimpleDateFormat sdf = new SimpleDateFormat("MM / dd / HH:mm:ss");

                Log.d("test_time_of_now", sdf.format(now.getTime()));
                Log.d("test_time_of_set", sdf.format(cal.getTime()));

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
                mAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        mPendingIntent
                );
                ////알람 기능구현 종료////
                //설정시간을 preference에 저장
                //왜냐하면, AlarmManager는 알람정보를 받기만하고 출력할수 없기 때문에 어딘가에 직접저장해야한다.
                SharedPreferences pref = getSharedPreferences("preference", MODE_PRIVATE);
                SharedPreferences.Editor editor = pref.edit();
                editor.putLong("setTime",cal.getTimeInMillis());
                editor.apply();
                Log.d("test_preferences","setTimeSetting + " + cal.getTimeInMillis());
                Log.d("test_preferences", "바로 확인"+pref.getLong("setTime", 123));

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
                SimpleDateFormat sdf = new SimpleDateFormat("MM / dd / HH:mm:ss");

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
                //requestCode:1로 성공 알람 설정
                mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
                mAlarmManager.set(
                        AlarmManager.RTC_WAKEUP,
                        cal.getTimeInMillis(),
                        mPendingIntent
                );
                ////알람 기능구현 종료////

                Bundle bundle = new Bundle();
                bundle.putSerializable("finish",cal);
                Message msg = new Message();
                msg.setData(bundle);
                msg.what = 0;

            }
        },
                Calendar.getInstance().get(Calendar.HOUR_OF_DAY),
                Calendar.getInstance().get(Calendar.MINUTE),
                true);  //마지막 boolean 값은 시간을 24시간으로 보일지 아닐지

        dialog.show();
    }

}
