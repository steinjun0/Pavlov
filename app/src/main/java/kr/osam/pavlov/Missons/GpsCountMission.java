package kr.osam.pavlov.Missons;

import android.location.Location;
import android.os.IBinder;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import kr.osam.pavlov.Services.GPSDistanceService;

public class GpsCountMission extends Mission {

    Location location_now;
    Location location_prev;
    List<Location> locationList;
    Double distance;

    GpsCountMission(String _title, int _ID, int _goal, int _present, int _type, Calendar _exp,
                    List<Location> _locationList)
    { title = _title; missionID = _ID; goal = _goal; present = _present; type = _type;
      locationList = _locationList;
      exp = _exp; location_now = null;
      distance = (double)_present;
      location_prev = (locationList.isEmpty()?null:locationList.get(locationList.size()-1));}

    @Override
    public void upDate(IBinder binder) {
        location_prev = location_now;
        locationList.add(location_now);
        location_now = ((GPSDistanceService.GPSDistanceBinder)binder).getService().getLoctaion();
        if( location_prev == null || location_now == location_prev ) { return; }

        double tmp = location_now.distanceTo( location_prev );
        if(tmp < 100 && tmp > .1)
        {
            distance += tmp;
            present = (int)Double.doubleToLongBits(distance);
        }
        if( present > goal ) {condition = 0; return;}
        if( exp.compareTo(Calendar.getInstance()) == -1 ) { condition = -1; return; }
    }

    @Override public int getMissionID() { return missionID; }
    @Override public int getCondition() { return condition; }
    @Override public int getPresent() { return present; }
    @Override public int getGoal() { return goal; }
    @Override public int getType() { return type; }
    @Override public Calendar getDate() { return exp; }
}
