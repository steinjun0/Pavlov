package kr.osam.pavlov.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.osam.pavlov.Missons.GpsCountMission;
import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.Missons.StepCountMisson;

public class MissionManager extends Service {

    public List<Mission> missionList = new ArrayList<>();

    private Location now_Location;
    private long steps;

    MissionManagerBinder binder;

    LocationManager locationManager;

    SensorManager sensorManager;
    Sensor stepSensor;
    StepSensorListener stepSensorlistener;
    GetLocationListener locationListener;

    public MissionManager() {}

    @Override
    public void onCreate() {

        //missionList.addAll(dbManager.readMission());

        locationManager =  (LocationManager)getSystemService(Context.LOCATION_SERVICE);

        Calendar tmp =  Calendar.getInstance();
        tmp.set(2019,9,24,14,00);

        missionList.add(new StepCountMisson("집에",0, 50, 0, tmp));
        missionList.add(new GpsCountMission("가고",0, 30, 0, tmp, new ArrayList<Location> ()));
        missionList.add(new StepCountMisson("싶다",0, 20000, 0, tmp));

        Log.d("test", "Service on!");

        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        setServiceOnForeGround();

        locationListener = new GetLocationListener();
        stepSensorlistener = new StepSensorListener();

        sensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        stepSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER);
        sensorManager.registerListener( stepSensorlistener ,stepSensor,SensorManager.SENSOR_DELAY_UI );
        registerLocationUpdates();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        locationManager.removeUpdates(locationListener);
        sensorManager.unregisterListener(stepSensorlistener);

        Log.d("test", "Service Down");

        removeServiceOnForeground();
        super.onDestroy();
    }

    public void addMission(Mission _mission) { missionList.add(_mission); }

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

    @Nullable @Override public IBinder onBind(Intent intent) {
        binder = new MissionManagerBinder();
        Log.d("test", "Service Bound!");
        return binder;
    }

    class GetLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {

            now_Location = location;

            for(Mission mission : missionList)
            {
                if(mission.getType() == Mission.MISSION_TYPE_WALK_DISTANCE)
                {
                    Intent intent = LocationEvent();
                    mission.upDate(intent);
                }
            }
        }
        @Override public void onProviderDisabled(String s) { }
        @Override public void onProviderEnabled(String s) { }
        @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
    }
    class StepSensorListener implements SensorEventListener
    {
        @Override public void onAccuracyChanged(Sensor sensor, int accuracy) { }
        @Override
        public void onSensorChanged(SensorEvent event) {
            if( event.sensor.getType() == Sensor.TYPE_STEP_COUNTER )
            {
                steps = (int)event.values[0];

                for(Mission mission : missionList)
                {
                    if(mission.getType() == Mission.MISSION_TYPE_WALK_DISTANCE)
                    {
                        Intent intent = stepEvent();
                        mission.upDate(intent);
                    }
                }
            }
        }
    }

    public class MissionManagerBinder extends Binder
    {
        public MissionManager getService() { return MissionManager.this; }
    }

    private void registerLocationUpdates() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if(locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)
                {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, .01f, locationListener);
                }
                if(locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)
                {
                    locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, .01f, locationListener);
                }
            }
        }
    }

    Intent stepEvent()
    {
        Intent intent = new Intent("Event");
        Log.d("test", "stepEvent!");
        intent.putExtra("Steps", steps);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        return intent;
    }
    Intent LocationEvent()
    {
        Intent intent = new Intent("Event");
        Log.d("test", "locationEvent!");
        intent.putExtra("latitude", now_Location.getLongitude());
        intent.putExtra("longitude", now_Location.getLatitude());
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        return intent;
    }
}
