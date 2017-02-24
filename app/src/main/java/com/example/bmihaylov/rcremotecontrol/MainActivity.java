package com.example.bmihaylov.rcremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    TextView state_text;
    TextView distance_text;
    Button left_bt;
    Button right_bt;
    Button up_bt;
    Button down_bt;
    Button bluetooth_bt;
    Button accelerometer_bt;
    Button light_bt;
    ImageView battery;
    BluetoothAdapter myBluetooth = null;
    boolean isClicked = true;
    SharedPreferences isFirstRun = null;
    Handler bluetoothIn;

    final int handlerState = 0;
    private StringBuilder recDataString = new StringBuilder();
//    private ConnectedThread mConnectedThread;

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
        battery = (ImageView) findViewById(R.id.battery_level);

        left_bt.setOnTouchListener(listener);
        right_bt.setOnTouchListener(listener);
        up_bt.setOnTouchListener(listener);
        down_bt.setOnTouchListener(listener);

        isFirstRun = getSharedPreferences("com.example.bmihaylov.rcremotecontrol", MODE_PRIVATE);

        myBluetooth = BluetoothAdapter.getDefaultAdapter();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_ACL_CONNECTED);

        if (!myBluetooth.isEnabled()) {
            myBluetooth.enable();
            msg("Turned on");
        } else {
            msg("Already on");
        }

