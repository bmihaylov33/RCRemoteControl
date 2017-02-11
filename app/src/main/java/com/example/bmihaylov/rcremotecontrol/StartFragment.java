//package com.example.bmihaylov.rcremotecontrol;
//
//
//import android.content.Intent;
//import android.content.pm.ActivityInfo;
//import android.os.Bundle;
//import android.os.Handler;
//import android.support.v4.app.Fragment;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class StartFragment extends Fragment {
//
//    private static int SPLASH_TIME_OUT = 3000;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View view = inflater.inflate(R.layout.fragment_start, container, false);
//
//        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                Intent i = new Intent(getActivity(), MainActivity.class);
//                startActivity(i);
//                getActivity().finish();
//            }
//        }, SPLASH_TIME_OUT);
//
//        return view;
//    }
//
//    private void msg(String s) {
//        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
//    }
//
//}
