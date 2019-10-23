package kr.osam.pavlov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class AddAppUsageMissionActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    //AppUseTimeCheckService appUseTimeCheckService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appuse);

        final PackageManager pm = getPackageManager();

        //설치된 패키지들의 리스트
        packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
        ArrayList<Drawable> icons = new ArrayList<Drawable>();

        if(Build.VERSION.SDK_INT >= 21)
        {
            //startActivityForResult(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS), 1);

            for (ApplicationInfo packageInfo : packages) {
                String packageName = packageInfo.packageName;
                try
                {
                    icons.add(getPackageManager().getApplicationIcon(packageName));
                }
                catch (PackageManager.NameNotFoundException e)
                {
                    e.printStackTrace();
                }
            }

        }

        //서비스 커넥션 생성
//        final ServiceConnection serviceConnection = new ServiceConnection() {
//            @Override
//            public void onServiceConnected(ComponentName name, IBinder service) {
//                //서비스와 연결되었을 때 호출되는 메서드
//                AppUseTimeCheckService.AppUseBinder appUseBinder = (AppUseTimeCheckService.AppUseBinder) service;
//                appUseTimeCheckService = appUseBinder.getService();
//            }
//
//            @Override
//            public void onServiceDisconnected(ComponentName name) {
//            }
//        };
//
//        final Intent service_intent = new Intent(AddAppUsageMissionActivity.this, AppUseTimeCheckService.class);
//        bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);
    }

    //어플리케이션을 선택하는 다이얼로그 생성 메소드
    private void showAlertDialog(ArrayList<Drawable> icons, final Context context) {

        // Prepare grid view
        GridView gridView = new GridView(this);
        PackageIconGridViewAdapter gridAdapter = new PackageIconGridViewAdapter(this, R.layout.item_packageicon, icons);

        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(4);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //서비스 생성


                //새로운 다이얼로그 출력
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                TextView dialogTv = new TextView(context);
                //dialogTv.setText(String.valueOf(appUseTimeCheckService.getUsedTime(packages.get(position).packageName)));
                builder.setView(dialogTv);
                builder.setTitle(packages.get(position).packageName);
                builder.show();
            }
        });

        // Set grid view to alertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(gridView);
        builder.setTitle("App Selection");
        builder.show();
    }

}