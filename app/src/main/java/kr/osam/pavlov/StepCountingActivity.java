package kr.osam.pavlov;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class StepCountingActivity extends AppCompatActivity {


    private int Attached_id;
    private int goal;
    int tmp;
    int step;

    TextView stepText;
    Intent step_service_intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counting);

        stepText = findViewById(R.id.StepText);
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private BroadcastReceiver LocalReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            step =  intent.getIntExtra("int",0);
            stepText.setText("" + step + " 보");
        }
    };

    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuaction, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case R.id.editmenu:

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
                if (prev != null) { ft.remove(prev); }
                ft.addToBackStack(null);
                DialogFragment dialogFragment = new StepCountFragment();
                dialogFragment.show(ft, "dialog");
                break;

            case R.id.deletemenu:

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("삭제");
                builder.setMessage("정말로 삭제하시겠습니까?");
                builder.setPositiveButton("예",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                requestDelete();
                            }
                        });
                builder.setNegativeButton("아니오",null);
                builder.show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void requestEdit()
    {
        Intent intent = new Intent("Edit-request");
        intent.putExtra("Service-id", Attached_id);
        intent.putExtra("Edit-goal", tmp);
        finish();
    }
    public void requestDelete() {
        Intent intent = new Intent("Delete-request");
        intent.putExtra("Service-id", Attached_id);
        finish();
    }
}
