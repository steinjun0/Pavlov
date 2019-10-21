package kr.osam.pavlov;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;

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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.unregisterReceiver(screenOnReceiver);
        this.unregisterReceiver(screenOffReceiver);
    }
}
