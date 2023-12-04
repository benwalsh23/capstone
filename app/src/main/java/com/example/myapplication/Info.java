package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ToggleButton;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class Info extends AppCompatActivity {

    private LineChart dustChart;
    private LineChart airChart;
    private LineChart qualityChart;
    private ToggleButton fanSwitch;
    private Button settings;

    private DatabaseReference dust;
    private DatabaseReference air;
    private List<Entry> entries = new ArrayList<>();
    private List<Entry> entries2 = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        /**
         * code to open settings page
         */
        settings = findViewById(R.id.button2);
        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {openSettings();}

        });
        /**
         * declare dust chart
         */
        dust = FirebaseDatabase.getInstance().getReference("Dust");
        dustChart = findViewById(R.id.lineChart);
        XAxis xAxis = dustChart.getXAxis();
        xAxis.setDrawLabels(false);
        /**
         * update dust chart with data from firebase
         */
        dust.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries.clear();
                int index = 0;
                for(DataSnapshot snapshot1: snapshot.getChildren()) {
                    String value = snapshot1.getValue().toString();
                    if(value.length()>3) {
                        value = value.substring(value.length() - 3);
                        value = value.substring(0,value.length() -1);
                        value = value.replace("=", "");
                        int value1 = Integer.parseInt(value);
                        entries.add(new Entry(index++, value1));

                    }
                    if(index > 20){
                        entries.remove(0);
                    }
                }


                LineDataSet dataSet = new LineDataSet(entries, "Dust");
                List<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);


                LineData lineData = new LineData(dataSets);
                dustChart.setData(lineData);
                dustChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error reading data: " + error.getMessage());
            }
        });
        /**
         * set the fan setting line on the dust chart
         */
        YAxis yAxis = dustChart.getAxisLeft();
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Fan/Dust Max");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int line = dataSnapshot.getValue(Integer.class);
                    yAxis.removeAllLimitLines();
                    LimitLine limitLine = new LimitLine(line, "Fan Setting");
                    limitLine.setLineColor(Color.RED);
                    limitLine.setLineWidth(1f);
                    limitLine.setTextColor(Color.BLACK);
                    yAxis.addLimitLine(limitLine);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * set the air quality chart
         */
        air = FirebaseDatabase.getInstance().getReference("Air Quality");
        airChart = findViewById(R.id.lineChart2);
        XAxis xAxis2 = airChart.getXAxis();
        xAxis2.setDrawLabels(false);
        air.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                entries2.clear();
                
                int index = 0;
                for(DataSnapshot snapshot1: snapshot.getChildren()) {
                    String value = snapshot1.getValue().toString();
                    if(value.length()>3) {
                        value = value.substring(value.length() - 3);
                        value = value.substring(0,value.length() -1);
                        value = value.replace("=", "");
                        int value1 = Integer.parseInt(value);
                        entries2.add(new Entry(index++, value1));
                    }
                    if(index > 20){
                        entries2.remove(0);
                    }
                }

                LineDataSet dataSet = new LineDataSet(entries2, "Air Quality");
                List<ILineDataSet> dataSets = new ArrayList<>();
                dataSets.add(dataSet);


                LineData lineData = new LineData(dataSets);
                airChart.setData(lineData);
                airChart.invalidate();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("FirebaseError", "Error reading data: " + error.getMessage());
            }
        });

        /**
         * set the fan setting line for the air quality chart
         */
        YAxis yAxis1 = airChart.getAxisLeft();
        DatabaseReference databaseReference2 = FirebaseDatabase.getInstance().getReference("Fan/Air Max");
        databaseReference2.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int line = dataSnapshot.getValue(Integer.class);
                    yAxis1.removeAllLimitLines();
                    LimitLine limitLine = new LimitLine(line, "Fan Setting");
                    limitLine.setLineColor(Color.RED);
                    limitLine.setLineWidth(1f);
                    limitLine.setTextColor(Color.BLACK);
                    yAxis1.addLimitLine(limitLine);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * determine whether auto is on/off and set clickable accordingly
         */
        ToggleButton fanSwitch = findViewById(R.id.toggleButton);
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fan/Control");
        DatabaseReference autoReference = FirebaseDatabase.getInstance().getReference("Fan/auto");

        autoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    int autoValue = dataSnapshot.getValue(Integer.class);

                    if (autoValue == 1) {
                        fanSwitch.setClickable(false);
                    } else {
                        fanSwitch.setClickable(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * display fan status
         */
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int fan = dataSnapshot.getValue(Integer.class);

                    if(fan == 0){
                        fanSwitch.setChecked(false);
                    }else {
                        fanSwitch.setChecked(true);
                    }
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * set fan status if auto is off
         */
        fanSwitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    if (fanSwitch.isChecked()) {
                        databaseReference.setValue(1);
                    } else {
                        databaseReference.setValue(0);
                    }
                }
        });
    }

    /**
     * code to open settings
     */
    private void openSettings() {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}