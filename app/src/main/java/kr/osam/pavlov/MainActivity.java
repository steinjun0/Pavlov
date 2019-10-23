package kr.osam.pavlov;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    ListView missionListView;
    ArrayList<HashMap<String,Object>> containAdapter = new ArrayList<HashMap<String,Object>> ();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        missionListView = findViewById(R.id.MissionListView);
    }
}
