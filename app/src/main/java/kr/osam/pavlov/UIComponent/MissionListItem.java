package kr.osam.pavlov.UIComponent;

import java.util.Calendar;

import kr.osam.pavlov.Missons.Mission;

public class MissionListItem {

    int present;

    public MissionListItem(Mission _mission) { present = _mission.getPresent(); mission = _mission; }

    public Mission mission;

    public void getMission(Mission _mission) { present = _mission.getPresent(); mission = _mission; }
    public int getType() { return mission.getType(); }
}
