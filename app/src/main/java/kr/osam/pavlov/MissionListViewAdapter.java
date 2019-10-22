package kr.osam.pavlov;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;


import java.util.ArrayList;

public class MissionListViewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Mission> missions = new ArrayList<Mission>() ;
    private ArrayList<Boolean> itemExpandState = new ArrayList<>();

    // ListViewAdapter의 생성자
    public MissionListViewAdapter() {

    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return missions.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_checkmission, parent, false);
        }


        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = convertView.findViewById(R.id.iv_checkicon);

        // Data Set(missions)에서 position에 위치한 데이터 참조 획득
        Mission listViewItem = missions.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        if(true)
        {
            iconImageView.setImageResource(R.drawable.success);
        }


        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return missions.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem() {
        Mission item = new Mission(0, "dd", missions.size());

        missions.add(item);
        this.itemExpandState.add(false);
    }

    public boolean getExpandState(int position)
    {
        return itemExpandState.get(position);
    }

    public void setExpandState(int position, boolean state)
    {
        itemExpandState.set(position, state);
        return;
    }

    public void deleteAll()
    {
        missions.clear();
    }
}
