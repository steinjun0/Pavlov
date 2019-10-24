package kr.osam.pavlov.UIComponent;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import kr.osam.pavlov.R;
import kr.osam.pavlov.Services.MissionManager;

public class MainActivity extends AppCompatActivity {

    String[] permission_list = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

    public masterConn conn;
    public Intent intent;
    EventReceiver receiver;

    ListView missionListView;
    MissionListAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new masterConn();

        intent = new Intent(this, MissionManager.class);
        startService(intent);

        missionListView = findViewById(R.id.MissionListView);

        receiver = new EventReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter("Event"));

        checkPermission();

    }

    @Override
    protected void onStart() {

        adapter = new MissionListAdapter();
        missionListView.setAdapter(adapter);

        super.onStart();
    }

    @Override
    protected void onResume() {

        bindService(intent, conn, BIND_ABOVE_CLIENT);

        super.onResume();
    }

    @Override
    protected void onPause() {

        super.onPause();
    }

    @Override
    protected void onStop() {
        unbindService(conn);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    class masterConn implements ServiceConnection{
        public IBinder m_service;
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d("test", "Binder Got!");m_service = service;}
        @Override
        public void onServiceDisconnected(ComponentName name) { m_service = null; }
    }

    @Override
    protected void onDestroy() {
        //isRunning = false;
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainacctmenu, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.AddMission:

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) { ft.remove(prev); }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = ListFragment.newInstance();
                dialogFragment.show(ft, "dialog");

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // 권한 체크 메서드
    public void checkPermission(){
        // 현재 안드로이드 버전이 6.0 미만이면 메서드를 종료한다.
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return;
        }
        // 각 권한의 허용 여부를 확인한다.
        for(String permission : permission_list){
            // 권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);
            // 거부 상태라고 한다면..
            if(chk == PackageManager.PERMISSION_DENIED){
                // 사용자에게 권한 허용여부를 확인하는 창을 띄운다.
                requestPermissions(permission_list, 0);
            }
        }
    }
    // 권한 확인 여부가 완료되면 호출되는 메서드
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 사용자가 권한 허용 여부를 확인한다.
        for(int i = 0 ; i < grantResults.length ; i++){
            if(grantResults[i] == PackageManager.PERMISSION_GRANTED){
            } else {
            }
        }
    }

    class EventReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {

            Log.d("test", "Got it!");
            if(conn.m_service != null)
            {
                adapter.CopyMissionsList(((MissionManager.MissionManagerBinder)conn.m_service).getService().missionList);
                adapter.notifyDataSetChanged();
            }
        }
    }
}
