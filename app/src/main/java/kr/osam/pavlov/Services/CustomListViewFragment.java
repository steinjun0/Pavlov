package kr.osam.pavlov.Services;


import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import kr.osam.pavlov.MainActivity;
import kr.osam.pavlov.MissionListAdapter;
import kr.osam.pavlov.R;

import static android.content.Context.BIND_ABOVE_CLIENT;

/**
 * A simple {@link Fragment} subclass.
 */
public class CustomListViewFragment extends Fragment {

    ListView missionListView;

    boolean isRunning;
    MissionListAdapter adapter;
    UpdaterThread thread;

    public CustomListViewFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       missionListView = getView().findViewById(R.id.MissionListView);
       missionListView.setAdapter(adapter);
       isRunning = true;

       adapter = new MissionListAdapter();
       thread = new UpdaterThread();
       thread.start();

       // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_custom_list_view, container, false);
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

                    ((MainActivity)getActivity()).unbind();

                    runOnUiThread(new Runnable() {
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
