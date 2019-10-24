package kr.osam.pavlov.UIComponent;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import kr.osam.pavlov.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class ListFragment extends DialogFragment{

    private ListView lv_missions;
    Context context;
    Intent intent;

    public ListFragment() {
        // Required empty public constructor
    }


    static ListFragment newInstance(Context context) {

        ListFragment f = new ListFragment();
        f.context = context;

        // Supply num input as an argument.
//        Bundle args = new Bundle();
//        args.putInt("num", num);
//        f.setArguments(args);

        return f;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_list, container, false);

        lv_missions = view.findViewById(R.id.lv_missions);
        String[] items = {
                "어플 사용 시간 미션", "걸은 거리 측정 미션", "걸음 수 측정 미션"
        };
        ListAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, items);
        onItemClickListener listener = new onItemClickListener();

        lv_missions.setAdapter(adapter);
        lv_missions.setOnItemClickListener(listener);

        // Inflate the layout for this fragment
        return view;
    }

    class onItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            getDialog().dismiss();
            switch (position)
            {
                case 0:
                    intent = new Intent(context, AddAppUseMissionActivity.class);
                    startActivity(intent);
                    break;
                case 1:
                    intent = new Intent(context, AddAppUseMissionActivity.class);
                    startActivity(intent);
                    break;
                case 2:
                    intent = new Intent(context, AddAppUseMissionActivity.class);
                    startActivity(intent);
                    break;
            }
        }
    }

}