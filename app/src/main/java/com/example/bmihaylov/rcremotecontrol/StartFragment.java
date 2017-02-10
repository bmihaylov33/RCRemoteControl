package com.example.bmihaylov.rcremotecontrol;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    BluetoothAdapter myBluetooth = null;
    Button start_bt;


    public StartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false);

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        start_bt = (Button) view.findViewById(R.id.start_bt);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        if (!myBluetooth.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            msg("Turned on");
        } else {
            msg("Already on");
        }

        FragmentManager fm = getFragmentManager();
        BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
        editNameDialog.show(fm, "fragment_bluetooth_devices");

        start_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);
            }
        });

        return view;
    }

    private void msg(String s) {
        Toast.makeText(getActivity(),s,Toast.LENGTH_LONG).show();
    }

}
