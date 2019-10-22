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

/**************************************************

 서비스 생성시 putExtra를 통해
 Service_id와 목표거리 goal(meteric)을 설정합니다.
 이 두 값은 0이여서는 안됩니다.

 서비스는 주기적으로 현재위치를 파악하고,
 이를 기반으로 이동거리를 추정합니다.
 단, 이때 너무 느리거나 너무 빠른 이동은 이동거리에서 제외될 수 있습니다.

 총 이동거리가 goal보다 커져 목표를 달성하면,
 Accomplished! Intent로 BroadCast를 발송합니다.
 이 Intent에는 id와 Goal과 Distance,
 Sampletime, Latitude (+i), Longitude (+i)의 좌표정보가
 putExtra 되어있습니다.

 서비스 구동중에는 GPS-request 브로드캐스트에 반응하여
 GPS-reply를 반환합니다.
 반응시에는 현재 Distance와 목표 Goal, 좌표를 Intent 반환합니다.

 **************************************************/

public class GPSDistanceService extends Service {
    public GPSDistanceService() {
    }

    private float distance;
    private int goal;
    private int Service_id;
    private int Working;

    Location loc_now;
    List<Location> loc_list = new ArrayList<Location>(){};
    LocationManager locman;
    getLocationListener locationListener;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        distance = 0;
        setServiceOnForeGround();

        goal = intent.getIntExtra("goal", 0);
        Service_id = intent.getIntExtra("Service_id", 0);

        if(goal == 0 || Service_id == 0)
        {
            removeServiceOnForeground();
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(
                GPSRequestReceiver, new IntentFilter("GPS-Request"));

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
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        removeServiceOnForeground();
        locman.removeUpdates(locationListener);
    }

    public void getDistance()
    {
        distance = 0.f;
        if (loc_list.isEmpty())
        {
            distance = 0.f;
        }
        else
        {
            for(int i = 1; i < loc_list.size(); i++)
            {
                float tmp_distance = loc_list.get(i-1).distanceTo(loc_list.get(i));
                if(tmp_distance > 0.5 && tmp_distance < 20)
                {
                    distance += tmp_distance;
                }
            }
        }
    }

    class getLocationListener implements LocationListener
    {
        @Override
        public void onLocationChanged(Location location) {
            loc_now = location;
            if(loc_now != null) { loc_list.add(loc_now); Log.d("test", "add loc");}
            Log.d("test", "" + location.getLatitude() + " " + location.getLongitude());
            Toast.makeText(getBaseContext(), "" + location.getLatitude() + " " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            getDistance();

            if(distance >= (float)goal)
            {
                Intent intent = new Intent("Accomplished!");
                intent.putExtra("Service-id", Service_id);
                intent.putExtra("Distance", distance);
                intent.putExtra("Goal", goal);
                LocalBroadcastManager.getInstance(GPSDistanceService.this).sendBroadcast(intent);
            }
        }
        @Override
        public void onProviderDisabled(String s) { }
        @Override
        public void onProviderEnabled(String s) { }
        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) { }
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

    private BroadcastReceiver GPSRequestReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            getDistance();
            Intent reply = new Intent("GPS-Reply");
            reply.putExtra("float", distance);

            reply.putExtra("Sampletime", loc_list.size());
            for (int i = 0; i < loc_list.size(); i++)
            {
                reply.putExtra("Latitude" + i, loc_list.get(i).getLatitude());
                reply.putExtra("Longitude" + i, loc_list.get(i).getLongitude());
            }

            LocalBroadcastManager.getInstance(GPSDistanceService.this).sendBroadcast(reply);
        }
    };
}