//        FragmentManager fm = getSupportFragmentManager();
//        BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
//        editNameDialog.show(fm, "fragment_bluetooth_devices");

        if (!myBluetooth.isEnabled()) {
            bluetooth_bt.setBackgroundResource(R.drawable.ic_bluetooth1);
        } else {
            bluetooth_bt.setBackgroundResource(R.drawable.ic_bluetooth);
        }

        bluetooth_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // change your bluetooth_bt background

                if (isClicked) {
                    v.setBackgroundResource(R.drawable.ic_bluetooth);
                    turn_bluetooth_on(v);
                } else {
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
                    turnShortLightOn(v);
                    msg("Short lights on");
                    Log.d("Lights", "on");

                } else {
                    v.setBackgroundResource(R.drawable.lightbulb);
                    turnLightOff(v);
                    msg("Lights off");
                }

                isClicked = !isClicked; //reverse
            }
        });

        light_bt.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View v) {
                // change your light_bt background

                if (isClicked) {
                    v.setBackgroundResource(R.drawable.ic_lightbulb);
                    turnLongLightOn(v);
                    msg("Long lights on");
                    Log.d("Lights", "on");
                }

                isClicked = !isClicked; //reverse
                return true;
            }
        });

        accelerometer_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(MainActivity.this, AccelerometerMode.class);
                startActivity(intent);

                msg("Accelerometer mode on");
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                        //if message is what we want
                    String readMessage = (String) msg.obj;                              // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                  //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                    // determine the end-of-line
                    if (endOfLineIndex > 0) {                                           // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string
                        int dataLength = dataInPrint.length();                            //get length of data received

                        if (recDataString.charAt(0) == '#') {                       //if it starts with # we know it is battery state
                            String percentage = recDataString.substring(dataLength);
                            setBatteryState(Float.valueOf(percentage));
                        }
//                        recDataString.delete(0, recDataString.length());                    //clear all string data

                        else if (recDataString.charAt(0) == '/') {                       //if it starts with / we know it is distance state
                            String distance = recDataString.substring(dataLength);
                            displayDistance(Float.valueOf(distance));
                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                    }
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (isFirstRun.getBoolean("firstrun", true)) {
            // Do first run stuff here then set 'firstrun' as false
            // using the following line to edit/commit prefs
            Intent intent = new Intent(this, IntroActivity.class);
            startActivity(intent);

            isFirstRun.edit().putBoolean("firstrun", false).commit();

//            mConnectedThread = new ConnectedThread(BluetoothDevicesFragment.bluetoothSocket);
//            mConnectedThread.start();
        }
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

    public void turnLongLightOn(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
                try {
                    Log.d("Lights", "on");
                    BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("O".getBytes());
                } catch (IOException e1) {
                    msg("Error");
                }
            }
    }

    public void turnShortLightOn(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
            try {
                Log.d("Lights", "on");
                BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("o".getBytes());
            } catch (IOException e1) {
                msg("Error");
            }
        }
    }

    public void turnLightOff(View v) {
            if (BluetoothDevicesFragment.bluetoothSocket != null) {
                try {
                    Log.d("Lights", "off");
                    BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("s".getBytes());
                } catch (IOException e) {
                    msg("Error");
                }
            }
    }

    public void setBatteryState(float percentage) {
        if(percentage > 80) {
            battery.setImageResource(R.drawable.battery_full);
        } else if(percentage > 60) {
            battery.setImageResource(R.drawable.battery_60);
        } else if(percentage > 30) {
            battery.setImageResource(R.drawable.battery_30);
        } else if(percentage > 20) {
            battery.setImageResource(R.drawable.battery_20);
        } else if(percentage < 20) {
            battery.setImageResource(R.drawable.battery_empty);
        }
    }

    public void displayDistance(float distance) {
        distance_text.setText((int) distance + "cm");
    }

    public void motorLeft(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
            try {
                Log.d("Motor", "left");
                BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("L".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public void motorRight(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
            try {
                Log.d("Motor", "right");
                BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("R".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public void motorForward(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
            try {
                Log.d("Motor", "forward");
                BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("F".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public void motorBack(View v) {
        if (BluetoothDevicesFragment.bluetoothSocket != null) {
            try {
                Log.d("Motor", "back");
                BluetoothDevicesFragment.bluetoothSocket.getOutputStream().write("B".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
    }

    public View.OnTouchListener listener = (new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            if(event.getAction() == MotionEvent.ACTION_DOWN) {
                switch (view.getId()) {
                    case R.id.left_arrow_bt:
                        state_text.setText("LEFT...");
                        motorLeft(view);
                        break;
                    case R.id.right_arrow_bt:
                        state_text.setText("RIGHT...");
                        motorRight(view);
                        break;
                    case R.id.up_arrow_bt:
                        state_text.setText("FORWARD...");
                        motorForward(view);
                        break;
                    case R.id.down_arrow_bt:
                        state_text.setText("BACKWARD...");
                        motorBack(view);
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
        }
        else if (id == R.id.paired_devices) {
            FragmentManager fm = getSupportFragmentManager();
            BluetoothDevicesFragment editNameDialog = new BluetoothDevicesFragment();
            editNameDialog.show(fm, "fragment_bluetooth_devices");
        }
        else if(id == R.id.control) {
            FragmentManager fm = getSupportFragmentManager();
            ControlCenterFragment editNameDialog = new ControlCenterFragment();
            editNameDialog.show(fm, "fragment_control");
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

//    //create new class for connect thread
//    private class ConnectedThread extends Thread {
//        private final InputStream mmInStream;
//        private final OutputStream mmOutStream;
//
//        //creation of the connect thread
//        public ConnectedThread(BluetoothSocket socket) {
//            InputStream tmpIn = null;
//            OutputStream tmpOut = null;
//
//            try {
//                //Create I/O streams for connection
//                tmpIn = socket.getInputStream();
//                tmpOut = socket.getOutputStream();
//            } catch (IOException e) { }
//
//            mmInStream = tmpIn;
//            mmOutStream = tmpOut;
//        }
//
//
//        public void run() {
//            byte[] buffer = new byte[256];
//            int bytes;
//
//            // Keep looping to listen for received messages
//            while (true) {
//                try {
//                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
//                    String readMessage = new String(buffer, 0, bytes);
//                    // Send the obtained bytes to the UI Activity via handler
//                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
//                } catch (IOException e) {
//                    break;
//                }
//            }
//        }
//        //write method
//        public void write(String input) {
//            byte[] msgBuffer = input.getBytes();           //converts entered String into bytes
//            try {
//                mmOutStream.write(msgBuffer);                //write bytes over BT connection via outstream
//            } catch (IOException e) {
//                //if you cannot write, close the application
//                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
//                finish();
//
//            }
//        }
//    }
}
