package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class GenerateElection extends AppCompatActivity {

    private EditText electionName;
    private Button generateBtn;
    private TextView manageBtn;
    private ProgressBar pg1;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_election);

        electionName=findViewById(R.id.election_name);
        generateBtn=findViewById(R.id.generate_btn);
        manageBtn=findViewById(R.id.manage_election);
        pg1=findViewById(R.id.progressBar2);
        firebaseFirestore=FirebaseFirestore.getInstance();

        String codes=getAlphaNumericString(8);

        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String electName=electionName.getText().toString().trim();
//                Toast.makeText(GenerateElection.this, "inside onClick", Toast.LENGTH_SHORT).show();

                if(!TextUtils.isEmpty(electName))
                {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    Map<String, Object> map = new HashMap<>();
                    map.put("admin_uid",uid);
                    map.put("ID", codes);
                    map.put("election_name", electName);
                    map.put("timestamp", FieldValue.serverTimestamp());

                    firebaseFirestore.collection(codes)
                            .document("election_admin")
                            .set(map)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pg1.setVisibility(View.GONE);
                                    Intent intent = new Intent(GenerateElection.this, GiveCode.class);
                                    intent.putExtra("electionName",electName);
                                    intent.putExtra("keycode", codes);
                                    startActivity(intent);
//                                    Toast.makeText(GenerateElection.this, "Inserted", Toast.LENGTH_SHORT).show();
//                                    Toast.makeText(GenerateElection.this, "Code:"+electionName, Toast.LENGTH_SHORT).show();
                                    // create new activity for successfull wherein they will be introduced to manage election
                                }
                            });
//                            .add(map)
//                            .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//                                @Override
//                                public void onComplete(@NonNull Task<DocumentReference> task) {
//                                    if (task.isSuccessful()) {
//                                        Toast.makeText(GenerateElection.this, "Election Created Successfully", Toast.LENGTH_SHORT).show();
////                                        Toast.makeText(GenerateElection.this, "Code:"+FieldValue.serverTimestamp()+electionName, Toast.LENGTH_SHORT).show();
//                                        Intent intent=new Intent(GenerateElection.this,GiveCode.class);
//                                        intent.putExtra("keycode",codes);
//                                        startActivity(intent);
//                                        finish();
//                                    } else {
//                                        Toast.makeText(GenerateElection.this, "Data not Stored of Candidate", Toast.LENGTH_SHORT).show();
//                                    }
//
//                                }
//                            });
                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(GenerateElection.this, "Please enter the Election Name.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        manageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(GenerateElection.this,ManageElectionGetId.class));
            }
        });
    }

    public String getAlphaNumericString(int n)
    {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";
        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(n);

        for (int i = 0; i < n; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }
        return sb.toString();
    }
}