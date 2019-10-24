package kr.osam.pavlov.UIComponent;


import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import kr.osam.pavlov.UIComponent.MainActivity;
import kr.osam.pavlov.UIComponent.MissionListAdapter;
import kr.osam.pavlov.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomListViewFragment extends Fragment {

    ListView missionListView;

    boolean isRunning;
    MissionListAdapter adapter;
    IBinder binder;
    UpdaterThread thread;
    MainActivity activity;

    public CustomListViewFragment() {// Required empty public constructor
        }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        Bundle bundle = getArguments();

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v1 = inflater.inflate(R.layout.fragment_custom_list_view, container, false);


        adapter = new MissionListAdapter();
        missionListView = v1.findViewById(R.id.MissionListView);
        missionListView.setAdapter(adapter);
        isRunning = true;

        activity = (MainActivity)getActivity();

        thread = new UpdaterThread();
        thread.start();

        // Inflate the layout for this fragment
        return v1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class UpdaterThread extends Thread
    {
        @Override
        public void run() {
            while (isRunning)
            {
                try {
                    long tmpTime = SystemClock.currentThreadTimeMillis();

                    adapter.CopyMissionsList(((MainActivity)getActivity()).getService().missionList);

                    activity.unbind();

                    activity.runOnUiThread(new Runnable(){
                        @Override
                        public void run() {
                            adapter.notifyDataSetChanged();
                        }
                    });

                    tmpTime = SystemClock.currentThreadTimeMillis() - tmpTime;
                    sleep((250 - tmpTime)>0 ? (250 - tmpTime) : 0);
                } catch (Exception e) { Log.d("CatchExeption", e.toString()); }
            }

            super.run();
        }
    }
}
