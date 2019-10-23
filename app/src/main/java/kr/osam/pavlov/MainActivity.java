package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TimeCheckerActivity Act1 = new TimeCheckerActivity();
        TimeCheckerActivity Act2 = new TimeCheckerActivity();
        Intent intent1 = new Intent(this, TimeCheckerActivity.class);
        Intent intent2 = new Intent(this, TimeCheckerActivity.class);

        startActivity(intent1);
        startActivity(intent2);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
