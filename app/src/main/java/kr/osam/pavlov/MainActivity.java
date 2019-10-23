package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //메인에 모든 서비스 집합을 생성, 이후 인텐트 등으로 참조
        ArrayList<AppUseTimeCheckService> appUseTimeCheckServices = new ArrayList<>();

        Intent intent = new Intent(MainActivity.this,UsageStatsManagerTestActivity.class);
        startActivity(intent);

    }
}
