package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eelection.adapter.CandidateResultAdapter;
import com.example.eelection.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ResultActivity extends AppCompatActivity {

    private RecyclerView resultRV;
    private List<Candidate> list;
    private CandidateResultAdapter adapter;
    private FirebaseFirestore firebaseFirestore;
    private TextView warningTxt;
    private ProgressBar pg1;

    String ShowResultId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        resultRV=findViewById(R.id.result_rv);
        warningTxt=findViewById(R.id.warning_text);
        pg1=findViewById(R.id.progressBar2);
        ShowResultId=getIntent().getStringExtra("keycode");
        firebaseFirestore= FirebaseFirestore.getInstance();

        list=new ArrayList<>();
        adapter=new CandidateResultAdapter(ResultActivity.this,list);
        resultRV.setLayoutManager(new LinearLayoutManager(ResultActivity.this));
        resultRV.setAdapter(adapter);

        pg1.setVisibility(View.VISIBLE);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            firebaseFirestore.collection(ShowResultId+"/election_admin/Candidates")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            Toast.makeText(ResultActivity.this, ".get working in resultactivity", Toast.LENGTH_SHORT).show();
                            if(task.isSuccessful())
                            {
//                                Toast.makeText(ResultActivity.this, "tasksuccessfull working in resultactivity", Toast.LENGTH_SHORT).show();
                                for(DocumentSnapshot snapshot: Objects.requireNonNull(task.getResult())){
                                    list.add(new Candidate(
                                            snapshot.getString("name"),
                                            snapshot.getString("party"),
                                            snapshot.getString("position"),
                                            snapshot.getString("image"),
                                            snapshot.getId(),  //will get document id
                                            snapshot.getString("election_name")
                                    ));
//                                    Toast.makeText(ResultActivity.this, ".getstring working in resultactivity", Toast.LENGTH_SHORT).show();
                                }

                                pg1.setVisibility(View.GONE);

                                adapter.notifyDataSetChanged();

                            }else{
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(ResultActivity.this, "Candidate not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();

        FirebaseFirestore.getInstance().collection(ShowResultId)
                .document("election_admin")
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        String Status = task.getResult().getString("status");
                        if (Status != null) {
                            if (Status.equals("Stop")) {
                                pg1.setVisibility(View.GONE);
                                resultRV.setVisibility(View.VISIBLE);
                                warningTxt.setVisibility(View.GONE);
                            }else{
                                pg1.setVisibility(View.GONE);
                                resultRV.setVisibility(View.GONE);
                                warningTxt.setVisibility(View.VISIBLE);
                            }

                        } else {
                            pg1.setVisibility(View.GONE);
                            warningTxt.setVisibility(View.VISIBLE);
                        }
                    }
                });

//        FirebaseFirestore.getInstance().collection(ShowResultId+"/election_voted/Voters")
//                .document(uid)
//                .get()
//                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//
//                        String finish=task.getResult().getString("finish");
////                        Toast.makeText(ResultActivity.this, "value of finish got is:"+finish, Toast.LENGTH_SHORT).show();
//
////                        assert finish!=null;
//                        if(finish!=null) {
////                            Toast.makeText(ResultActivity.this, "Finish in resultactivity:"+finish, Toast.LENGTH_SHORT).show();
//                            if (!finish.equals("voted")) {
//                                resultRV.setVisibility(View.GONE);
//                                warningTxt.setVisibility(View.VISIBLE);
//                            } else {
//                                resultRV.setVisibility(View.VISIBLE);
//                                warningTxt.setVisibility(View.GONE);
//                            }
//                        }
//                        else{
//                            warningTxt.setVisibility(View.VISIBLE);
//                        }
//                    }
//                });
    }
}