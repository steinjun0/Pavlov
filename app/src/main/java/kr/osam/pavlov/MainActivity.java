package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;
import android.app.AppOpsManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    AppUseTimeCheckService appUseTimeCheckService;



    //PACKAGE_USAGE_STATS권한을 갖고 있는지 체크하는 메서드
    public boolean checkPermissions(Context context)
    {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
            return false;
        }

        boolean permissionState = false;
        AppOpsManager appOps = (AppOpsManager) context
                .getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow(AppOpsManager.OPSTR_GET_USAGE_STATS,
                android.os.Process.myUid(), context.getPackageName());

        if (mode == AppOpsManager.MODE_DEFAULT) {
            permissionState = (context.checkCallingOrSelfPermission(android.Manifest.permission.PACKAGE_USAGE_STATS) == PackageManager.PERMISSION_GRANTED);
        } else {
            permissionState = (mode == AppOpsManager.MODE_ALLOWED);
        }

        return permissionState;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if(!checkPermissions(this)) {
            //권한이 없다면 실행 X
            startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);
        }
        else
        {
            //권한이 있으면 기능 실행

            //서비스커넥션
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

            //패키지 리스트를 얻어오기 위한 매니저
            final PackageManager pm = getPackageManager();

            //설치된 패키지들의 리스트
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);



            //서비스 시작, 바인딩
            Intent service_intent = new Intent(MainActivity.this, AppUseTimeCheckService.class);
            bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);
        }
    }
}
