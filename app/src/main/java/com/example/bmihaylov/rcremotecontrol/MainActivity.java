package com.example.bmihaylov.rcremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    private BluetoothDevicesFragment bluetoothDevicesFragment;

    TextView state_text;
    TextView distance_text;
    Button left_bt;
    Button right_bt;
    Button up_bt;
    Button down_bt;
    Button bluetooth_bt;
    Button accelerometer_bt;
    Button light_bt;
    BluetoothAdapter myBluetooth = null;
    //private final BluetoothSocket bluetoothSocket = null;
    private final static UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    boolean isClicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        state_text = (TextView) findViewById(R.id.state_text);
        distance_text = (TextView) findViewById(R.id.distance_text);
        left_bt = (Button) findViewById(R.id.left_arrow_bt);
        right_bt = (Button) findViewById(R.id.right_arrow_bt);
        up_bt = (Button) findViewById(R.id.up_arrow_bt);
        down_bt = (Button) findViewById(R.id.down_arrow_bt);
        bluetooth_bt = (Button) findViewById(R.id.bluetooth_bt);
        accelerometer_bt = (Button) findViewById(R.id.accelerometer_bt);
        light_bt = (Button) findViewById(R.id.light_bt);

        left_bt.setOnTouchListener(listener);
        right_bt.setOnTouchListener(listener);
        up_bt.setOnTouchListener(listener);
        down_bt.setOnTouchListener(listener);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        if(!myBluetooth.isEnabled()) {
            bluetooth_bt.setBackgroundResource(R.drawable.ic_bluetooth1);
        } else {
            bluetooth_bt.setBackgroundResource(R.drawable.ic_bluetooth);
        }

        bluetooth_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {
                // change your bluetooth_bt background

                if(isClicked){
                    v.setBackgroundResource(R.drawable.ic_bluetooth);
                    turn_bluetooth_on(v);
                }else{
                    v.setBackgroundResource(R.drawable.ic_bluetooth1);
                    turn_bluetooth_off(v);
                }

                isClicked = !isClicked; //reverse
            }
        });

        light_bt.setBackgroundResource(R.drawable.lightbulb);


        light_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // change your light_bt background

                if (isClicked) {
                    v.setBackgroundResource(R.drawable.ic_lightbulb);
                    turn_light_on(v);
                    msg("Lights on");
                    Log.d("Lights", "on");

                } else {
                    v.setBackgroundResource(R.drawable.lightbulb);
                    turn_light_off(v);
                    msg("Lights off");
                }

                isClicked = !isClicked; //reverse
            }
        });

        accelerometer_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {

            Intent intent = new Intent(MainActivity.this, AccelerometerMode.class);
            startActivity(intent);

            msg("Accelerometer mode on");
            }
        });
    }

//    // UI thread
//    private boolean ConnectSuccess = true; //if it's here, it's almost connected
////
//////    protected void onPreExecute() {
//////        progress = ProgressDialog.show(MainActivity.this, "Connecting...", "Please wait!!!");  //show a progress dialog
//////    }
////
//    protected Void doInBackground(Void... devices) {
//
//        private final BluetoothDevice bluetoothDevice;
//        //while the progress dialog is shown, the connection is done in background
//        try {
//            if (bluetoothSocket == null || !isBtConnected) {
//
//                myBluetooth = BluetoothAdapter.getDefaultAdapter();//get the mobile bluetooth device
//                //BluetoothDevice dispositivo = myBluetooth.getRemoteDevice(address);//connects to the device's address and checks if it's available
//                bluetoothSocket = bluetoothDevice.createInsecureRfcommSocketToServiceRecord(uuid);//create a RFCOMM (SPP) connection
//                BluetoothAdapter.getDefaultAdapter().cancelDiscovery();
//                bluetoothSocket.connect();//start connection
//            }
//        }
//        catch (IOException e) {
//            ConnectSuccess = false;//if the try failed, you can check the exception here
//        }
//        return null;
//    }
//
//    protected void onPostExecute(Void result) {//after the doInBackground, it checks if everything went fine
//
//        if (!ConnectSuccess) {
//            msg("Connection Failed. Is it a SPP Bluetooth? Try again.");
//            finish();
//        }
//        else {
//            msg("Connected.");
//            isBtConnected = true;
//        }
//        //progress.dismiss();
//    }

