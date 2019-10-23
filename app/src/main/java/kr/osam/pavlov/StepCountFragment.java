package kr.osam.pavlov;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;


public class StepCountFragment extends DialogFragment {

    EditText inputbox;
    TextView title;
    positivebuttonListener positiveListener;
    negativebuttonListener negativeListener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_step_count,null);
        positiveListener = new positivebuttonListener();
        negativeListener = new negativebuttonListener();
        inputbox = view.findViewById(R.id.edittext);

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
            StepCountingActivity activity = (StepCountingActivity)getActivity();
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
