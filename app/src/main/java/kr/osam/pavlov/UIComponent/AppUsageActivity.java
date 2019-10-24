package kr.osam.pavlov.UIComponent;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;

import kr.osam.pavlov.R;
import kr.osam.pavlov.Services.MissionManager;

public class AppUsageActivity extends AppCompatActivity {

    MissionManager missionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_usage);

        ServiceConnection serviceConnection = new ServiceConnection()
        {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //서비스와 연결되었을 때 호출되는 메서드
                MissionManager.MissionManagerBinder missionManagerBinder = (MissionManager.MissionManagerBinder) service;
                missionManager = missionManagerBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        //서비스 시작, 바인딩
        Intent service_intent = new Intent(AppUsageActivity.this, MissionManager.class);
        bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);
    }
}
