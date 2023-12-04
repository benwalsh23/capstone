package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Switch;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Settings extends AppCompatActivity {

    private Button saveButton;

    private Button defaultsetting;
    private EditText dust;
    private EditText air;

    private SeekBar airBar;

    private SeekBar dustBar;

    private Switch auto;



    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        /**
         * declare views
         */
        dust = findViewById(R.id.editTextNumber);
        saveButton = findViewById(R.id.button3);
        air = findViewById(R.id.editTextNumber2);
        dustBar = findViewById(R.id.seekBar);
        airBar = findViewById(R.id.seekBar2);
        defaultsetting = findViewById(R.id.button4);
        auto = findViewById(R.id.switch1);

        /**
         * set the auto button
         */
        DatabaseReference databaseReference3 = FirebaseDatabase.getInstance().getReference("Fan/auto");
        databaseReference3.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int autovalue = snapshot.getValue(Integer.class);
                if(autovalue == 1){
                    auto.setChecked(true);
                }else {
                    auto.setChecked(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        /**
         * display the air setting
         */
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Fan/Air Max");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int sliderValue = dataSnapshot.getValue(Integer.class);


                    airBar.setProgress(sliderValue);
                    air.setText(String.valueOf(sliderValue));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        /**
         * display the dust setting
         */
        DatabaseReference databaseReference1 = FirebaseDatabase.getInstance().getReference("Fan/Dust Max");
        databaseReference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    int sliderValue = dataSnapshot.getValue(Integer.class);


                    dustBar.setProgress(sliderValue);
                    dust.setText(String.valueOf(sliderValue));
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        /**
         * set the progress bars with settings
         */
        dustBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                dust.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        airBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                air.setText(String.valueOf(progress));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        /**
         * on save click update database with the values
         */
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int newSettingValue = Integer.parseInt(air.getText().toString());
                int newSettingValue1 = Integer.parseInt(dust.getText().toString());
                int autoNum = 0;
                if(auto.isChecked()){
                    autoNum = 1;
                }


                databaseReference.setValue(newSettingValue);
                databaseReference1.setValue(newSettingValue1);
                databaseReference3.setValue(autoNum);

            }
        });

        /**
         * on default click change the settings to the default values
         */
        defaultsetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                air.setText("2");
                dust.setText("50");
                airBar.setProgress(2);
                dustBar.setProgress(50);
                auto.setChecked(true);
            }
        });
    }
}

