package kr.osam.pavlov.Missons;

import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.IBinder;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.osam.pavlov.Services.GPSDistanceService;

public class GpsCountMission extends Mission {

    Location location_now;
    Location location_prev;
    List<Location> locationList;
    Double distance;

    public GpsCountMission(String _title, int _ID, int _goal, int _present, Calendar _exp,
                           List<Location> _locationList)
    { title = _title; missionID = _ID; goal = _goal; present = _present; type = Mission.MISSION_TYPE_WALK_DISTANCE;
        locationList = _locationList; condition = 0;
        exp = _exp; location_now = null;
        distance = (double)_present;
        location_prev = (locationList.isEmpty()?null:locationList.get(locationList.size()-1));}

    @Override
    public void upDate(IBinder binder) {
        location_prev = location_now;
        locationList.add(location_now);
        location_now = ((GPSDistanceService.GPSDistanceBinder)binder).getService().getLoctaion();
        if( location_prev == null ) { return; }

        float tmp = location_now.distanceTo( location_prev );
        if(tmp < 100. && tmp > .1)
        {
            distance += tmp;
            present = (int)Math.round(distance);
        }

        if( present >= goal ) { condition = Mission.MISSION_SUCCES; return; }
        Calendar now = Calendar.getInstance();
        if( exp.compareTo(now) <= 0 ) { condition = Mission.MISSION_FAILED; return; }
    }

    @Override public int getMissionID() { return missionID; }
    @Override public int getCondition() { return condition; }
    @Override public int getPresent() { return present; }
    @Override public int getGoal() { return goal; }
    @Override public int getType() { return type; }
    @Override public Calendar getDate() { return exp; }
    @Override public Drawable getIcon() {return null;}
}