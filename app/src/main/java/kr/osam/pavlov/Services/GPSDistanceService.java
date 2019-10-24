package kr.osam.pavlov.Services;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.ArrayList;
import java.util.List;

import kr.osam.pavlov.Missons.Mission;

/**************************************************

 **************************************************/

public class GPSDistanceService extends Service {
    public GPSDistanceService() {
    }

    private Location loc_now;

    GPSDistanceBinder gpsDistanceBinder = new GPSDistanceBinder();

    public class GPSDistanceBinder extends Binder
    {
        public GPSDistanceService getService() { return GPSDistanceService.this; }
    }

    LocationManager locman;
    getLocationListener locationListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        setServiceOnForeGround();

        locman = (LocationManager)getSystemService(LOCATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationListener = new getLocationListener();
                if(locman.isProviderEnabled(LocationManager.GPS_PROVIDER) == true)
                {
                    locman.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, .01f, locationListener);
                }
                if(locman.isProviderEnabled(LocationManager.NETWORK_PROVIDER) == true)
                {
                    locman.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, .01f, locationListener);
                }
            }
        }

        Log.d("Test", "Service started");
        return gpsDistanceBinder;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeServiceOnForeground();
        locman.removeUpdates(locationListener);
    }

    class getLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {

            loc_now = location;
        }
        @Override public void onProviderDisabled(String s) { }
        @Override public void onProviderEnabled(String s) { }
        @Override public void onStatusChanged(String s, int i, Bundle bundle) { }
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
            startForeground(Mission.MISSION_TYPE_WALK_DISTANCE, notification);
        }
    }

    private void removeServiceOnForeground()
    {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            stopForeground(STOP_FOREGROUND_REMOVE);
            NotificationManager manager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            manager.cancel(Mission.MISSION_TYPE_WALK_DISTANCE);
        }
    }

    public Location getLoctaion() { return loc_now; }
}
