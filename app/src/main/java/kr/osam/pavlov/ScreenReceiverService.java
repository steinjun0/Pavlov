package kr.osam.pavlov;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import kr.osam.pavlov.receiver.ScreenOffReceiver;
import kr.osam.pavlov.receiver.ScreenOnReceiver;

public class ScreenReceiverService extends Service {

    BroadcastReceiver screenOnReceiver = new ScreenOnReceiver();
    BroadcastReceiver screenOffReceiver = new ScreenOffReceiver();

    public ScreenReceiverService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
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


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(screenOnReceiver);
        this.unregisterReceiver(screenOffReceiver);
    }
}
