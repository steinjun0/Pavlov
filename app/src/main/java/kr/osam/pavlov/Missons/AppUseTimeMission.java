package kr.osam.pavlov.Missons;

import android.graphics.drawable.Drawable;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import kr.osam.pavlov.Services.AppUseTimeCheckService;
import kr.osam.pavlov.Services.StepCounterService;

public class AppUseTimeMission extends Mission {


    Drawable icon;

    public AppUseTimeMission(String _title, int _ID, int _goal, Drawable icon)
    { title = _title; missionID = _ID; goal = _goal; condition = 0; type = 2; this.icon = icon;}

    @Override
    public void upDate(IBinder binder) {
        //pkg 이름을 저장하고 있는 title String을 이용해서 어플리케이션 사용 시간을 알아옴
        if(binder != null)
            present =  ((AppUseTimeCheckService.AppUseBinder)binder).getService().getTime(title);
        else
            Log.d("AppUseTimeMission", "binder == null");

        Log.d("AppUseTimeMission", title + present);
        if( present > goal ) { condition = -1; return; }
        //매일 밤 24시에 마지막 upDate후 DB에 저장되고 present는 0으로 초기화, condition은 1로 초기화됨
    }

    @Override public int getMissionID() { return missionID; }
    @Override public int getCondition() { return condition; }
    @Override public int getPresent() { return present; }
    @Override public int getGoal() { return goal; }
    @Override public int getType() { return type; }
    @Override public Calendar getDate() { return exp; }
    public Drawable getIcon() {return icon;}
}
