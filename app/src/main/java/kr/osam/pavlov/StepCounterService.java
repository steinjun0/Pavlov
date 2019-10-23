package kr.osam.pavlov;
import android.app.Notification;
import android.app.NotificationChannel;
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
import android.os.Build;
import android.os.IBinder;
import android.util.Log;
import androidx.core.app.NotificationCompat;

/**************************************************

 **************************************************/

public class StepCounterService extends Service {
    public StepCounterService() {
    }

    private int num;

    SensorManager sensorManager;
    Sensor stepSensor;
    StepSensorListener listener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        setServiceOnForeGround();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        listener = new StepSensorListener();
        sensorManager.registerListener(listener,stepSensor,SensorManager.SENSOR_DELAY_GAME);

        Log.d("test", "StepCounter Service OnStart");
        return super.onStartCommand(intent, flags, startId);
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
            NotificationChannel channel = new NotificationChannel("Pavlov", "Pavlov", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pavlov");
            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("Pavlov가 당신을 보고있습니다.");
            builder.setContentText("Pavlov가 당신의 걸음수를 측정하고 있습니다.");
            builder.setAutoCancel(true);
            Notification notification = builder.build();
            // 현재 노티피케이션 메시즈를 포그라운드 서비스의 메시지로 등록한다.
            startForeground(10, notification);
        }
    }
    private void removeServiceOnForeground()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(STOP_FOREGROUND_REMOVE);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(10);
        }
    }

    public int getSteps() { return num; }
}


