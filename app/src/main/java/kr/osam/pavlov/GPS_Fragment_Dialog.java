package kr.osam.pavlov;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class GPS_Fragment_Dialog extends DialogFragment{

    EditText inputbox;
    TextView title;
    positivebuttonListener positiveListener;
    negativebuttonListener negativeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_gps__fragment__dialog,null);
        positiveListener = new positivebuttonListener();
        negativeListener = new negativebuttonListener();
        inputbox = view.findViewById(R.id.editText4);

        view.findViewById(R.id.Confirm).setOnClickListener(positiveListener);
        view.findViewById(R.id.Deny).setOnClickListener(negativeListener);

        builder.setView(view);
        Dialog dialog = builder.create();

        return dialog;
    }

    class positivebuttonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            Gps_Meter_Activity activity = (Gps_Meter_Activity)getActivity();
            try {
                activity.tmp = Integer.parseInt(inputbox.getText().toString());
                activity.requestEdit();
                dismiss();
            }
            catch (Exception e)
            {
                Log.d("test", e.toString());
            }
        }
    }
    class negativebuttonListener implements View.OnClickListener
    {
        @Override
        public void onClick(View v) {
            dismiss();
        }
    }
}