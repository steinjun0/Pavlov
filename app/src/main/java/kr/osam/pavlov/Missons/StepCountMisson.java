package kr.osam.pavlov.Missons;

import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

import kr.osam.pavlov.Services.StepCounterService;

public class StepCountMisson extends Mission {

    int step_Now;
    int step_Prev;

    public StepCountMisson(String _title, int _ID, int _goal, int _present, Calendar _exp)
    { title = _title; missionID = _ID; goal = _goal; present = _present; type = Mission.MISSION_TYPE_WALK_STEPCOUNT; condition = 0;
        exp = _exp; step_Now = 0; step_Prev = 0; }

    @Override
    public void upDate(IBinder binder) {

        if(step_Prev == 0)
        {
            step_Now =  ((StepCounterService.StepCounterBinder)binder).getService().getSteps();
            step_Prev = step_Now;
        }

        step_Prev = step_Now;
        step_Now =  ((StepCounterService.StepCounterBinder)binder).getService().getSteps();
        present += (step_Now - step_Prev);

        if( present >= goal ) { condition = Mission.MISSION_SUCCES; return; }
        Calendar now = Calendar.getInstance();
        if( (exp.getTimeInMillis() - now.getTimeInMillis()) <= 0 ) { condition = Mission.MISSION_FAILED; return; }
    }

    @Override public int getMissionID() { return missionID; }
    @Override public int getCondition() { return condition; }
    @Override public int getPresent() { return present; }
    @Override public int getGoal() { return goal; }
    @Override public int getType() { return type; }
    @Override public Calendar getDate() { return exp; }
}