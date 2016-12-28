package com.example.bmihaylov.rcremotecontrol;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ControlFragment extends DialogFragment {

    private TextView option1;
    private TextView option2;
    private TextView option3;
    private TextView option4;
    private TextView option5;
    private TextView option6;
    private TextView option7;
    private TextView option8;
    private TextView option9;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        option1 = (TextView) view.findViewById(R.id.option1);
        option2 = (TextView) view.findViewById(R.id.option2);
        option3 = (TextView) view.findViewById(R.id.option3);
        option4 = (TextView) view.findViewById(R.id.option4);
        option5 = (TextView) view.findViewById(R.id.option5);
        option6 = (TextView) view.findViewById(R.id.option6);
        option7 = (TextView) view.findViewById(R.id.option7);
        option8 = (TextView) view.findViewById(R.id.option8);
        option9 = (TextView) view.findViewById(R.id.option9);
        getDialog().setTitle("Control Center");

        return view;
    }

}
