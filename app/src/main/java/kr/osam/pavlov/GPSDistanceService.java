package kr.osam.pavlov;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
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

    private double distance;
    private Location loc_now;
    private Location loc_prev;

    LocationManager locman;
    getLocationListener locationListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.

        distance = 0;
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
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
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

            loc_prev = loc_now;
            loc_now = location;

            distance = loc_now.distanceTo(loc_prev);
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
            NotificationChannel channel = new NotificationChannel("Pavlov", "Pavlov", NotificationManager.IMPORTANCE_HIGH);
            channel.enableLights(true);
            channel.setLightColor(Color.RED);
            channel.enableVibration(true);
            manager.createNotificationChannel(channel);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "Pavlov");
            builder.setSmallIcon(android.R.drawable.ic_menu_search);
            builder.setContentTitle("Pavlov가 당신을 보고있습니다.");
            builder.setContentText("Pavlov가 당신이 걸은 거리를 측정하고 있습니다.");
            builder.setAutoCancel(true);
            Notification notification = builder.build();
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
    public double getDistance() { return distance; }
}
