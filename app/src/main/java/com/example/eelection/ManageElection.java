package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.eelection.adapter.ManageElectionAdapter;
import com.example.eelection.model.Candidate;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ManageElection extends AppCompatActivity {

    private RecyclerView ManageElectionRV;
    private List<Candidate> list;
    private ManageElectionAdapter adapter;
    private FloatingActionButton createBtn,voteBtn,stopBtn,fab_main;
//    private Button optionsUI;
    private TextView createTxt,viewTxt,stopTxt;
    private ProgressBar pg1;
    private FirebaseFirestore firebaseFirestore;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean isOpen=false;

    String giveCode,viewVotersCode;
    public static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_election);

        pg1=findViewById(R.id.progressBar4);
        ManageElectionRV=findViewById(R.id.manage_election_rv);
        fab_main=findViewById(R.id.options_ui);
        createBtn=findViewById(R.id.create_candidate);
        voteBtn=findViewById(R.id.view_voters);
        stopBtn=findViewById(R.id.stop_voting);
        createTxt=findViewById(R.id.create_candidate_txt);
        viewTxt=findViewById(R.id.view_voters_txt);
        stopTxt=findViewById(R.id.stop_voting_txt);
        giveCode=getIntent().getStringExtra("keycode");
        viewVotersCode=giveCode;

        firebaseFirestore= FirebaseFirestore.getInstance();

        fabOpen= AnimationUtils.loadAnimation(ManageElection.this,R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(ManageElection.this,R.anim.fab_close);
        rotateForward= AnimationUtils.loadAnimation(ManageElection.this,R.anim.rotate_forward);
        rotateBackward= AnimationUtils.loadAnimation(ManageElection.this,R.anim.rotate_backward);

        list=new ArrayList<>();
        adapter=new ManageElectionAdapter(ManageElection.this,list);
        ManageElectionRV.setLayoutManager(new LinearLayoutManager(ManageElection.this));
        ManageElectionRV.setAdapter(adapter);

        pg1.setVisibility(View.VISIBLE);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
//            Toast.makeText(AllCandidateActivity.this, "getting user", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection(giveCode+"/election_admin/Candidates")
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        @SuppressLint("NotifyDataSetChanged")
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                            Toast.makeText(AllCandidateActivity.this, ".get working", Toast.LENGTH_SHORT).show();
                            if (task.isSuccessful()) {
//                                Toast.makeText(AllCandidateActivity.this, "Task:"+task.isSuccessful(), Toast.LENGTH_SHORT).show();
                                for (DocumentSnapshot snapshot : Objects.requireNonNull(task.getResult())) {
//                                    Toast.makeText(AllCandidateActivity.this, "inside for loop", Toast.LENGTH_SHORT).show();
                                    list.add(new Candidate(
                                            snapshot.getString("name"),
                                            snapshot.getString("party"),
                                            snapshot.getString("position"),
                                            snapshot.getString("image"),
                                            snapshot.getId(),   //will get document id
                                            snapshot.getString("election_name")
                                    ));
                                }

                                pg1.setVisibility(View.GONE);

                                adapter.notifyDataSetChanged();

                            } else {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(ManageElection.this, "Candidate not found.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
            }
        });

//        optionsUI.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                count++;
//                if(count%2==0) {
//                    optionsUI.setText("+");
////                    optionsUICancel.setVisibility(View.VISIBLE);
//                    createBtn.setVisibility(View.GONE);
//                    voteBtn.setVisibility(View.GONE);
//                    stopBtn.setVisibility(View.GONE);
//                    createTxt.setVisibility(View.GONE);
//                    viewTxt.setVisibility(View.GONE);
//                    stopTxt.setVisibility(View.GONE);
//                }else{
//                    optionsUI.setText("x");
//                    createBtn.setVisibility(View.VISIBLE);
//                    voteBtn.setVisibility(View.VISIBLE);
//                    stopBtn.setVisibility(View.VISIBLE);
//                    createTxt.setVisibility(View.VISIBLE);
//                    viewTxt.setVisibility(View.VISIBLE);
//                    stopTxt.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Intent intent=new Intent(ManageElection.this,Create_Candidate_Activity.class);
                intent.putExtra("keycode",viewVotersCode);
                startActivity(intent);
//                startActivity(new Intent(ManageElection.this,Create_Candidate_Activity.class));
                finish();
            }
        });

        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Intent intent=new Intent(ManageElection.this,ViewVoters.class);
                intent.putExtra("keycode",viewVotersCode);
                startActivity(intent);
            }
        });

        stopBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                AlertDialog.Builder dialog=new AlertDialog.Builder(ManageElection.this);
                dialog.setTitle("Are you Sure?");
                dialog.setMessage("After stoping the election no-one will be able to vote the candidates of this election.");
                dialog.setPositiveButton("Stop", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pg1.setVisibility(View.VISIBLE); //pg1 is progress bar object
                        String Status="Stop";
                        Map<String,Object> usermap=new HashMap<>();
                        usermap.put("status",Status);

                        firebaseFirestore.collection(viewVotersCode)
                                .document("election_admin")
                                .update(usermap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        pg1.setVisibility(View.GONE);
                                        Toast.makeText(ManageElection.this, "Voting is stopped.", Toast.LENGTH_SHORT).show();

                                    }
                                });
                    }
                });
                dialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                AlertDialog alertDialog=dialog.create();
                alertDialog.show();
//                String Status="Stop";
//                Map<String,Object> usermap=new HashMap<>();
//                usermap.put("status",Status);
//
//                firebaseFirestore.collection(viewVotersCode)
//                        .document("election_admin")
//                        .update(usermap)
//                        .addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//
//                    }
//                });
            }
        });
    }

    private void animateFab()
    {
        if (isOpen){
            fab_main.startAnimation(rotateBackward);
            createBtn.startAnimation(fabClose);
            voteBtn.startAnimation(fabClose);
            stopBtn.startAnimation(fabClose);
            createTxt.startAnimation(fabClose);
            viewTxt.startAnimation(fabClose);
            stopTxt.startAnimation(fabClose);
            createBtn.setClickable(false);
            voteBtn.setClickable(false);
            stopBtn.setClickable(false);
            isOpen=false;
        }else {
            fab_main.startAnimation(rotateForward);
            createBtn.startAnimation(fabOpen);
            voteBtn.startAnimation(fabOpen);
            stopBtn.startAnimation(fabOpen);
            createTxt.startAnimation(fabOpen);
            viewTxt.startAnimation(fabOpen);
            stopTxt.startAnimation(fabOpen);
            createBtn.setClickable(true);
            voteBtn.setClickable(true);
            stopBtn.setClickable(true);
            isOpen=true;
        }
    }
}