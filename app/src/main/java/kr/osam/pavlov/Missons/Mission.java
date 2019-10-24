package kr.osam.pavlov.Missons;

import android.graphics.drawable.Drawable;
import android.os.IBinder;

import org.json.JSONObject;

import java.util.Calendar;

public abstract class Mission {

    final static public int MISSION_FAILED       = -1;
    final static public int MISSION_SUCCES       =  1;
    final static public int MISSION_ON_PROGRESS  =  0;
    final static public int MISSION_READY        =  0xff;

    final static public int MISSION_TYPE_ALARM         = 0x00;
    final static public int MISSION_TYPE_USEAGE_DEVICE = 0x01;
    final static public int MISSION_TYPE_USEAGE_APP    = 0x02;
    final static public int MISSION_TYPE_WALK_DISTANCE = 0x03;
    final static public int MISSION_TYPE_WALK_STEPCOUNT= 0x04;

    final static public int MISSION_NUM_OF_MISSION_TYPE= 0x05;

    public String title;
    protected int missionID;
    protected int goal;
    protected int present;
    protected int type;
    protected int condition;
    protected Calendar exp;

    private String dbString;

    public void setId(int id)
    {
        this.missionID = id;
    }
    abstract public void upDate(IBinder binder);
    abstract public String getTitle();
    abstract public Calendar getDate();
    abstract public int getMissionID();
    abstract public int getCondition();
    abstract public int getPresent();
    abstract public int getGoal();
    abstract public int getType();
    abstract public JSONObject getJSON();                   //미션의 missionID와 type를 제외한 모든 데이터를 JSONObject로 반환하는 메서드
    abstract public void setJSON(JSONObject jsonObject);    //JSONObject를 입력받아 missionId와 type를 제외한 모든 데이터를 미션에 저장하는 메서드
}