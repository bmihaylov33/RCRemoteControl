package com.example.bmihaylov.rcremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerMode extends AppCompatActivity {

    TextView state_text;
    TextView distance_text;
    Button bluetooth_bt;
    Button normal_mode_bt;
    Button light_bt;

//    private TextView textViewX;
//    private TextView textViewY;
//    private TextView textViewZ;

    private SensorManager sensorManager;
    private BluetoothAdapter BA;
    //    private Set<BluetoothDevice> pairedDevices;
    boolean isClicked = true; //shows if bluetooth button is clicked

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer_mode);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        state_text = (TextView) findViewById(R.id.state_text);
        distance_text = (TextView) findViewById(R.id.distance_text);
        bluetooth_bt = (Button) findViewById(R.id.bluetooth_bt);
        normal_mode_bt = (Button) findViewById(R.id.normal_mode_bt);
        light_bt = (Button) findViewById(R.id.light_bt);

//        textViewX = (TextView) findViewById(R.id.textViewX);
//        textViewY = (TextView) findViewById(R.id.textViewY);
//        textViewZ = (TextView) findViewById(R.id.textViewZ);

        BA = BluetoothAdapter.getDefaultAdapter();

        if(!BA.isEnabled()) {
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
            public void onClick(View v)  {
                // change your light_bt background

                if(isClicked){
                    v.setBackgroundResource(R.drawable.ic_lightbulb);
                    turn_light_on(v);
                }else{
                    v.setBackgroundResource(R.drawable.lightbulb);
                    turn_light_off(v);
                }

                isClicked = !isClicked; //reverse
            }
        });

        normal_mode_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {

                Intent intent = new Intent(AccelerometerMode.this, MainActivity.class);
                startActivity(intent);

                Toast.makeText(getBaseContext(), "Arrow keys mode" , Toast.LENGTH_SHORT ).show();
            }
        });

        //Enable the button
        enableAccelerometerListening();
    }

    @Override
    public void onBackPressed() {
        //No codes...
    }

    public void turn_bluetooth_on(View v){
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_LONG).show();
        }
    }

    public void turn_bluetooth_off(View v){
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_LONG).show();
    }

    public void turn_light_on(View v) {
        Toast.makeText(getApplicationContext(), "Lights on", Toast.LENGTH_SHORT).show();
    }

    public void turn_light_off(View v) {
        Toast.makeText(getApplicationContext(), "Lights off",Toast.LENGTH_SHORT).show();
    }

    private void enableAccelerometerListening() {
            sensorManager = (SensorManager) this.getSystemService(Context.SENSOR_SERVICE);
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                    SensorManager.SENSOR_DELAY_NORMAL);
    }

    private SensorEventListener sensorEventListener = new SensorEventListener() {

            @Override
            public void onSensorChanged(SensorEvent event) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];


                //Display the values
//                textViewX.setText(String.valueOf(x));
//                textViewY.setText(String.valueOf(y));
//                textViewZ.setText(String.valueOf(z));

                if(x > 4) {
                    state_text.setText("DOWN...");
                }
                else if(x < -4) {
                    state_text.setText("UP...");
                }

                if(y > 4) {
                    state_text.setText("RIGHT...");
                }
                else if(y < -4) {
                    state_text.setText("LEFT...");
                }

                else if(-4 < x && x < 4) {
                    state_text.setText("STAY");
                }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int i) {

            }
        };

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
            SettingsFragment fragment = new SettingsFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
        }
        else if(id == R.id.paired_devices) {
            BluetoothDevicesFragment fragment = new BluetoothDevicesFragment();
            android.support.v4.app.FragmentTransaction fragmentTransaction =
                    getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container,fragment);
            fragmentTransaction.commit();
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

}

