package com.example.bmihaylov.rcremotecontrol;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.os.Bundle;
import android.util.Log;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import org.askerov.dynamicgrid.DynamicGridView;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

public class ControlActivity extends Activity {

    LinearLayout target1, target2, target3;
    Button test1, test2, test3, btn1, btn2, btn3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_control);

        target1 = (LinearLayout) findViewById(R.id.target1);
        target2 = (LinearLayout) findViewById(R.id.target2);
        target3 = (LinearLayout) findViewById(R.id.target3);

        test1 = (Button) findViewById(R.id.test1);
        test2 = (Button) findViewById(R.id.test2);
        test3 = (Button) findViewById(R.id.test3);

        btn1 = (Button) findViewById(R.id.btn1);
        btn2 = (Button) findViewById(R.id.btn2);
        btn3 = (Button) findViewById(R.id.btn3);

        target1.setOnDragListener(dragListener);
        target2.setOnDragListener(dragListener);
        target3.setOnDragListener(dragListener);

        btn1.setOnLongClickListener(longClickListener);
        btn2.setOnLongClickListener(longClickListener);
        btn3.setOnLongClickListener(longClickListener);

        String json = "";

        try {

            JSONObject obj = new JSONObject(json);

            Log.d("My App", obj.toString());
            Log.d("phonetype value ", obj.getString("phonetype"));

        } catch (Throwable tx) {
            Log.e("My App", "Could not parse malformed JSON: \"" + json + "\"");
        }

    }

    View.OnLongClickListener longClickListener = new View.OnLongClickListener() {
        @Override
            public boolean onLongClick(View v) {
                ClipData data = ClipData.newPlainText("","");
                View.DragShadowBuilder myShadowBuilder = new View.DragShadowBuilder(v);
                v.startDrag(data,myShadowBuilder,v,0);
            return true;
        }
    };

    View.OnDragListener dragListener = new View.OnDragListener() {
        @Override
        public boolean onDrag(View v, DragEvent event) {

            int dragEvent = event.getAction();
            final View view = (View) event.getLocalState();

            switch (dragEvent) {
                case DragEvent.ACTION_DRAG_ENTERED:

                    break;
                case DragEvent.ACTION_DRAG_EXITED:

                    break;
                case DragEvent.ACTION_DROP:

                    if(view.getId() == R.id.btn1 && v.getId() == R.id.target1) {
                        LinearLayout oldparent = (LinearLayout) view.getParent();
                        oldparent.removeView(view);
                        LinearLayout newParent = (LinearLayout)v;
                        test1.setVisibility(View.GONE);
                        newParent.addView(view);
                        Toast.makeText(ControlActivity.this, "Dropped", Toast.LENGTH_SHORT).show();
                    }
                    else if(view.getId() == R.id.btn2 && v.getId() == R.id.target2) {
                        LinearLayout oldparent = (LinearLayout) view.getParent();
                        oldparent.removeView(view);
                        LinearLayout newParent = (LinearLayout)v;
                        test2.setVisibility(View.GONE);
                        newParent.addView(view);
                        Toast.makeText(ControlActivity.this, "Dropped", Toast.LENGTH_SHORT).show();
                    }
                    else if(view.getId() == R.id.btn3 && v.getId() == R.id.target3) {
                        LinearLayout oldparent = (LinearLayout) view.getParent();
                        oldparent.removeView(view);
                        LinearLayout newParent = (LinearLayout)v;
                        test3.setVisibility(View.GONE);
                        newParent.addView(view);
                        Toast.makeText(ControlActivity.this, "Dropped", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
            return true;
        }
    };

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
