package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class VotingActivity extends AppCompatActivity {

    private CircleImageView image;
    private TextView name,position,party;
    private Button voteBtn;
    private ProgressBar pg1;
    private FirebaseFirestore firebaseFirestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voting);

        firebaseFirestore=FirebaseFirestore.getInstance();

        image=findViewById(R.id.image);
        name=findViewById(R.id.name);
        party=findViewById(R.id.party);
        position=findViewById(R.id.post);
        voteBtn=findViewById(R.id.vote_btn);
        pg1=findViewById(R.id.progressBar2);

        String url=getIntent().getStringExtra("image");
        String nm=getIntent().getStringExtra("name");
        String pos=getIntent().getStringExtra("position");
        String par=getIntent().getStringExtra("party");
        String id=getIntent().getStringExtra("id");
        String elect_id=getIntent().getStringExtra("keycode");


        Glide.with(this).load(url).into(image);
        name.setText(nm);
        position.setText(pos);
        party.setText(par);

        String uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();


        voteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                firebaseFirestore.collection("Users")
                        .document(uid)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
//                                    Toast.makeText(VotingActivity.this, "got details from uid", Toast.LENGTH_SHORT).show();
                                    String v_name = task.getResult().getString("name");
//                                    Toast.makeText(VotingActivity.this, "v_name:"+v_name, Toast.LENGTH_SHORT).show();
//                                    String v_nationalID = task.getResult().getString("nationalId");
                                    String v_email = task.getResult().getString("email");
                                    String v_image = task.getResult().getString("image");
                                    String finish = "voted";
//                                    Toast.makeText(VotingActivity.this, "v_name in map"+v_name, Toast.LENGTH_SHORT).show();
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("name", v_name);
                                    userMap.put("email", v_email);
                                    userMap.put("image",v_image);
                                    userMap.put("finish", finish);
                                    userMap.put("develop", getDeviceIP());
                                    userMap.put(pos, id);

                                    firebaseFirestore.collection(elect_id+"/election_voted/Voters")
                                            .document(uid)
                                            .set(userMap)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
//                                                        Toast.makeText(VotingActivity.this, "voter data stored", Toast.LENGTH_SHORT).show();
//                                    startActivity(new Intent(VotingActivity.this, ResultActivity.class));
//                                    finish();
                                                    } else {
//                                                        Toast.makeText(VotingActivity.this, "voter data not stored", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                } else {
                                    Toast.makeText(VotingActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                                }


                            }
                        });
//                String finish="voted";
//                Map<String,Object> userMap=new HashMap<>();
//                userMap.put("finish",finish);
//                userMap.put("develop",getDeviceIP());
//                userMap.put(pos,id);
//
//                firebaseFirestore.collection("Users")
//                        .document(uid).update(userMap);


                Map<String,Object> candidateMap = new HashMap<>();
                candidateMap.put("deviceIp",getDeviceIP());
                candidateMap.put("candidatePost",pos);
                candidateMap.put("timestamp", FieldValue.serverTimestamp());

                firebaseFirestore.collection(elect_id+"/election_admin/Candidates/"+id+"/Vote")
                        .document(uid)
                        .set(candidateMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    pg1.setVisibility(View.GONE);
                                    Toast.makeText(VotingActivity.this, "Voted Successfully.", Toast.LENGTH_SHORT).show();
                                    Intent intent=new Intent(VotingActivity.this,ResultActivity.class);
                                    intent.putExtra("keycode",elect_id);
                                    startActivity(intent);
                                    finish();
//                                    startActivity(new Intent(VotingActivity.this, TestActivity.class));
//                                    finish();
                                } else {
                                    pg1.setVisibility(View.GONE);
                                    Toast.makeText(VotingActivity.this, "Vote not counted.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });

    }

    private String getDeviceIP() {
        try {
            for (Enumeration<NetworkInterface> en= NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface inf=en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = inf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                    InetAddress inetAddress=enumIpAddr.nextElement();
                    if (!inetAddress.isLoopbackAddress()){
                        return inetAddress.getHostAddress().toString();
                    }
                }
            }
        }catch (Exception e)
        {
            Toast.makeText(VotingActivity.this, ""+e, Toast.LENGTH_SHORT).show();
        }
        return null;
    }
}