package com.example.triptracker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.pires.obd.commands.control.DtcNumberCommand;
import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

public class ProfileActivity extends AppCompatActivity {
    private Button btnLogout, btnReadDTCs;
    private ImageView imgProfile, imgIcon;
    private TextView tvName;


    @Override
    public void onResume() {
        super.onResume();
        //
        GoogleSignInAccount signInAccount = GoogleSignIn.getLastSignedInAccount(this);
        if(signInAccount!=null){
            Uri profilePic = signInAccount.getPhotoUrl();
            Picasso.with(this).load(profilePic).into(imgProfile);
            tvName.setText(signInAccount.getDisplayName());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        btnLogout = findViewById(R.id.btnLogOut);
        btnReadDTCs = findViewById(R.id.btnReadDTCs);
        imgProfile = findViewById(R.id.imgProfile);
        tvName = findViewById(R.id.tvName);
        imgIcon = findViewById(R.id.imgIcon);

        btnReadDTCs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TransmitDataThread.dtcFlag = Boolean.TRUE;
                //UpdateUIThread updateUIThread = new UpdateUIThread(ProfileActivity.this);
                //Thread.sleep(5000);
                //updateUIThread.showDTCReport();
                finish();
            }
        });

        btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                startActivity(intent);

            }
        });

        imgIcon.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
}