//    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//
//            if (BluetoothDevice.ACTION_ACL_CONNECTED.equals(action)) {
//                Toast.makeText(getApplicationContext(), "BT is Connected", Toast.LENGTH_SHORT).show();
//             isConnected = true;
//                //Device is connected
//            } else {
//                isConnected = false;
//                Toast.makeText(getApplicationContext(), "BT is not connected", Toast.LENGTH_SHORT).show();
//            }
//        }
//    };

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        //No codes...
    }

    public void turn_bluetooth_on(View v) {
        if (!myBluetooth.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            msg("Turned on");
        } else {
            msg("Already on");
        }
    }

    public void turn_bluetooth_off(View v) {
        myBluetooth.disable();
        msg("Turned off");
    }

    public void turn_light_on(View v) {
//        if(isConnected = true) {
            if (bluetoothDevicesFragment.bluetoothSocket != null) {
                try {
                    Log.d("Lights", "on");
                    bluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("O".toString().getBytes());
                } catch (IOException e1) {
                    msg("Error");
                }
            }
//        } else {
//            msg("Not connected");
//        }
    }

    public void turn_light_off(View v) {
//        if(isConnected = true) {
            if (bluetoothDevicesFragment.bluetoothSocket != null) {
                try {
                    Log.d("Lights", "off");
                    bluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("F".toString().getBytes());
                } catch (IOException e) {
                    msg("Error");
                }
            }
//        } else {
//            msg("Not connected");
//        }
    }

    public View.OnTouchListener listener = (new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (view.getId()) {
                    case R.id.left_arrow_bt:
                        state_text.setText("LEFT...");
                        break;
                    case R.id.right_arrow_bt:
                        state_text.setText("RIGHT...");
                        break;
                    case R.id.up_arrow_bt:
                        state_text.setText("FORWARD...");
                        break;
                    case R.id.down_arrow_bt:
                        state_text.setText("BACKWARD...");
                        break;
                }
            }
            else if(event.getAction() == MotionEvent.ACTION_UP) {
                state_text.setText("STAY");
            }
            return false;
        }
    });

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            FragmentManager fm = getSupportFragmentManager();
            SettingsFragment editNameDialog = new SettingsFragment();
            editNameDialog.show(fm, "fragment_settings");
//            SettingsFragment fragment = new SettingsFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container,fragment);
//            fragmentTransaction.commit();
        }
        else if(id == R.id.paired_devices) {
            FragmentManager fm = getSupportFragmentManager();
            BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
            editNameDialog.show(fm, "fragment_bluetooth_devices");
//            BluetoothDevicesFragment fragment = new BluetoothDevicesFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container,fragment);
//            fragmentTransaction.commit();
        }
        else if(id == R.id.control) {
            FragmentManager fm = getSupportFragmentManager();
            ControlFragment editNameDialog = new ControlFragment();
            editNameDialog.show(fm, "fragment_control");
//            ControlFragment fragment = new ControlFragment();
//            android.support.v4.app.FragmentTransaction fragmentTransaction =
//                    getSupportFragmentManager().beginTransaction();
//            fragmentTransaction.replace(R.id.fragment_container,fragment);
//            fragmentTransaction.commit();
        }
        else if(id == R.id.about_app) {
            AboutFragment fragment = new AboutFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    private class ListeningThread extends Thread {
        private final BluetoothServerSocket bluetoothServerSocket;

        public ListeningThread() {
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

                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Connected", Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Code to manage the connection in a separate thread
                   /*
                       manageBluetoothConnection(bluetoothSocket);
                   */

                    try {
                        bluetoothServerSocket.close();
                        //isBtConnected = false;
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

        private final BluetoothSocket bluetoothSocket;
        private final BluetoothDevice bluetoothDevice;

        public ConnectThread(BluetoothDevice device) {

            BluetoothSocket temp = null;
            bluetoothDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                temp = bluetoothDevice.createRfcommSocketToServiceRecord(bluetoothDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                try {
                    temp = device.createRfcommSocketToServiceRecord(uuid);
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            bluetoothSocket = temp;
        }

        public void run() {
            // Cancel any discovery as it will slow down the connection
//        myBluetooth.cancelDiscovery();

            try {
                // This will block until it succeeds in connecting to the device
                // through the bluetoothSocket or throws an exception
                bluetoothSocket.connect();
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
