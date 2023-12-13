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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

public class ManageElectionGetId extends AppCompatActivity {

    private EditText electionID;
    private Button viewBtn;
    private ProgressBar pg1;
    FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_election_get_id);

        electionID=findViewById(R.id.manage_election_id_receive);
        viewBtn=findViewById(R.id.manage_election_btn);
        pg1=findViewById(R.id.progressBar2);
        firebaseFirestore=FirebaseFirestore.getInstance();

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());
                String ID = electionID.getText().toString();
                if(!TextUtils.isEmpty(ID)) {
                    if(ID.length()==8) {
                        firebaseFirestore.collection(ID)
                                .document("election_admin")
                                .get()
                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            String admin = task.getResult().getString("admin_uid");
                                            if (uid.equals(admin)) {
                                                pg1.setVisibility(View.GONE);

                                                //                                            FirebaseFirestore.getInstance().collection(ID)
                                                //                                                    .document("election_admin")
                                                //                                                    .get()
                                                //                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                //                                                                               @Override
                                                //                                                                               public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                //                                                                                   String Status = task.getResult().getString("status");
                                                //                                                                                   if (Status != null) {
                                                //                                                                                       if (Status.equals("Stop")) {
                                                //                                                                                           Toast.makeText(ManageElectionGetId.this, "Sorry! Voting is stopped.", Toast.LENGTH_SHORT).show();
                                                //                                                                                       }
                                                ////                                                                                       else {
                                                ////                                                                                           Intent intent = new Intent(ManageElectionGetId.this, ManageElection.class);
                                                ////                                                                                           intent.putExtra("keycode", ID);
                                                ////                                                                                           startActivity(intent);
                                                ////                                                                                       }
                                                //                                                                                   }else {
                                                //                                                                                       Intent intent = new Intent(ManageElectionGetId.this, ManageElection.class);
                                                //                                                                                       intent.putExtra("keycode", ID);
                                                //                                                                                       startActivity(intent);
                                                //                                                                                   }
                                                //                                                                               }
                                                //                                                                           });

                                                Intent intent = new Intent(ManageElectionGetId.this, ManageElection.class);
                                                intent.putExtra("keycode", ID);
                                                startActivity(intent);
                                            } else {
                                                pg1.setVisibility(View.GONE);
                                                Toast.makeText(ManageElectionGetId.this, "Sorry you are not authorised to manage this election.", Toast.LENGTH_SHORT).show();
                                            }
                                        } else {
                                            pg1.setVisibility(View.GONE);
                                            Toast.makeText(ManageElectionGetId.this, "User not found.", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                });
                    }else{
                        pg1.setVisibility(View.GONE);
                        Toast.makeText(ManageElectionGetId.this, "Length of ID must be eight.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(ManageElectionGetId.this, "Please enter the Election ID.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}