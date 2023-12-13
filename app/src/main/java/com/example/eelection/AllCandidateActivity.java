package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eelection.adapter.CandidateAdapter;
import com.example.eelection.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AllCandidateActivity extends AppCompatActivity {
    private RecyclerView candidateRV;
    private List<Candidate> list;
    private CandidateAdapter adapter;
    private ProgressBar pg1;
    private FirebaseFirestore firebaseFirestore;

    String giveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_candidate);

        candidateRV=findViewById(R.id.candidate_rv);
        pg1=findViewById(R.id.progressBar2);
        giveCode=getIntent().getStringExtra("keycode");

        firebaseFirestore= FirebaseFirestore.getInstance();

        list=new ArrayList<>();
        adapter=new CandidateAdapter(AllCandidateActivity.this,list);
        candidateRV.setLayoutManager(new LinearLayoutManager(AllCandidateActivity.this));
        candidateRV.setAdapter(adapter);

        pg1.setVisibility(View.VISIBLE);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
//            Toast.makeText(AllCandidateActivity.this, "getting user", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection(giveCode+"/election_admin/Candidates")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            Toast.makeText(AllCandidateActivity.this, ".get working", Toast.LENGTH_SHORT).show();
                            if(task.isSuccessful())
                            {
//                                Toast.makeText(AllCandidateActivity.this, "Task:"+task.isSuccessful(), Toast.LENGTH_SHORT).show();

                                for(DocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
//                                    Toast.makeText(AllCandidateActivity.this, "inside for loop", Toast.LENGTH_SHORT).show();
                                    list.add(new Candidate(
                                            snapshot.getString("name"),
                                            snapshot.getString("party"),
                                            snapshot.getString("position"),
                                            snapshot.getString("image"),
                                            snapshot.getId(),  //will get document id
                                            snapshot.getString("election_name")
                                    ));
                                }

                                pg1.setVisibility(View.GONE);

                                adapter.notifyDataSetChanged();

                            }else{
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(AllCandidateActivity.this, "Candidate not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

//        FirebaseFirestore.getInstance().collection(giveCode)
//                .document("election_admin")
//                        .get()
//                                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                        String Status=task.getResult().getString("status");
//                                        if(Status!=null){
//                                            if(Status.equals("Stop")){
//                                                Toast.makeText(AllCandidateActivity.this, "Voting is stopped.", Toast.LENGTH_SHORT).show();
//                                            }else{
//                                                FirebaseFirestore.getInstance().collection(giveCode+"/election_voted/Voters")
//                                                        .document(uid)
//                                                        .get()
//                                                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                                                            @Override
//                                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                                                String finish=task.getResult().getString("finish");
//                                                                if(finish!=null) {
//                                                                    if (finish.equals("voted")) {
//                                                                        Toast.makeText(AllCandidateActivity.this, "Your vote is already counted.", Toast.LENGTH_SHORT).show();
//                                                                        Intent intent=new Intent(AllCandidateActivity.this,ResultActivity.class);
//                                                                        intent.putExtra("keycode",giveCode);
//                                                                        startActivity(intent);
//                                                                        finish();
//                                                                    }
//                                                                }
//                                                            }
//                                                        });
//                                            }
//                                        }
//                                    }
//                                });

        FirebaseFirestore.getInstance().collection(giveCode+"/election_voted/Voters")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        String finish=task.getResult().getString("finish");
//                        Toast.makeText(AllCandidateActivity.this, "value of finish got in allcandi is:"+finish, Toast.LENGTH_SHORT).show();

//                        assert finish!=null;
                        if(finish!=null) {
                            if (finish.equals("voted")) {
                                Toast.makeText(AllCandidateActivity.this, "Your vote is already counted.", Toast.LENGTH_SHORT).show();
                                Intent intent=new Intent(AllCandidateActivity.this,ResultActivity.class);
                                intent.putExtra("keycode",giveCode);
                                startActivity(intent);
//                                startActivity(new Intent(AllCandidate.this, GetResultId.class));
                                finish();
                            }
                        }
//                        else{
//                            Toast.makeText(AllCandidateActivity.this, "finish is null", Toast.LENGTH_SHORT).show();
//                        }

                    }
                });
    }
}