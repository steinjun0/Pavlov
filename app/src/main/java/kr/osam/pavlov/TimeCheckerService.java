package kr.osam.pavlov;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class TimeCheckerService extends Service {

    public String title = "Checking Using Time"; //미션 이름
    public int goal=1; //성공여부
    public int present=2; //현재값
    public int type=3; //미션 번호(공책 참조)
    public int condition=4; //위에 참조

    public TimeCheckerService() {
    }
    LocalBinder binder = new LocalBinder();

    @Override
    public IBinder onBind(Intent intent) {

        Log.d("test","setData");
        printData();
        return binder;
    }

    class LocalBinder extends Binder{
        TimeCheckerService getService(){
            return TimeCheckerService.this;
        }
    }

    public void setData(int goal, int present,int type, int condition){
        try {
            this.goal = goal;
        }catch(Exception e){}
        try {
            this.present = present;
        }catch(Exception e){}
        try {
            this.type = type;
        }catch(Exception e){}
        try {
            this.condition = condition;
        }catch(Exception e){}

    }

    public void printData(){
        Log.d("test", "goal: " + goal + " present: " + present + " type: " + type + " condition: "+ condition);
    }
}
