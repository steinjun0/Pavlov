package kr.osam.pavlov.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class ScreenOffReceiver extends BroadcastReceiver {

    public static final String SCREEN_OFF_NOTIFICATION = "SCREEN_OFF_NOTIFICATION";
    Date past = null;
    long diff = 0;
    int hr = 0;
    int min = 0;
    int sec = 0;
    String pastStr = null;
    long firstTime = 0;
    long presentTime = 0;

    @Override
    public void onReceive(Context context, Intent intent) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("MM / dd / HH:mm:ss");
        Date present = cal.getTime();
        String presentStr = sdf.format(present.getTime());
        presentTime = present.getTime();
        if(past != null){
            pastStr = sdf.format(past.getTime());
            diff =  present.getTime() - past.getTime();
            sec = (int)(diff/1000)%60;
            min = (int)((diff/1000)/60)%60;
            hr = (int)(((diff/1000)/60)/60)%60;
        }
        else if(past == null){
            firstTime = present.getTime();
        }

        Intent mintent = new Intent();
        //mintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        mintent.putExtra("name", "Screen Off");
        mintent.putExtra("first", firstTime);
        mintent.putExtra("presentTime", presentTime);
        mintent.putExtra("present", presentStr);
        mintent.putExtra("past", pastStr);
        mintent.putExtra("diff", diff);
        mintent.putExtra("sec", sec);
        mintent.putExtra("min", min);
        mintent.putExtra("hr", hr);
        mintent.setAction(SCREEN_OFF_NOTIFICATION);
        Log.d("test", "Screen Off Detection");
        context.sendBroadcast(mintent);
        past = present;
    }
}
