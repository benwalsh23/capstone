package com.example.myapplication;

import static com.example.myapplication.R.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Button button2;
    private EditText user;
    private EditText pass;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_main);

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference userCollection = db.collection("users");

        button2 = findViewById(R.id.button5);

        /**
         * open add user
         */
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddUser();
            }
        });


        button = findViewById(R.id.button);
        user = findViewById(R.id.editTextTextPersonName);
        pass = findViewById(R.id.editTextTextPersonName2);

        /**
         * when sign in button clicked retrieve entered name and password then check database if correct open info else display error message
         */
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = user.getText().toString();
                String password = pass.getText().toString();
                DocumentReference userDocument = userCollection.document(username);
                userDocument.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            return;
                        }

                        if (value != null && value.exists()) {
                            String password1 = value.getString("password");
                            if(password1 != null) {

                                if(password1.equals(password)) {
                                    openInfo();
                                }else{
                                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                    builder.setMessage("username or password is incorrect.");
                                    builder.show();
                                }
                            }else{
                                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                                builder.setMessage("username or password is incorrect.");
                                builder.show();
                            }
                        }else{
                            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                            builder.setMessage("username or password is incorrect.");
                            builder.show();
                        }
                    }
                });

            }

        });

    }

    /**
     * open info page
     */
    private void openInfo() {
        Intent intent = new Intent(this, Info.class);
        startActivity(intent);
    }

    /**
     * open add user page
     */
    private void openAddUser() {
        Intent intent = new Intent(this, AddUser.class);
        startActivity(intent);
    }

}