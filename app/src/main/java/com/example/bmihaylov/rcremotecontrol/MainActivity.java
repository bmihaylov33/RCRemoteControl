package com.example.bmihaylov.rcremotecontrol;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MotionEvent;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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

    private BluetoothAdapter BA;
//    private Set<BluetoothDevice> pairedDevices;
    boolean isClicked = true; //shows if bluetooth button is clicked

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

        accelerometer_bt.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v)  {

                    Intent intent = new Intent(MainActivity.this, AccelerometerMode.class);
                    startActivity(intent);

                Toast.makeText(getBaseContext(), "Accelerometer mode" , Toast.LENGTH_SHORT ).show();
            }
        });
    }

    @Override
    public void onBackPressed() {
        //No codes...
    }

    public void turn_bluetooth_on(View v) {
        if (!BA.isEnabled()) {
            Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(turnOn, 0);
            Toast.makeText(getApplicationContext(), "Turned on",Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Already on", Toast.LENGTH_SHORT).show();
        }
    }

    public void turn_bluetooth_off(View v) {
        BA.disable();
        Toast.makeText(getApplicationContext(), "Turned off" ,Toast.LENGTH_SHORT).show();
    }

    public void turn_light_on(View v) {
        Toast.makeText(getApplicationContext(), "Lights on", Toast.LENGTH_SHORT).show();
    }

    public void turn_light_off(View v) {
        Toast.makeText(getApplicationContext(), "Lights off",Toast.LENGTH_SHORT).show();
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
            PairedDevicesFragment editNameDialog = new PairedDevicesFragment();
            editNameDialog.show(fm, "fragment_paired_devices");
//            PairedDevicesFragment fragment = new PairedDevicesFragment();
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
}
