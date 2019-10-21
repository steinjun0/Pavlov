package kr.osam.pavlov.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    public static final String MISSTION_SUCCESS_NOTIFICATION = "MISSTION_SUCCESS_NOTIFICATION";
    public static final String MISSTION_FAIL_NOTIFICATION = "MISSTION_FAIL_NOTIFICATION";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("test", "AlarmReceived");
        Intent mintent = new Intent();
        if(intent.getStringExtra("flag").equals("fail")) {
            Log.d("test","실패하셨습니다");
            Toast.makeText(context, "실패하셨습니다", Toast.LENGTH_SHORT).show();
            mintent.setAction(MISSTION_FAIL_NOTIFICATION);
            context.sendBroadcast(mintent);
        }
        else if(intent.getStringExtra("flag").equals("success")) {
            Log.d("test","성공하셨습니다");
            Toast.makeText(context, "성공하셨습니다", Toast.LENGTH_SHORT).show();
            mintent.setAction(MISSTION_SUCCESS_NOTIFICATION);
            context.sendBroadcast(mintent);
        }
    }
}
