package com.example.admin.sinch;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sinch.android.rtc.Sinch;
import com.sinch.android.rtc.SinchClient;
import com.sinch.android.rtc.calling.CallClient;

public class AppToPhoneCall extends AppCompatActivity {

    Button buttonCall;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apptophone);

        buttonCall = findViewById(R.id.callBtn);

        android.content.Context context = this.getApplicationContext();
        final SinchClient sinchClient = Sinch.getSinchClientBuilder().context(context)
                .applicationKey("3783d3a7-c71b-4328-8917-f7e28c95d86c")
                .applicationSecret("FpU3o1fH402HA1qBF93BFA==")
                .environmentHost("clientapi.sinch.com")
                .userId("129488")
                .build();
        sinchClient.setSupportCalling(true);
        sinchClient.startListeningOnActiveConnection();

        buttonCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CallClient callClient = sinchClient.getCallClient();
                callClient.callPhoneNumber("");
            }
        });
    }
}
