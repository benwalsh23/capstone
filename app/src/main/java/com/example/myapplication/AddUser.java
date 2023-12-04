package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.auth.User;

import java.util.HashMap;
import java.util.Map;

public class AddUser extends AppCompatActivity {


    private EditText userNameEditText;
    private EditText passwordEditText;
    private Button addUserButton;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference userCollection = db.collection("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        /**
         * declare views
         */
        userNameEditText = findViewById(R.id.editTextTextPersonName3);
        passwordEditText = findViewById(R.id.editTextTextPassword);
        addUserButton = findViewById(R.id.button6);

        /**
         * on click of add user check if user exists if true display error else add to database
         */
        addUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = userNameEditText.getText().toString();
                String password = passwordEditText.getText().toString();


                userCollection.document(username).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {

                            showAlertDialog("Username already exists. try a new username.");
                        } else {

                            addUserToFirestore(username, password);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        showAlertDialog("Failed to check username. Please try again.");
                    }
                });
            }
        });
    }

    /**
     * add user to the database
     * @param username
     * @param password
     */
    private void addUserToFirestore(String username, String password) {
        Map<String, Object> user = new HashMap<>();
        user.put("username", username);
        user.put("password", password);

        userCollection.document(username)
                .set(user)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        showAlertDialog("User added successfully!");

                    } else {
                        showAlertDialog("Failed to add user. Please try again.");

                    }
                });
    }

    /**
     * display alerts
     * @param message
     */
    private void showAlertDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(message);
        builder.show();
    }
}
