package kr.osam.pavlov;

import android.content.Context;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;


public class MissionListActivity extends AppCompatActivity implements View.OnClickListener{

    //fab 관련 클래스들 선언
    private Animation fab_open, fab_close;
    private Boolean fabOpenState = false;
    private FloatingActionButton fabAddMission, fabAddCheckMission, fabAddUseTimeMission;

    //미션매니저 선언
    MissionManager missionManager = new MissionManager();

    //미션리스트뷰어댑터 선언
    final MissionListViewAdapter missionListViewAdapter = new MissionListViewAdapter();


    @Override
    public void onClick(View v) {
        animate_fab();
    }

    public void animate_fab() {

        if (fabOpenState) {
            fabAddCheckMission.startAnimation(fab_close);
            fabAddUseTimeMission.startAnimation(fab_close);
            fabAddCheckMission.setClickable(false);
            fabAddUseTimeMission.setClickable(false);
            fabOpenState = false;
        } else {
            fabAddCheckMission.startAnimation(fab_open);
            fabAddUseTimeMission.startAnimation(fab_open);
            fabAddCheckMission.setClickable(true);
            fabAddUseTimeMission.setClickable(true);
            fabOpenState = true;
        }
    }




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mission_list);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listview = findViewById(R.id.lv_missionmain);



        //리스트뷰와 리스트를 연결하기 위해 사용되는 어댑터


        //리스트뷰의 어댑터를 지정해준다.
        listview.setAdapter(missionListViewAdapter);

        missionListViewAdapter.addItem();
        missionListViewAdapter.addItem();
        missionListViewAdapter.addItem();

        //인플레이터로 미리 각 expand menu들을 작성
        final LayoutInflater layoutInflater =
                (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);




        //리스트뷰에 온클릭리스너를 지정, 레이아웃을 추가
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                LinearLayout dstLayout = view.findViewById(R.id.ll_checkitem);
                if(missionListViewAdapter.getExpandState(position)) {
                    dstLayout.setVisibility(View.GONE);
                    missionListViewAdapter.setExpandState(position, false);

                }
                else {
                    dstLayout.setVisibility(View.VISIBLE);
                    missionListViewAdapter.setExpandState(position, true);
                }

            }
        });


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