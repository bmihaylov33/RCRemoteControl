package com.example.bmihaylov.rcremotecontrol;


import android.app.Activity;
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
import android.widget.Toast;

import static android.app.Activity.RESULT_CANCELED;


/**
 * A simple {@link Fragment} subclass.
 */
public class StartFragment extends Fragment {

    BluetoothAdapter myBluetooth = null;
    private final static int REQUEST_ENABLE_BT = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_start, container, false); // Inflate the layout for this fragment
        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth == null) {
            msg("Device does not support Bluetooth");
        }

        turn_bluetooth_on();

        return view;
    }

    private void msg(String s) {
        Toast.makeText(getActivity().getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void turn_bluetooth_on() {
        if (!myBluetooth.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            msg("Turning on");
        } else {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
            editNameDialog.show(fm, "fragment_bluetooth_devices");
            msg("Already on");
        }
    }

    protected void makeDiscoverable(){
        // Make local device discoverable
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_ENABLE_BT && resultCode==RESULT_CANCELED) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);

            // Bluetooth successfully enabled!
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Scanning for remote Bluetooth devices...",
                        Toast.LENGTH_SHORT).show();

                // Make local device discoverable by other devices
                makeDiscoverable();
            }

        } else {
            FragmentManager fm = getActivity().getSupportFragmentManager();
            BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
            editNameDialog.show(fm, "fragment_bluetooth_devices");
        }
    }
}
