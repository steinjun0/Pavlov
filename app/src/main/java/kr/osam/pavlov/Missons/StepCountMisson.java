package kr.osam.pavlov.Missons;

import android.content.Intent;

import java.util.Calendar;

public class StepCountMisson extends Mission {

    long step_Now;
    long step_Prev;

    public StepCountMisson(String _title, int _ID, int _goal, int _present, Calendar _exp)
                { title = _title; missionID = _ID; goal = _goal; present = _present; type = Mission.MISSION_TYPE_WALK_STEPCOUNT; condition = 0;
                  exp = _exp; step_Now = 0; step_Prev = 0; }

    @Override
    public void upDate(Intent intent) {

        if(step_Prev == 0)
        {
            step_Now =  intent.getLongExtra("Steps", 0);
            step_Prev = step_Now;
        }

        step_Prev = step_Now;
        step_Now = intent.getLongExtra("Steps", 0);
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
