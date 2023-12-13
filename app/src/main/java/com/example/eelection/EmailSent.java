package com.example.eelection;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

public class EmailSent extends AppCompatActivity {

    private LottieAnimationView animationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_sent);

        new Handler().postDelayed((new Runnable() {
            @Override
            public void run() {
                animationView.pauseAnimation();
            }
        }),3000);

        animationView=findViewById(R.id.animationView);
    }
}