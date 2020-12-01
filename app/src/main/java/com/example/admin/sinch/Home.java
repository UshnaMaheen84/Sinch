package com.example.admin.sinch;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.sinch.adapters.HomeAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sinch.android.rtc.PushPair;
import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.Call;
import com.sinch.android.rtc.calling.CallClient;
import com.sinch.android.rtc.calling.CallClientListener;
import com.sinch.android.rtc.calling.CallListener;

import java.util.ArrayList;
import java.util.List;

public class Home extends AppCompatActivity {
    RecyclerView recyclerView;
    HomeAdapter myAdapter;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    SinchClient sinchClient;
    Call call;
    ArrayList<modelUser> usersArray = new ArrayList<>();
    DatabaseReference databaseReference;
    AlertDialog.Builder alertDialogIncomingCall;
    AlertDialog.Builder alertDialogCallEstablish;
    AlertDialog alertDialogCallEstablish_, alertDialogCallingUser_,alertDialogIncomingCall_;
    AlertDialog.Builder alertDialogCallingUser;

    String GetUserName, callerName;
    ImageButton CallEndBtn,  CallAcceptBtn, CallRejectBtn ;
    TextView userName, timer;
    int seconds;
    boolean running;
    String time;

    private DatabaseHelper db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        db = new DatabaseHelper(this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        myAdapter = new HomeAdapter(usersArray,this);
        recyclerView.setAdapter(myAdapter);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference().child("UserTable");

        sinchClient = Sinch.getSinchClientBuilder().context(this)
                .applicationKey("3783d3a7-c71b-4328-8917-f7e28c95d86c")
                .applicationSecret("FpU3o1fH402HA1qBF93BFA==")
                .environmentHost("clientapi.sinch.com")
                .userId(firebaseUser.getUid())
                .build();

        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();
        sinchClient.start();

        prepareData();


        sinchClient.getCallClient().addCallClientListener(new CallClientListener() {
            @Override
            public void onIncomingCall(CallClient callClient, final Call incommingcall) {

                String remoteUserId = ""+incommingcall.getRemoteUserId();
                Log.e("abc", remoteUserId);
                modelUser user = db.getUser(remoteUserId);
                String name = user.getName();
                callerName=name;

                alertDialogIncomingCall = new AlertDialog.Builder(Home.this);
                alertDialogIncomingCall.setTitle("Calling");
                alertDialogIncomingCall.setMessage(name);

                alertDialogIncomingCall.setNegativeButton("Reject", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                        incommingcall.hangup();
                    }
                });

                alertDialogIncomingCall.setPositiveButton("Accept", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        call = incommingcall;
                        call.answer();
                        call.addCallListener(new SinchCallListner());
                        Toast.makeText(Home.this, "Call Started", Toast.LENGTH_SHORT).show();
                        alertDialogIncomingCall_.dismiss();

                    }
                });

                alertDialogIncomingCall.setCancelable(false);
               alertDialogIncomingCall_= alertDialogIncomingCall.show();
            }
        });


    }

    private void prepareData() {
        databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                usersArray.clear();
                for (DataSnapshot data : dataSnapshot.getChildren()) {

                    modelUser model = data.getValue(modelUser.class);

                    usersArray.add(model);
                    }
                    myAdapter.notifyDataSetChanged();
                    Log.e("arraylistsize", "" + String.valueOf(usersArray.size()));


                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private class SinchCallListner implements CallListener{

        @Override
        public void onCallProgressing(Call call) {
            Toast.makeText(Home.this, "Ringing...", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onCallEstablished(final Call call) {
            LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
            View view = inflater.inflate(R.layout.alertdialog_call_establish, null);

            Toast.makeText(Home.this, "Call Established", Toast.LENGTH_SHORT).show();
            running = true;
            alertDialogCallEstablish = new AlertDialog.Builder(Home.this);
            alertDialogCallEstablish.setTitle("Call Established...");

            alertDialogCallEstablish.setView(view);

            userName = view.findViewById(R.id.userName);
            userName.setText(callerName);

            CallEndBtn = view.findViewById(R.id.CallEndBtn);
            CallEndBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    call.hangup();
                    alertDialogCallEstablish_.dismiss();
                }
            });

            timer = view.findViewById(R.id.timer);
            final Handler handler = new Handler();
            handler.post(new Runnable() {
                @Override public void run() {
                    int hours = seconds / 3600;
                    int minutes = (seconds % 3600) / 60;
                    int sec = seconds % 60;
                    time = String.format("%d:%02d:%02d", hours, minutes, sec);
                    Log.e("time",time);
                    timer.setText(time);
                    if(running) { seconds++; }
                    handler.postDelayed(this, 1000);
                } });


//            alertDialogCallEstablish.setNeutralButton("Hang Up", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//
//                    dialog.dismiss();
//                    call.hangup();
//                }
//            });

            alertDialogCallEstablish.setCancelable(false);

            alertDialogCallEstablish_=  alertDialogCallEstablish.show();
        }

        @Override
        public void onCallEnded(Call callEnded) {
            Toast.makeText(Home.this, "Call Ended", Toast.LENGTH_SHORT).show();
            call=null;
            callEnded.hangup();
            running = false;
            seconds = 0;

            if(!(alertDialogCallEstablish_==null)){
                alertDialogCallEstablish_.dismiss();
            }
            if(!(alertDialogCallingUser_==null)){
                alertDialogCallingUser_.dismiss();
            }
            if(!(alertDialogIncomingCall_==null)){
                alertDialogIncomingCall_.dismiss();
            }
        }

        @Override
        public void onShouldSendPushNotification(Call call, List<PushPair> list) {

        }
    }

    public void callUser(modelUser user) {

        if(call== null){
            GetUserName = user.getName();
            callerName= GetUserName;
            call = sinchClient.getCallClient().callUser(user.getId());
            call.addCallListener(new SinchCallListner());
            openCallerDialog(call);
        }
    }

    private void openCallerDialog(final Call call) {

        alertDialogCallingUser = new AlertDialog.Builder(Home.this);
        alertDialogCallingUser.setTitle("Calling...");
        alertDialogCallingUser.setMessage(GetUserName);


        alertDialogCallingUser.setNeutralButton("Hang Up", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.dismiss();
                call.hangup();
            }
        });

        alertDialogCallingUser.setCancelable(false);
        alertDialogCallingUser_= alertDialogCallingUser.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;

    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.action_logout) {

            firebaseAuth.signOut();
            startActivity(new Intent(Home.this, MainActivity.class));
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);

    }
}
