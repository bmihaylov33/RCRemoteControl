package com.remote.bmihaylov.rccar;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class AccelerometerActivity extends AppCompatActivity {

    TextView state_text;
    TextView distance_text;
    Button normal_mode_bt;
    Button light_bt;

    private SensorManager sensorManager;
    boolean isClicked = true; //shows if bluetooth button is clicked

    BluetoothConnection service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);

        state_text = (TextView) findViewById(R.id.state_text);
        distance_text = (TextView) findViewById(R.id.distance_text);
        normal_mode_bt = (Button) findViewById(R.id.normal_mode_bt);
        light_bt = (Button) findViewById(R.id.light_bt);

        light_bt.setBackgroundResource(R.drawable.lightbulb);

        light_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // change your light_bt background

                if (isClicked) {
                    v.setBackgroundResource(R.drawable.lightbulb_on);
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
                    v.setBackgroundResource(R.drawable.lightbulb_on);
                    turnLongLightOn(v);
                    msg("Long lights on");
                    Log.d("Lights", "on");
                }

                isClicked = !isClicked; //reverse
                return true;
            }
        });

        normal_mode_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(AccelerometerActivity.this, MainActivity.class);
                startActivity(intent);
                msg("Arrow keys mode");
            }
        });

        //Enable the button
        enableAccelerometerListening();
    }


    private void msg(String s) {
        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void motorRight() {
        //service.mConnectedThread.write("R");
    }

    public void motorLeft() {
        //service.mConnectedThread.write("L");
    }

    public void motorForward() {
        //service.mConnectedThread.write("F");
    }

    public void motorBack() {
        //service.mConnectedThread.write("B");
    }

    public void motorStop() {
        //app.mConnectedThread.write("Q");
        //service.mConnectedThread.write("Q");
    }

    public void turnShortLightOn(View v) {
        service.mConnectedThread.write("o");
    }

    public void turnLongLightOn(View v) {
        service.mConnectedThread.write("O");
    }

    public void turnLightOff(View v) {
        Log.d("Lights", "off");
        service.mConnectedThread.write("s");
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


            if(x > 4) {
                state_text.setText("BACKWARD...");
                motorBack();
            }
            else if(x < -4) {
                state_text.setText("FORWARD...");
                motorForward();
            }
            if(y > 4) {
                state_text.setText("RIGHT...");
                motorRight();
            }
            else if(y < -4) {
                state_text.setText("LEFT...");
                motorLeft();
            }
            else if(x > -4 && x < 4) {
                state_text.setText("STAY");
                motorStop();
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

       if (id == R.id.action_control) {
            Intent intent = new Intent(AccelerometerActivity.this, ControlActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_about) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

