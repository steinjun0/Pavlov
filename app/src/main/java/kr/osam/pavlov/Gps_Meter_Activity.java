package kr.osam.pavlov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.maps.model.LatLng;
import java.util.ArrayList;
import java.util.List;


/**************************************************************************************************


 이 액티비티는 실행시에 연결될 Service의 ID, 미션의 Title, goal,


 *************************************************************************************************/

public class Gps_Meter_Activity extends AppCompatActivity {

    List<LatLng> coord = new ArrayList<LatLng>() {};

    int tmp;

    private int Attached_id;
    private double distance;
    private int goal;
    private String Title;

    String[] permission_list =
            {
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps__meter_);

        if (savedInstanceState == null) {
            MapFragment mapFragment = new MapFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.map, mapFragment, "main")
                    .commit();
        }
        checkPermission();
    }

    public void checkPermission() {
        // 현재 안드로이드 버전이 6.0 미만이면 메서드를 종료한다.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return;
        }
        // 각 권한의 허용 여부를 확인한다.
        for (String permission : permission_list) {
            // 권한 허용 여부를 확인한다.
            int chk = checkCallingOrSelfPermission(permission);
            // 거부 상태라고 한다면..
            if (chk == PackageManager.PERMISSION_DENIED) {
                // 사용자에게 권한 허용여부를 확인하는 창을 띄운다.
                requestPermissions(permission_list, 0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuaction, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.editmenu:

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) { ft.remove(prev); }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new GPS_Fragment_Dialog();
                dialogFragment.show(ft, "dialog");
                break;

            case R.id.deletemenu:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("삭제");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                requestDelete();
                            }
                        });
                builder.setNegativeButton("아니오",null);
                builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestEdit()
    {
        Intent intent = new Intent("Edit-request");
        intent.putExtra("Service-id", Attached_id);
        intent.putExtra("Edit-goal", tmp);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }
    public void requestDelete()
    {
        Intent intent = new Intent("Delete-request");
        intent.putExtra("Service-id", Attached_id);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        finish();
    }

}