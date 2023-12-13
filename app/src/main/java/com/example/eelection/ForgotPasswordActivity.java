package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    private EditText emailEdt;
    private Button reset;
    private ProgressBar pg1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        emailEdt=findViewById(R.id.email_edit);
        reset=findViewById(R.id.reset_btn);
        pg1=findViewById(R.id.progressBar2);

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String email=emailEdt.getText().toString().trim();
                if(!TextUtils.isEmpty(email))
                {
                    FirebaseAuth auth =FirebaseAuth.getInstance();
                    auth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                            {
                                pg1.setVisibility(View.GONE);
//                                Toast.makeText(ForgotPasswordActivity.this, "Email Sent Successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(ForgotPasswordActivity.this,EmailSent.class));
                                finish();
                            }else
                            {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(ForgotPasswordActivity.this, "Email Not Sent due to error OR\nUser does not exists.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else
                {
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(ForgotPasswordActivity.this, "Please Enter your Email.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}