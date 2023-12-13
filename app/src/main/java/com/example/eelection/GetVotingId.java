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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class GetVotingId extends AppCompatActivity {

    private EditText electionID;
    private Button viewBtn;
    private ProgressBar pg1;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting_get_id);

        electionID=findViewById(R.id.election_id_receive);
        viewBtn=findViewById(R.id.allcandidate_btn);
        pg1=findViewById(R.id.progressBar2);
        firebaseFirestore=FirebaseFirestore.getInstance();

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String ID=electionID.getText().toString();
                if(!TextUtils.isEmpty(ID)) {
                    if(ID.length()==8) {
                        firebaseFirestore.collection(ID)
                                .document("election_admin")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String Status = task.getResult().getString("status");
                                            if (Status != null) {
                                                if (Status.equals("Stop")) {
                                                    pg1.setVisibility(View.GONE);
                                                    Toast.makeText(GetVotingId.this, "Sorry! Voting is stopped.\n You can only view results.", Toast.LENGTH_SHORT).show();
                                                    Intent intent = new Intent(GetVotingId.this, ResultActivity.class);
                                                    intent.putExtra("keycode", ID);
                                                    startActivity(intent);
                                                }
                                                //                                                                                       else {
                                                //                                                                                           Intent intent = new Intent(ManageElectionGetId.this, ManageElection.class);
                                                //                                                                                           intent.putExtra("keycode", ID);
                                                //                                                                                           startActivity(intent);
                                                //                                                                                       }
                                            } else {
                                                pg1.setVisibility(View.GONE);
                                                Intent intent = new Intent(GetVotingId.this, AllCandidateActivity.class);
                                                intent.putExtra("keycode", ID);
                                                startActivity(intent);
                                            }
                                        }
                                    }
                                });
                    }else{
                        pg1.setVisibility(View.GONE);
                        Toast.makeText(GetVotingId.this, "Length of ID must be eight.", Toast.LENGTH_SHORT).show();
                    }



//                    Intent intent = new Intent(GetVotingId.this, AllCandidateActivity.class);
//                    intent.putExtra("keycode", ID);
//                    startActivity(intent);
                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(GetVotingId.this, "Please enter the Election ID.", Toast.LENGTH_SHORT).show();
                }
//                Intent intent=new Intent(GetId.this,AllCandidate.class);
//                intent.putExtra("keycode",ID);
//                startActivity(intent);

//                Intent intent_voting=new Intent(GetId.this,VotingActivity.class);
//                intent_voting.putExtra("keycode",ID);
//                startActivity(intent_voting);
            }
        });
    }
}