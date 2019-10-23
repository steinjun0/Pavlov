package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    AppUseTimeCheckService appUseTimeCheckService;

    //PACKAGE_USAGE_STATS권한을 갖고 있는지 체크
    public int checkPermissions()
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return -1;
        }
        String[] permissions = {
                Manifest.permission.PACKAGE_USAGE_STATS};
        int chk = checkCallingOrSelfPermission(Manifest.permission.PACKAGE_USAGE_STATS);
        if(chk == PackageManager.PERMISSION_DENIED)
        {
            requestPermissions(permissions,0);
            return 1;
        }
        return 0;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);

        ServiceConnection serviceConnection = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                //서비스와 연결되었을 때 호출되는 메서드
                AppUseTimeCheckService.AppUseBinder appUseBinder = (AppUseTimeCheckService.AppUseBinder) service;
                appUseTimeCheckService = appUseBinder.getService();
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
            }
        };

        final PackageManager pm = getPackageManager();

        //설치된 패키지들의 리스트
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);

        Intent service_intent = new Intent(MainActivity.this, AppUseTimeCheckService.class);
        bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);


        //메인에 모든 서비스 집합을 생성, 이후 인텐트 등으로 참조

        /*Intent intent = new Intent(MainActivity.this, AppSelectActivity.class);
        //intent.putExtra("serviceIntent", AppUseTimeCheckService.class);
        startActivity(intent);*/

    }
}
