package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class StepCountingActivity extends AppCompatActivity {


    int step;
    TextView stepText;
    Intent step_service_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counting);

        stepText = findViewById(R.id.StepText);

        LocalBroadcastManager.getInstance(this).registerReceiver(
                LocalReceiver, new IntentFilter("Step-received"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(LocalReceiver);
    }

    private BroadcastReceiver LocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            step =  intent.getIntExtra("int",0);
            stepText.setText("" + step + " 보");
        }
    };

    public void StartService(View view)
    {
        step_service_intent = new Intent(this, StepCounterService.class);
        startService(step_service_intent);
    }
    public void StopService(View view)
    {
        stopService(step_service_intent);
    }
}
