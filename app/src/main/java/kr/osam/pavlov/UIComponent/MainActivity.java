package kr.osam.pavlov.UIComponent;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;

import kr.osam.pavlov.R;
import kr.osam.pavlov.Services.MissionManager;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    //change
    public masterConn conn;
    public Intent intent;

    CustomListViewFragment frag;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

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

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }



    //Navigation Drawer을 위한 백프레즈
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //오른쪽위에 플로팅 관리할듯
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            //Intent intent = new Intent(MainActivity.this,NoticeBoard.class);
            //startActivity(intent);
        } else if (id == R.id.nav_gallery) {
            //Intent intent2 =new Intent(MainActivity.this,ShareActivity.class);
            //startActivity(intent2);   이런식으로 쓰세요

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
    /*
    @Override
    protected void onStart() {

        super.onStart();
    }
    */
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
