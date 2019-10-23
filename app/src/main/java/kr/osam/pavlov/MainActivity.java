package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TimeCheckerService tcs = new TimeCheckerService();

        TimeCheckerActivity Act1 = new TimeCheckerActivity();
        Intent intent1 = new Intent(this, TimeCheckerActivity.class);
        startActivity(intent1);

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("test", "tcs data" + tcs.goal + tcs.present + tcs.type + tcs.condition);
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
