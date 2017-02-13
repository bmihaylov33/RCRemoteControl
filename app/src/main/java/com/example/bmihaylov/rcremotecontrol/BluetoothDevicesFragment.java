package com.example.bmihaylov.rcremotecontrol;


import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 */
public class BluetoothDevicesFragment extends DialogFragment {

    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public static BluetoothSocket bluetoothSocket;
    BluetoothAdapter myBluetooth = null;
    private ArrayAdapter adapter;
    private ListView listView;
    private static final int ENABLE_BT_REQUEST_CODE = 1;
    private static final int DISCOVERABLE_BT_REQUEST_CODE = 2;
    private static final int DISCOVERABLE_DURATION = 300;

    // Create a BroadcastReceiver for ACTION_FOUND
    private final BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d("broadcast", "in");

            // Whenever a remote Bluetooth device is found
            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                // Get the BluetoothDevice object from the Intent
                BluetoothDevice bluetoothDevice = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // Add the name and address to an array adapter to show in a ListView
                adapter.add(bluetoothDevice.getName() + "\n"
                        + bluetoothDevice.getAddress());
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_bluetooth_devices, container, false);

        listView = (ListView) view.findViewById(R.id.listView);

        adapter = new ArrayAdapter
                (getActivity(),android.R.layout.simple_list_item_1);
        listView.setAdapter(adapter);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        if(myBluetooth.isEnabled()) {
            adapter.clear();
            discoverDevices();
            // Make local device discoverable by other devices
            makeDiscoverable();
            Log.d("enabled", "in");
        } else {
            myBluetooth .disable();
            adapter.clear();
            Toast.makeText(getActivity(), "Bluetooth is turned off.",
                    Toast.LENGTH_SHORT).show();
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String  itemValue = (String) listView.getItemAtPosition(position);
                String MAC = itemValue.substring(itemValue.length() - 17);
                BluetoothDevice bluetoothDevice = myBluetooth.getRemoteDevice(MAC);
                // Initiate a connection request in a separate thread
                ConnectThread t = new ConnectThread(bluetoothDevice);
                t.start();
            }
        });

            getDialog().setTitle("Bluetooth Devices");

        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == ENABLE_BT_REQUEST_CODE) {

            // Bluetooth successfully enabled!
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(getActivity(), "Scanning for remote Bluetooth devices...",
                        Toast.LENGTH_SHORT).show();

                // Make local device discoverable by other devices
                makeDiscoverable();

                // To discover remote Bluetooth devices
                discoverDevices();

                // Start a thread to create a  server socket to listen
                // for connection request
                ListeningThread t = new ListeningThread();
                t.start();

            } else { // RESULT_CANCELED as user refused or failed to enable Bluetooth
                Toast.makeText(getActivity(), "Bluetooth is not enabled.",
                        Toast.LENGTH_SHORT).show();

            }
        } else if (requestCode == DISCOVERABLE_BT_REQUEST_CODE){

            if (resultCode == DISCOVERABLE_DURATION){
                Toast.makeText(getActivity(), "Your device is now discoverable by other devices for " +
                                DISCOVERABLE_DURATION + " seconds",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "Fail to enable discoverability on your device.",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    protected void discoverDevices(){
        Log.d("discover devices", "in");

        // To scan for remote Bluetooth devices
        if (myBluetooth.startDiscovery()) {
            Toast.makeText(getActivity(), "Discovering other bluetooth devices...",
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Discovery failed to start.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    protected void makeDiscoverable(){
        // Make local device discoverable
        Intent discoverableIntent = new
                Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, DISCOVERABLE_DURATION);
        startActivityForResult(discoverableIntent, DISCOVERABLE_BT_REQUEST_CODE);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Register the BroadcastReceiver for ACTION_FOUND
        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        getActivity().registerReceiver(broadcastReceiver, filter);
    }

    @Override
    public void onPause() {
        super.onPause();
        getActivity().unregisterReceiver(broadcastReceiver);
    }

    private class ListeningThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        private ListeningThread() {
            BluetoothServerSocket temp = null;
            try {
                temp = myBluetooth.listenUsingRfcommWithServiceRecord(getString(R.string.app_name), uuid);

            } catch (IOException e) {
                e.printStackTrace();
            }
            bluetoothServerSocket = temp;
        }

        public void run() {
            BluetoothSocket bluetoothSocket;
            // This will block while listening until a BluetoothSocket is returned
            // or an exception occurs
            while (true) {
                try {
                    bluetoothSocket = bluetoothServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection is accepted
                if (bluetoothSocket != null) {

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getActivity(), "Connected",Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Code to manage the connection in a separate thread
                   /*
                       manageBluetoothConnection(bluetoothSocket);
                   */

                    try {
                        bluetoothServerSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        // Cancel the listening socket and terminate the thread
        public void cancel() {
            try {
                bluetoothServerSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public class ConnectThread extends Thread {

        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            bluetoothDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                temp = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(bluetoothDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                try {
                    temp = device.createInsecureRfcommSocketToServiceRecord(uuid);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            bluetoothSocket = temp;
        }

        public void run() {
            // Cancel any discovery as it will slow down the connection
        myBluetooth.cancelDiscovery();

            try {
                // This will block until it succeeds in connecting to the device
                // through the bluetoothSocket or throws an exception
                bluetoothSocket.connect();

                getDialog().dismiss();

                bluetoothSocket.getOutputStream().write("O".getBytes());

            } catch (IOException connectException) {
                connectException.printStackTrace();
                try {
                    bluetoothSocket.close();
                } catch (IOException closeException) {
                    closeException.printStackTrace();
                }
            }

            // Code to manage the connection in a separate thread
        /*
            manageBluetoothConnection(bluetoothSocket);
        */
        }

        // Cancel an open connection and terminate the thread
        public void cancel() {
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
