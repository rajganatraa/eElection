package com.example.eelection;

import androidx.appcompat.app.AppCompatActivity;

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

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import de.hdodenhof.circleimageview.CircleImageView;

public class Candidate_Details extends AppCompatActivity {

    private CircleImageView image;
    private TextView name,position,party,editTxt,deleteTxt;
//    private Button optionsUI;
    private ProgressBar pg1;
    private FloatingActionButton editBtn,deleteBtn,fab_main;
    private FirebaseFirestore firebaseFirestore;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean isOpen=false;

    public static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_candidate_details);

        firebaseFirestore = FirebaseFirestore.getInstance();

        image = findViewById(R.id.image);
        name = findViewById(R.id.name);
        party = findViewById(R.id.party);
        position = findViewById(R.id.post);
        fab_main=findViewById(R.id.options_ui);
        editBtn=findViewById(R.id.edit_candidate);
        deleteBtn=findViewById(R.id.delete_candidate);
        editTxt=findViewById(R.id.edit_candidate_txt);
        deleteTxt=findViewById(R.id.delete_candidate_txt);
        pg1=findViewById(R.id.progressBar3);

        fabOpen= AnimationUtils.loadAnimation(Candidate_Details.this,R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(Candidate_Details.this,R.anim.fab_close);
        rotateForward= AnimationUtils.loadAnimation(Candidate_Details.this,R.anim.rotate_forward);
        rotateBackward= AnimationUtils.loadAnimation(Candidate_Details.this,R.anim.rotate_backward);

        String url = getIntent().getStringExtra("image");
        String nm = getIntent().getStringExtra("name");
        String pos = getIntent().getStringExtra("post");
        String par = getIntent().getStringExtra("party");
        String id = getIntent().getStringExtra("id");
        String elect_id=getIntent().getStringExtra("keycode");

        Glide.with(this).load(url).into(image);
        name.setText(nm);
        position.setText(pos);
        party.setText(par);

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
//                    editBtn.setVisibility(View.GONE);
//                    deleteBtn.setVisibility(View.GONE);
//                    editTxt.setVisibility(View.GONE);
//                    deleteTxt.setVisibility(View.GONE);
//                }else{
//                    optionsUI.setText("x");
//                    editBtn.setVisibility(View.VISIBLE);
//                    deleteBtn.setVisibility(View.VISIBLE);
//                    editTxt.setVisibility(View.VISIBLE);
//                    deleteTxt.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                Intent intent=new Intent(Candidate_Details.this, EditCandidate.class);
                intent.putExtra("name",nm);
                intent.putExtra("party",par);
                intent.putExtra("position",pos);
                intent.putExtra("image",url);
                intent.putExtra("id",id);
                intent.putExtra("electname",elect_id);
                startActivity(intent);
                finish();
//                Toast.makeText(Candidate_Details.this, "Edit Candidate activity should be opened", Toast.LENGTH_SHORT).show();
//                startActivity(new Intent(Candidate_Details.this,EditProfile.class));
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                AlertDialog.Builder dialog=new AlertDialog.Builder(Candidate_Details.this);
                dialog.setTitle("Are you Sure?");
                dialog.setMessage("After deleting the candidate you won't be able to access the details of the candidate.");
                dialog.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        pg1.setVisibility(View.VISIBLE); //pg1 is progress bar object
                        DocumentReference db=firebaseFirestore.collection(elect_id+"/election_admin/Candidates").document(id);
                        db.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(Candidate_Details.this, "Candidate is deleted successfully.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(Candidate_Details.this,ManageElectionGetId.class));
                                finish();
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
            }
        });
    }

    private void animateFab()
    {
        if (isOpen){
            fab_main.startAnimation(rotateBackward);
            editBtn.startAnimation(fabClose);
            deleteBtn.startAnimation(fabClose);

            editTxt.startAnimation(fabClose);
            deleteTxt.startAnimation(fabClose);

            editBtn.setClickable(false);
            deleteBtn.setClickable(false);

            isOpen=false;
        }else {
            fab_main.startAnimation(rotateForward);
            editBtn.startAnimation(fabOpen);
            deleteBtn.startAnimation(fabOpen);

            editTxt.startAnimation(fabOpen);
            deleteTxt.startAnimation(fabOpen);

            editBtn.setClickable(true);
            deleteBtn.setClickable(true);
            isOpen=true;
        }
    }
}