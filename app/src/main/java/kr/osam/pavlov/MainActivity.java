package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

import kr.osam.pavlov.Missons.Mission;
import kr.osam.pavlov.Services.MissionManager;

public class MainActivity extends AppCompatActivity {

    ListView missionListView;
    Intent intent;
    boolean isRunning;
    masterConn conn;
    MissionListAdapter adapter;
    UpdaterThread thread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        missionListView = findViewById(R.id.MissionListView);

        intent = new Intent(this, MissionManager.class);
        conn = new masterConn();
        isRunning = true;
        startService(intent);


        adapter = new MissionListAdapter();
        missionListView.setAdapter(adapter);

        thread = new UpdaterThread();
        thread.start();

        Intent addMissionIntent = new Intent(MainActivity.this,AddAppUseMissionActivity.class);
        startActivity(addMissionIntent);
    }

    class UpdaterThread extends Thread
    {
        @Override
        public void run() {
            while (isRunning)
            {
                try {
                    long tmpTime = SystemClock.currentThreadTimeMillis();

                    bindService(intent, conn, BIND_ABOVE_CLIENT);

                    adapter.CopyMissionsList(((MissionManager.MissionManagerBinder)conn.m_service).getService().missionList);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //adapter.notifyDataSetChanged();
                        }
                    });

                    unbindService(conn);

                    tmpTime = SystemClock.currentThreadTimeMillis() - tmpTime;
                    sleep((250 - tmpTime)>0 ? (250 - tmpTime) : 0);
                } catch (Exception e) { Log.d("CatchExeption", e.toString()); }
            }

            super.run();
        }
    }

    @Override
    protected void onResume() {
        isRunning = true;
        super.onResume();
    }

    @Override
    protected void onPause() {
        // isRunning = false;
        super.onPause();
    }

    class masterConn implements ServiceConnection{
        public IBinder m_service;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) { m_service = service;}
        @Override
        public void onServiceDisconnected(ComponentName name) { m_service = null; }
    }

    @Override
    protected void onDestroy() {
        isRunning = false;
        super.onDestroy();
    }
}