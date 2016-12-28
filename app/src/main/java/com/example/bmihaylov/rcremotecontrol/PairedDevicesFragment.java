package com.example.bmihaylov.rcremotecontrol;


import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 */
public class PairedDevicesFragment extends DialogFragment {

//    //private ArrayList<String> mDeviceList = new ArrayList<String>();
//    private BluetoothAdapter myBluetooth = null;
//    private Set pairedDevices;
//    Button btnPaired;
//    ListView devicelist;


    private TextView option1;
    private TextView option2;
    private TextView option3;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_paired_devices, container, false);

            option1 = (TextView) view.findViewById(R.id.option1);
            option2 = (TextView) view.findViewById(R.id.option2);
            option3 = (TextView) view.findViewById(R.id.option3);
            getDialog().setTitle("Paired Devices");

//        //btnPaired = (Button) view.findViewById(R.id.button);
//        devicelist = (ListView) view.findViewById(R.id.listView);
//
//        myBluetooth = BluetoothAdapter.getDefaultAdapter();
//        if(myBluetooth == null)
//        {
//            //Show a mensag. that thedevice has no bluetooth adapter
//            Toast.makeText(getActivity(), "Bluetooth Device Not Available", Toast.LENGTH_LONG).show();
//            //finish apk
//            getActivity().finish();
//        }
//        else
//        {
//            if (myBluetooth.isEnabled())
//            { }
//            else
//            {
//                //Ask to the user turn the bluetooth on
//                Intent turnBTon = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
//                startActivityForResult(turnBTon,1);
//            }
//        }
//
////        btnPaired.setOnClickListener(new View.OnClickListener() {
////            @Override
////            public void onClick(View v)
////            {
//                pairedDevicesList(); //method that will be called
////            }
////        });
//
//
        return view;
    }
//
//    private void pairedDevicesList()
//    {
//        Set<pairedDevices> = myBluetooth.getBondedDevices();
//        ArrayList list = new ArrayList();
//
//        if (pairedDevices.size()>0)
//        {
//            for(BluetoothDevice bt : pairedDevices)
//            {
//                list.add(bt.getName() + "\n" + bt.getAddress()); //Get the device's name and the address
//            }
//        }
//        else
//        {
//            Toast.makeText(getActivity(), "No Paired Bluetooth Devices Found.", Toast.LENGTH_LONG).show();
//        }
//
//        final ArrayAdapter adapter = new ArrayAdapter(getActivity(),android.R.layout.simple_list_item_1, list);
//        devicelist.setAdapter(adapter);
//        //devicelist.setOnItemClickListener(myListClickListener); //Method called when the device from the list is clicked
//
//    }
}
