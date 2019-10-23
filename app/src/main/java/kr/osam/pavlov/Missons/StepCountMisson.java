package kr.osam.pavlov.Missons;

import android.os.IBinder;

public class StepCountMisson extends Mission {

    StepCountMisson(String _title, int _ID, int _goal, int _present, int _type) { title = _title; missionID = _ID; goal = _goal; present = _present; type = _type; }

    @Override
    public void upDate(IBinder binder) {
        if (condition != 0) { return; }
//        ((StepCounterBinder)binder).getClass();
    }

    @Override public int getCondition() { return condition; }
    @Override public int getPresent() { return present; }
    @Override public int getGoal() { return goal; }
    @Override public int getType() { return type; }
}
