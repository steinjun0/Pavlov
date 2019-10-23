package kr.osam.pavlov.Missons;

import android.os.IBinder;

public abstract class Mission {

    final static public int MISSION_FAILED = -1;
    final static public int MISSION_SUCCES =  1;
    final static public int MISSION_ON_PROGRESS = 0;
    final static public int MISSION_READY = 0xff;

    final static public int MISSION_TYPE_ALARM         = 0x00;
    final static public int MISSION_TYPE_USEAGE_DEVICE = 0x01;
    final static public int MISSION_TYPE_USEAGE_APP    = 0x02;
    final static public int MISSION_TYPE_WALK_DISTANCE = 0x03;
    final static public int MISSION_TYPE_WALK_STEPCOUNT= 0x04;

    final static public int MISSION_NUM_OF_MISSION_TYPE= 0x05;


    public String title; //미션 이름
    protected int missionID; //MissionManaer가 매기는 순서
    protected int goal; //성공조건(목표값0
    protected int present; //현재값
    protected int type; //미션 번호(공책 참조)
    protected int condition; //위에 참조

    abstract public void upDate(IBinder binder);
    abstract public int getCondition();
    abstract public int getPresent();
    abstract public int getGoal();
    abstract public int getType();
}
