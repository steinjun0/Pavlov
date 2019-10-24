package kr.osam.pavlov;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import kr.osam.pavlov.Missons.Mission;

public class MissionListAdapter extends BaseAdapter {

    private ArrayList<MissionListItem> missionListItems = new ArrayList<MissionListItem>();

    public void CopyMissionsList(List<Mission> _missionList)
    {
        missionListItems.clear();
        for(int i = 0; i < _missionList.size(); i++)
        {
            missionListItems.add(new MissionListItem(_missionList.get(i)));
        }
    }

    @Override
    public int getViewTypeCount() {
        return Mission.MISSION_NUM_OF_MISSION_TYPE;
    }

    // position 위치의 아이템 타입 리턴.
    @Override
    public int getItemViewType(int position) {
        return missionListItems.get(position).getType() ;
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return missionListItems.size() ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final Context context = parent.getContext();
        int viewType = getItemViewType(position) ;

        //if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            MissionListItem listItem = missionListItems.get(position);

            switch (viewType) {
                case Mission.MISSION_TYPE_ALARM:
                    convertView = inflater.inflate(R.layout.alarm_item_layout,
                            parent, false);

                    TextView alarm_titleText  = convertView.findViewById(R.id.AlramTitleText);
                    TextView alarm_DateText   = convertView.findViewById(R.id.AlramDateText);
                    TextView alarm_hourText   = convertView.findViewById(R.id.AlramHourText);
                    TextView alarm_minuetText = convertView.findViewById(R.id.AlramMinuetText);

                    String subtitle = ByTwoChar(listItem.mission.getDate().get(Calendar.YEAR)) + "-" +
                                    ByTwoChar(listItem.mission.getDate().get(Calendar.MONTH)) + "-" +
                                    ByTwoChar(listItem.mission.getDate().get(Calendar.DAY_OF_MONTH));

                    alarm_titleText.setText(listItem.mission.title);
                    alarm_DateText.setText(subtitle);
                    alarm_hourText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.HOUR_OF_DAY)));
                    alarm_minuetText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.MINUTE)));
                    break;

                case Mission.MISSION_TYPE_USEAGE_DEVICE:
                    convertView = inflater.inflate(R.layout.timechecker_list_item,
                            parent, false);
                    TextView device_titleText  = convertView.findViewById(R.id.TimeTitleText);
                    TextView device_hourText   = convertView.findViewById(R.id.TimeHourText);
                    TextView device_minuetText = convertView.findViewById(R.id.TimeMinuetText);
                    TextView device_secText    = convertView.findViewById(R.id.TimeSecText);

                    device_titleText.setText(listItem.mission.title);
                    device_hourText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.HOUR_OF_DAY)));
                    device_minuetText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.MINUTE)));
                    device_secText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.SECOND)));
                    break;

                case Mission.MISSION_TYPE_USEAGE_APP:
                    convertView = inflater.inflate(R.layout.app_usage_cheker,
                            parent, false);
                    TextView app_titleText  = convertView.findViewById(R.id.APPTitleText);
                    TextView app_hourText   = convertView.findViewById(R.id.APPHourText);
                    TextView app_minuetText = convertView.findViewById(R.id.APPMinuetText);
                    TextView app_secText    = convertView.findViewById(R.id.APPSecText);
                    ImageView app_iconView  = convertView.findViewById(R.id.APPiconView);

                    app_titleText.setText(listItem.mission.title);
                    app_hourText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.HOUR_OF_DAY)));
                    app_minuetText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.MINUTE)));
                    app_secText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.SECOND)));
                    // TODO app_iconView.setImageDrawable(((아무튼)listItem.mission).Drawable);
                    break;

                case Mission.MISSION_TYPE_WALK_DISTANCE:
                    convertView = inflater.inflate(R.layout.gps_list_item,
                            parent, false);
                    TextView GPS_titleText   = convertView.findViewById(R.id.CounterTitleText);
                    TextView GPS_DateText    = convertView.findViewById(R.id.CounterDateText);
                    TextView GPS_PresentText = convertView.findViewById(R.id.CounterpresentText);
                    TextView GPS_GoalText    = convertView.findViewById(R.id.CountergoalText);
                    TextView GPS_PerText     = convertView.findViewById(R.id.CounterPer);
                    ProgressBar GPS_progressBar = convertView.findViewById(R.id.progressBar);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        switch (listItem.mission.getCondition()) {
                            case Mission.MISSION_FAILED:
                                GPS_GoalText.setTextColor(context.getColor(R.color.colorAccent));
                                GPS_PerText.setTextColor(context.getColor(R.color.colorAccent));
                                GPS_PresentText.setTextColor(context.getColor(R.color.colorAccent));
                                break;

                            case Mission.MISSION_SUCCES:
                                GPS_GoalText.setTextColor(context.getColor(R.color.colorPrimary));
                                GPS_PerText.setTextColor(context.getColor(R.color.colorPrimary));
                                GPS_PresentText.setTextColor(context.getColor(R.color.colorPrimary));
                                break;

                            case Mission.MISSION_ON_PROGRESS:
                                GPS_GoalText.setTextColor(GPS_titleText.getTextColors());
                                GPS_PerText.setTextColor(GPS_titleText.getTextColors());
                                GPS_PresentText.setTextColor(GPS_titleText.getTextColors());
                                break;
                        }
                    }

                    GPS_titleText.setText(listItem.mission.title);
                    GPS_DateText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.YEAR)) + "-" +
                                         ByTwoChar(listItem.mission.getDate().get(Calendar.MONTH)) + "-" +
                                         ByTwoChar(listItem.mission.getDate().get(Calendar.DAY_OF_MONTH)));
                    GPS_PresentText.setText((listItem.mission.getPresent()<1000?listItem.mission.getPresent() + "m":((float)listItem.mission.getPresent()/1000)+ "km"));
                    GPS_GoalText.setText((listItem.mission.getGoal()<1000?listItem.mission.getGoal() + "m":((float)listItem.mission.getGoal()/1000) + "km"));
                    GPS_progressBar.setProgress((int)((float)listItem.mission.getPresent()/(float)listItem.mission.getGoal()*GPS_progressBar.getMax()));
                    break;

                case Mission.MISSION_TYPE_WALK_STEPCOUNT:
                    convertView = inflater.inflate(R.layout.gps_list_item,
                            parent, false);

                    TextView Step_titleText   = convertView.findViewById(R.id.CounterTitleText);
                    TextView Step_DateText    = convertView.findViewById(R.id.CounterDateText);
                    TextView Step_PresentText = convertView.findViewById(R.id.CounterpresentText);
                    TextView Step_PerText     = convertView.findViewById(R.id.CounterPer);
                    TextView Step_GoalText    = convertView.findViewById(R.id.CountergoalText);
                    ProgressBar Step_progressBar = convertView.findViewById(R.id.progressBar);

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        switch (listItem.mission.getCondition()) {
                            case Mission.MISSION_FAILED:
                                Step_GoalText.setTextColor(context.getColor(R.color.colorAccent));
                                Step_PerText.setTextColor(context.getColor(R.color.colorAccent));
                                Step_PresentText.setTextColor(context.getColor(R.color.colorAccent));
                                break;

                            case Mission.MISSION_SUCCES:
                                Step_GoalText.setTextColor(context.getColor(R.color.colorPrimary));
                                Step_PerText.setTextColor(context.getColor(R.color.colorPrimary));
                                Step_PresentText.setTextColor(context.getColor(R.color.colorPrimary));
                                break;

                            case Mission.MISSION_ON_PROGRESS:
                                Step_GoalText.setTextColor(Step_titleText.getTextColors());
                                Step_PerText.setTextColor(Step_titleText.getTextColors());
                                Step_PresentText.setTextColor(Step_titleText.getTextColors());
                                break;
                        }
                    }

                    Step_titleText.setText(listItem.mission.title);
                    Step_DateText.setText(ByTwoChar(listItem.mission.getDate().get(Calendar.YEAR)) + "-" +
                            ByTwoChar(listItem.mission.getDate().get(Calendar.MONTH)) + "-" +
                            ByTwoChar(listItem.mission.getDate().get(Calendar.DAY_OF_MONTH)));
                    Step_PresentText.setText(listItem.mission.getPresent() + "걸음");
                    Step_GoalText.setText(listItem.mission.getGoal() + "걸음");
                    Step_progressBar.setProgress((int)((float)listItem.mission.getPresent()/(float)listItem.mission.getGoal()*Step_progressBar.getMax()));
                    break;
            }
       // }

        ViewGroup.LayoutParams params = convertView.getLayoutParams();
        params.height = 256;
        convertView.setLayoutParams(params);

        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) { return missionListItems.get(position) ; }

    private String ByTwoChar(int in)
    {
        return in < 10 ? "0" + in : "" + in;
    }

}
