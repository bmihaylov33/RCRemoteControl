package com.remote.bmihaylov.rccar;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {

    TextView distance_text;
    TextView state_text;

    Button left_bt;
    Button right_bt;
    Button up_bt;
    Button down_bt;
    Button accelerometer_bt;
    Button light_bt;

    ImageView battery;

    Handler bluetoothIn;

    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private StringBuilder recDataString = new StringBuilder();
    public ConnectedThread mConnectedThread;

    // SPP UUID service - this should work for most devices
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    // String for MAC address
    private static String address;

    boolean isClicked = false;
    boolean clicked = true;
    boolean arrow_clicked = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        initializeButtons();

        btAdapter = BluetoothAdapter.getDefaultAdapter();       // get Bluetooth adapter
        checkBTState();

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {                                             //if message is what we want
                    String readMessage = (String) msg.obj;                                  // msg.arg1 = bytes from connect thread
                    recDataString.append(readMessage);                                      //keep appending to string until ~
                    int endOfLineIndex = recDataString.indexOf("~");                        // determine the end-of-line
                    if (endOfLineIndex > 0) {                                               // make sure there data before ~
                        String dataInPrint = recDataString.substring(0, endOfLineIndex);    // extract string

                        int dataLength = dataInPrint.length();                              //get length of data received

                        if (recDataString.charAt(0) == '/') {                               //if it starts with '/' we know it is distance
                            String distance = dataInPrint;
                            distance = distance.substring(1, dataLength);
                            displayDistance(distance);

                            dataInPrint = " ";
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                        }
//                        else if (recDataString.charAt(0) == '#') {                          //if it starts with '#' we know it is battery percentage
//                            String percentage = dataInPrint;
//                            percentage = percentage.substring(1, dataLength);
//                            setBatteryState(Float.parseFloat(percentage));
//
//                            dataInPrint = " ";
//                            recDataString.delete(0, recDataString.length());                //clear all string data
//                        }
                        recDataString.delete(0, recDataString.length());                    //clear all string data
                    }
                }
            }
        };

    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {

        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }

    @Override
    protected void onResume() {
        super.onResume();

        //Get MAC address from DeviceListActivity via intent
        Intent intent = getIntent();

        //Get the MAC address from the DeviceListActivity via EXTRA
        address = intent.getStringExtra(DeviceActivity.EXTRA_DEVICE_ADDRESS);

        //create device and set the MAC address
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_LONG).show();
        }
        // Establish the Bluetooth socket connection.
        try {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {
                //insert code to deal with this
            }
        }
        if (btSocket != null) {
            try {
                btSocket.getOutputStream().write("O".getBytes());
            } catch (IOException e) {
                msg("Error");
            }
        }
        mConnectedThread = new ConnectedThread(btSocket);
        mConnectedThread.start();

        //I send a character when resuming.beginning transmission to check device is connected
        //If it is not an exception will be thrown in the write method and finish() will be called
