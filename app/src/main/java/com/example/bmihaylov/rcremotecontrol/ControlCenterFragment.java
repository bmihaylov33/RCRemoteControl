package com.example.bmihaylov.rcremotecontrol;


import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ControlCenterFragment extends DialogFragment {

    Button new_bt;
    Button saved_bt;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_control, container, false);

        getDialog().setTitle("Control Center");

        new_bt = (Button) view.findViewById(R.id.new_bt);
        saved_bt = (Button) view.findViewById(R.id.saved_bt);

        new_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ControlFragment fragment = new ControlFragment();
                android.support.v4.app.FragmentTransaction fragmentTransaction =
                        getActivity().getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.fragment_container,fragment);
                fragmentTransaction.commit();
            }
        });

        return view;
    }

}
