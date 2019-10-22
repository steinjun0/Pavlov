package kr.osam.pavlov;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MissionListActivity extends AppCompatActivity implements View.OnClickListener{

    private Animation fab_open, fab_close;
    private Boolean isFabOpen = false;
    private FloatingActionButton fabAddMission, fabAddCheckMission, fabAddUseTimeMission;

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.fab_addmission:
                anim();
                break;
            case R.id.fab_addcheckmission:
                anim();
                break;
            case R.id.fab_addusetimemission:
                anim();
                break;
        }
    }

    public void anim() {

        if (isFabOpen) {
            fabAddCheckMission.startAnimation(fab_close);
            fabAddUseTimeMission.startAnimation(fab_close);
            fabAddCheckMission.setClickable(false);
            fabAddUseTimeMission.setClickable(false);
            isFabOpen = false;
        } else {
            fabAddCheckMission.startAnimation(fab_open);
            fabAddUseTimeMission.startAnimation(fab_open);
            fabAddCheckMission.setClickable(true);
            fabAddUseTimeMission.setClickable(true);
            isFabOpen = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listview = (ListView)findViewById(R.id.lv_missionmain);

        //데이터를 저장하게 되는 리스트
        List<Mission> list = new ArrayList<>();

        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터
        MissionListViewAdapter missionlistviewadapter = new MissionListViewAdapter();

        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(missionlistviewadapter);

        missionlistviewadapter.addItem();
        missionlistviewadapter.addItem();
        missionlistviewadapter.addItem();

        //플로팅 버튼 애니메이션
        fab_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fab_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_close);

        fabAddMission = (FloatingActionButton) findViewById(R.id.fab_addmission);
        fabAddCheckMission = (FloatingActionButton) findViewById(R.id.fab_addcheckmission);
        fabAddUseTimeMission = (FloatingActionButton) findViewById(R.id.fab_addusetimemission);

        fabAddMission.setOnClickListener(this);
        fabAddCheckMission.setOnClickListener(this);
        fabAddUseTimeMission.setOnClickListener(this);

    }

}