//        mConnectedThread.write("x");

    }

    @Override
    public void onPause()
    {
        super.onPause();
        try {
            //Closing Bluetooth sockets when leaving activity
            btSocket.close();
        } catch (IOException e2) {
            //code
        }
    }

    //Checks that the Android device Bluetooth is available and prompts to be turned on if off
    private void checkBTState() {

        if(btAdapter==null) {
            Toast.makeText(getBaseContext(), "Device does not support bluetooth", Toast.LENGTH_LONG).show();
        } else {
            if (btAdapter.isEnabled()) {
            } else {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, 1);
            }
        }
    }

    //create new class for connect thread
    public class ConnectedThread extends Thread {
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        //creation of the connect thread
        public ConnectedThread(BluetoothSocket socket) {
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            try {
                //Create I/O streams for connection
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }


        public void run() {
            byte[] buffer = new byte[256];
            int bytes;

            // Keep looping to listen for received messages
            while (true) {
                try {
                    bytes = mmInStream.read(buffer);        	//read bytes from input buffer
                    String readMessage = new String(buffer, 0, bytes);
                    // Send the obtained bytes to the UI Activity via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }
        }
        //write method
        public void write(String input) {
            byte[] msgBuffer = input.getBytes();            //converts entered String into bytes
            try {
                mmOutStream.write(msgBuffer);                //write bytes over BT connection via output stream
            } catch (IOException e) {
                //if you cannot write, close the application
                Toast.makeText(getBaseContext(), "Connection Failure", Toast.LENGTH_LONG).show();
                finish();

            }
        }
    }

    private void initializeButtons() {

        distance_text = (TextView) findViewById(R.id.distance_text);
        state_text    = (TextView) findViewById(R.id.state_text);

        right_bt = (Button) findViewById(R.id.right_arrow_bt);
        left_bt  = (Button) findViewById(R.id.left_arrow_bt);
        up_bt    = (Button) findViewById(R.id.up_arrow_bt);
        down_bt  = (Button) findViewById(R.id.down_arrow_bt);
        light_bt = (Button) findViewById(R.id.light_bt);
        accelerometer_bt = (Button) findViewById(R.id.accelerometer_bt);

        accelerometer_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent  intent = new Intent(MainActivity.this, AccelerometerActivity.class);
                startActivity(intent);
            }
        });

        right_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrow_clicked) {
                    view.setBackgroundResource(R.drawable.arrow_right_clicked);
                    motorRight();
                    state_text.setText("RIGHT...");
                    Log.d("motors", "right");

                } else {
                    view.setBackgroundResource(R.drawable.arrow_right);
                    motorStop();
                    state_text.setText("STAY");
                }
                arrow_clicked = !arrow_clicked; //reverse
            }
        });

        left_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (arrow_clicked) {
                    view.setBackgroundResource(R.drawable.arrow_left_clicked);
                    motorLeft();
                    state_text.setText("LEFT...");
                    Log.d("motors", "left");

                } else {
                    view.setBackgroundResource(R.drawable.arrow_left);
                    motorStop();
                    state_text.setText("STAY");
                }
                arrow_clicked = !arrow_clicked; //reverse
            }
        });

        up_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked) {
                    view.setBackgroundResource(R.drawable.arrow_up_clicked);
                    motorForward();
                    state_text.setText("FORWARD...");
                    Log.d("motors", "forward");

                } else {
                    view.setBackgroundResource(R.drawable.arrow_up);
                    motorStop();
                    state_text.setText("STAY");
                }
                clicked = !clicked; //reverse
            }
        });

        down_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (clicked) {
                    view.setBackgroundResource(R.drawable.arrow_down_clicked);
                    motorBack();
                    state_text.setText("BACKWARD...");
                    Log.d("motors", "backward");

                } else {
                    view.setBackgroundResource(R.drawable.arrow_down);
                    motorStop();
                    state_text.setText("STAY");
                }
                clicked = !clicked; //reverse
            }
        });

        light_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isClicked) {
                    view.setBackgroundResource(R.drawable.lightbulb_on);
                    turnShortLightOn(view);
                    msg("Short lights on");
                    Log.d("Lights", "on");

                } else {
                    view.setBackgroundResource(R.drawable.lightbulb);
                    turnLightOff(view);
                    msg("Lights off");
                }
                isClicked = !isClicked; //reverse
            }
        });

        light_bt.setOnLongClickListener(new View.OnLongClickListener() {
            public boolean onLongClick(View view) {

                if (isClicked) {
                    view.setBackgroundResource(R.drawable.lightbulb_on);
                    turnLongLightOn(view);
                    msg("Long lights on");
                    Log.d("Lights", "long on");
                }
                isClicked = !isClicked; //reverse
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        alertMsg();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_control) {
            Intent intent = new Intent(MainActivity.this, ControlActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void alertMsg() {
        AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
        alertDialog.setTitle("Alert");
        alertDialog.setMessage("Press back again to exit.");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

    public void motorRight() {
        mConnectedThread.write("R");
    }

    public void motorLeft() {
        mConnectedThread.write("L");
    }

    public void motorForward() {
        mConnectedThread.write("F");
    }

    public void motorBack() {
        mConnectedThread.write("B");
    }

    public void motorStop() {
        mConnectedThread.write("Q");
    }

    public void turnShortLightOn(View v) {
        mConnectedThread.write("o");
    }

    public void turnLongLightOn(View v) {
        mConnectedThread.write("O");
    }

    public void turnLightOff(View v) {
        Log.d("Lights", "off");
        mConnectedThread.write("s");
    }

    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    public void displayDistance(String distance) {
        distance_text.setText(distance + " cm");
    }

    public void setBatteryState(Float percentage) {
        if(percentage > 80) {
            battery.setImageResource(R.drawable.battery_full);
        } else if(percentage <= 80 & percentage > 60) {
            battery.setImageResource(R.drawable.battery_80);
        } else if(percentage <= 60 & percentage > 40) {
            battery.setImageResource(R.drawable.battery_60);
        } else if(percentage <= 40 & percentage > 20) {
            battery.setImageResource(R.drawable.battery_40);
        } else if(percentage <= 20) {
            battery.setImageResource(R.drawable.battery_20);
//            Snackbar snackbar = Snackbar.make(view, "Battery Low!!", Snackbar.LENGTH_LONG);
//            snackbar.show();
        } else if(percentage < 5) {
            battery.setImageResource(R.drawable.battery_outline);
//            Snackbar snackbar = Snackbar.make(view, "Change batteries!", Snackbar.LENGTH_LONG);
//            snackbar.show();
        }
    }
}
