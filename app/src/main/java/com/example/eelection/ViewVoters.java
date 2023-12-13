package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.eelection.adapter.ViewVotersAdapter;
import com.example.eelection.model.Voter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ViewVoters extends AppCompatActivity {

    private RecyclerView ViewVotersRV;
    private List<Voter> list;
    private ViewVotersAdapter adapter;
    private ProgressBar pg1;
    private FirebaseFirestore firebaseFirestore;

    String giveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_voters);

        ViewVotersRV=findViewById(R.id.view_voters_rv);
        pg1=findViewById(R.id.progressBar2);

        giveCode=getIntent().getStringExtra("keycode");

        firebaseFirestore= FirebaseFirestore.getInstance();

        list=new ArrayList<>();
        adapter=new ViewVotersAdapter(ViewVoters.this,list);
        ViewVotersRV.setLayoutManager(new LinearLayoutManager(ViewVoters.this));
        ViewVotersRV.setAdapter(adapter);

        pg1.setVisibility(View.VISIBLE);

        if(FirebaseAuth.getInstance().getCurrentUser()!=null) {
//            Toast.makeText(AllCandidateActivity.this, "getting user", Toast.LENGTH_SHORT).show();
            firebaseFirestore.collection(giveCode+"/election_voted/Voters")
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
                                    list.add(new Voter(
                                            snapshot.getString("name"),
                                            snapshot.getString("email"),
                                            snapshot.getString("image")
                                    ));
                                }

                                pg1.setVisibility(View.GONE);

                                adapter.notifyDataSetChanged();

                            } else {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(ViewVoters.this, "Voter not found", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        }
    }
}