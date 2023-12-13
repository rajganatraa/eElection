package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity
{
    public static final String PREFERENCES="prefKey";
    SharedPreferences sharedPreferences;
    public static final String IsLogIn="islogin";
    private CircleImageView circleImg;
    private TextView nameTxt,nationalIdTxt,emailTxt,editTxt,deleteTxt;
    private String uid;
    private FirebaseFirestore firebaseFirestore;
    private Button startBtn,optionsUI;
    private FloatingActionButton editBtn,fab_main;//,deleteBtn;
    private ProgressBar pg1;
    Animation fabOpen,fabClose,rotateForward,rotateBackward;
    boolean isOpen=false;

    public static int count=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        firebaseFirestore=FirebaseFirestore.getInstance();
        uid= Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser().getUid());

        circleImg=findViewById(R.id.circle_image);
        nameTxt=findViewById(R.id.name);
        nationalIdTxt=findViewById(R.id.national_id);
        emailTxt=findViewById(R.id.email);
        startBtn=findViewById(R.id.candidate_create_voting);
        fab_main=findViewById(R.id.options_ui);
        editBtn=findViewById(R.id.edit_candidate);
        editTxt=findViewById(R.id.edit_candidate_txt);
//        deleteTxt=findViewById(R.id.delete_candidate_txt);
        pg1=findViewById(R.id.progressBar2);


        fabOpen= AnimationUtils.loadAnimation(HomeActivity.this,R.anim.fab_open);
        fabClose= AnimationUtils.loadAnimation(HomeActivity.this,R.anim.fab_close);
        rotateForward= AnimationUtils.loadAnimation(HomeActivity.this,R.anim.rotate_forward);
        rotateBackward= AnimationUtils.loadAnimation(HomeActivity.this,R.anim.rotate_backward);

        sharedPreferences=getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        SharedPreferences.Editor pref=sharedPreferences.edit();
        pref.putBoolean(IsLogIn,true);
        pref.apply();

        pg1.setVisibility(View.VISIBLE);
        firebaseFirestore.collection("Users")
                .document(uid)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful())
                {
                    String name=task.getResult().getString("name");
                    String nationalID=task.getResult().getString("nationalId");
                    String email=task.getResult().getString("email");
                    String image=task.getResult().getString("image");

                    nameTxt.setText(name);
                    nationalIdTxt.setText(nationalID);
                    emailTxt.setText(email);
                    Glide.with(HomeActivity.this).load(image).into(circleImg);
                    pg1.setVisibility(View.GONE);
                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(HomeActivity.this, "User not found.", Toast.LENGTH_SHORT).show();
                }

            }
        });

        startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,GetVotingId.class));

            }
        });

        fab_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
//                edit.setVisibility(View.VISIBLE);
//                delete.setVisibility(View.VISIBLE);
            }
        });

//        optionsUI.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                count++;
//                if(count%2==0) {
//                    optionsUI.setText("+");
//                    editBtn.setVisibility(View.GONE);
//                    editTxt.setVisibility(View.GONE);
//                }else{
//                    optionsUI.setText("x");
//                    editBtn.setVisibility(View.VISIBLE);
//                    editTxt.setVisibility(View.VISIBLE);
//                }
//            }
//        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                animateFab();
                startActivity(new Intent(HomeActivity.this,EditProfile.class));
                finish();;
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        int id=item.getItemId();
        SharedPreferences.Editor pref=sharedPreferences.edit();
        switch(id){
            case R.id.election:
                    startActivity(new Intent(HomeActivity.this,GenerateElection.class));
                    return true;
            case R.id.show_result:
                startActivity(new Intent(HomeActivity.this,GetResultId.class));
                return true;
            case R.id.log_out:
                FirebaseAuth.getInstance().signOut();
                pref.putBoolean(IsLogIn,false);
                pref.apply();
                startActivity(new Intent(HomeActivity.this,LoginActivity.class));
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void animateFab()
    {
        if (isOpen){
            fab_main.startAnimation(rotateBackward);
            editBtn.startAnimation(fabClose);
//            delete.startAnimation(fabClose);
            editTxt.startAnimation(fabClose);
//            tdelete.startAnimation(fabClose);
            editBtn.setClickable(false);
//            delete.setClickable(false);
            isOpen=false;
        }else {
            fab_main.startAnimation(rotateForward);
            editBtn.startAnimation(fabOpen);
//            delete.startAnimation(fabOpen);
            editTxt.startAnimation(fabOpen);
//            tdelete.startAnimation(fabOpen);
            editBtn.setClickable(true);
//            delete.setClickable(true);
            isOpen=true;
        }
    }
}