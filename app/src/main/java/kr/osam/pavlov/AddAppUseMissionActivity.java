package kr.osam.pavlov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AppOpsManager;
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
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.ArrayList;
import java.util.List;

import kr.osam.pavlov.Missons.AppUseTimeMission;
import kr.osam.pavlov.Services.MissionManager;

public class AddAppUseMissionActivity extends AppCompatActivity {

    List<ApplicationInfo> packages;
    MissionManager missionManagerService;

    TextView tv;
    LinearLayout ll;
    LinearLayout ll_cancel, ll_save;
    ImageView iv;
    TimePicker tp;
    int selectedAppIdx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addappusemission);

        //어플리케이션 사용 기록에 접근하기 위한 권한 체크
        if(!checkPermissions(this)) {
            //권한이 없다면 권한 얻는 액티비티 띄우기
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
                    MissionManager.MissionManagerBinder missionManagerBinder = (MissionManager.MissionManagerBinder) service;
                    missionManagerService = missionManagerBinder.getService();
                }

                @Override
                public void onServiceDisconnected(ComponentName name) {
                }
            };

            //서비스 시작, 바인딩
            Intent service_intent = new Intent(AddAppUseMissionActivity.this, MissionManager.class);
            bindService(service_intent, serviceConnection, this.BIND_AUTO_CREATE);

            //모든 패키지를 받아오고, 해당 아이콘을 그리드뷰로 보여줌

            //설치된 패키지들의 리스트
            final PackageManager pm = getPackageManager();
            packages = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            final ArrayList<Drawable> icons = new ArrayList<>();
            final ArrayList<String> pkgLabel = new ArrayList<>();

            //Drawable icon, 실제 어플 이름 가져오기
            class LoadPkgData implements Runnable {

                @Override
                public void run() {
                    for(ApplicationInfo pkg : packages)
                    {
                        icons.add(pkg.loadIcon(pm));
                        pkgLabel.add(String.valueOf(pm.getApplicationLabel(pkg)));
                    }
                }
            }

            LoadPkgData loadPkgData = new LoadPkgData() ;
            Thread loadThread = new Thread(loadPkgData) ;
            loadThread.start() ;


            tv = findViewById(R.id.tv_selectedapp);
            iv = findViewById(R.id.iv_appselect);
            ll = findViewById(R.id.ll_appselect);
            tp = findViewById(R.id.tp_appusetime);
            tp.setIs24HourView(true);
            ll_save = findViewById(R.id.ll_applimitsave);
            ll_cancel = findViewById(R.id.ll_applimitcancel);

            ll.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view)
                {
                    if(Build.VERSION.SDK_INT >= 21)
                    {
                        //앱 선택 창 띄워주기
                        showAlertDialog(icons, pkgLabel, ll.getContext());
                    }
                }
            });

            //아이템 로딩이 끝난 이후 저장 가능
            ll_save.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int limitTime = 0;
                    limitTime += tp.getHour()*3600000;
                    limitTime += tp.getMinute()*60000;
                    missionManagerService.addMission(new AppUseTimeMission(packages.get(selectedAppIdx).packageName, 0, limitTime, icons.get(selectedAppIdx)));
                    onBackPressed();
                }
            });

            ll_cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
                }
            });
        }



    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

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

    //어플리케이션을 선택하는 다이얼로그 생성 메소드
    private void showAlertDialog(final ArrayList<Drawable> icons, final ArrayList<String> pkgLabels, final Context context) {

        // Prepare grid view
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("어플리케이션 선택");

        final PackageIconGridViewAdapter gridAdapter = new PackageIconGridViewAdapter(context, R.layout.item_packageicon, icons, pkgLabels);

        GridView gridView = new GridView(this);

        gridView.setAdapter(gridAdapter);
        gridView.setNumColumns(2);

        // Set grid view to alertDialog
        builder.setView(gridView);
        final AlertDialog dialog = builder.show();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                selectedAppIdx = position;
                tv.setText(pkgLabels.get(position));
                iv.setImageDrawable(icons.get(position));
                dialog.dismiss();

            }
        });
    }


}



