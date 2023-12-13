package com.example.eelection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class GiveCode extends AppCompatActivity {

    private LottieAnimationView animationView;
    private TextView electionName,electionID,ManageElect;
    String ID,Name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_code);

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                animationView.pauseAnimation();
            }
        }),1390);
//        ->{
//
//        },1);

        animationView=findViewById(R.id.animationView);
        electionName=findViewById(R.id.election_name_txt);
        electionID=findViewById(R.id.election_id_txt);
        ManageElect=findViewById(R.id.manage_election);
        Name=getIntent().getStringExtra("electionName");
        ID=getIntent().getStringExtra("keycode");

        electionName.setText(Name);
        electionID.setText(ID);

        ManageElect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GiveCode.this,ManageElectionGetId.class));
                finish();
            }
        });
    }
}