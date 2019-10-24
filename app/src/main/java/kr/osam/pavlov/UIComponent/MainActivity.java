package kr.osam.pavlov.UIComponent;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import kr.osam.pavlov.R;
import kr.osam.pavlov.Services.MissionManager;

public class MainActivity extends AppCompatActivity {

    public masterConn conn;
    public Intent intent;

    CustomListViewFragment frag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        conn = new MainActivity.masterConn();

        intent = new Intent(this, MissionManager.class);
        startService(intent);
        bindService(intent, conn, BIND_ABOVE_CLIENT);

        Bundle bundle= new Bundle();

        if(Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR2) {
            bundle.putBinder("Binder",conn.m_service); }

        frag = new CustomListViewFragment();
        frag.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.FragmentContents, frag).commit();



    }

    @Override
    protected void onStart() {

        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        // isRunning = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
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
        //isRunning = false;
        super.onDestroy();
    }

    public static boolean isActivityAvailable(Activity activity) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return !activity.isFinishing() && !activity.isDestroyed();
        } else {
            return !activity.isFinishing();
        }
    }
    public MissionManager getService()
    {
        bindService(intent, conn, BIND_ABOVE_CLIENT);

        MissionManager manager = ((MissionManager.MissionManagerBinder)conn.m_service).getService();

        return manager;
    }
    public void unbind()
    {
        unbindService(conn);
    }
}
