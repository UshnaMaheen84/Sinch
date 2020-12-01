package com.example.admin.sinch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    FirebaseAuth firebaseAuth;
    FirebaseUser currentUser;

    ArrayList<modelUser> usersArray = new ArrayList<>();
    DatabaseReference databaseReference;

    private DatabaseHelper db;
    String name, user_id, email;

    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        db = new DatabaseHelper(this);

        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserTable");


        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading");
        progressDialog.setCanceledOnTouchOutside(false);

    }

    @Override
    protected void onStart() {
        super.onStart();

        if (currentUser == null) {
            startActivity(new Intent(MainActivity.this, Login.class));
            finish();
        } else {

            prepareData();

        }
    }

    private void prepareData() {

        progressDialog.show();

        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                usersArray.clear();

                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    modelUser model = data.getValue(modelUser.class);
                    ////
                    name = model.getName();
                    user_id = model.getId();
                    email = model.getEmail();
                    Log.e("userData", "name: "+name+" id: "+user_id+" email: "+email);
                    long id = db.insertUserData(user_id, name, email);
                    if (id != 1) {
                        usersArray.add(model);
                    }
                }
                Log.e("arraylistsize", "" + String.valueOf(usersArray.size()));
                startActivity(new Intent(MainActivity.this, Home.class));
                progressDialog.dismiss();
                finish();

            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "DatabaseError: "+databaseError, Toast.LENGTH_SHORT).show();
            }
        });

    }
}
