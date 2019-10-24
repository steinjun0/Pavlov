package kr.osam.pavlov.Services;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.osam.pavlov.Missons.Mission;

/**************************************************

 **************************************************/

public class StepCounterService extends Service {
    public StepCounterService() {
    }

    StepCounterBinder stepCounterBinder = new StepCounterBinder();

    public class StepCounterBinder extends Binder {
        public StepCounterService getService() { return StepCounterService.this; }
    }

    private int num;

    SensorManager sensorManager;
    Sensor stepSensor;
    StepSensorListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        setServiceOnForeGround();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        listener = new StepSensorListener();
        sensorManager.registerListener(listener,stepSensor,SensorManager.SENSOR_DELAY_UI);
        return stepCounterBinder;
    }

    @Override
    public void onDestroy() {

        super.onDestroy();
        sensorManager.unregisterListener(listener);
        removeServiceOnForeground();
    }

    class StepSensorListener implements SensorEventListener
    {
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }

        @Override
        public void onSensorChanged(SensorEvent event) {
            if( event.sensor.getType() == Sensor.TYPE_STEP_COUNTER )
            {
                num = (int)event.values[0];
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

            // 현재 노티피케이션 메시즈를 포그라운드 서비스의 메시지로 등록한다.
            startForeground(Mission.MISSION_TYPE_WALK_STEPCOUNT, notification);
        }
    }
    private void removeServiceOnForeground()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(STOP_FOREGROUND_REMOVE);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Mission.MISSION_TYPE_WALK_STEPCOUNT);
        }
    }

    public int getSteps() { return num; }
}


