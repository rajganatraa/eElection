package com.example.eelection;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditCandidate extends AppCompatActivity {

    private CircleImageView profileclick;
    private CircleImageView image;
    private EditText cand_name,cand_party,cand_post;
    private Button applychanges;
    private ProgressBar pg1;
    private FirebaseAuth mauth;
    private Uri mainuri;
    String img;
    private FirebaseFirestore firebaseFirestore;
    StorageReference reference;
    String uid=FirebaseAuth.getInstance().getCurrentUser().getUid();
    String c_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_candidate);

        profileclick=findViewById(R.id.profile_image2);
        cand_name=findViewById(R.id.candidate_name);
        cand_party=findViewById(R.id.candidate_party);
        cand_post=findViewById(R.id.candidate_position);
        image=findViewById(R.id.profile_image2);
        pg1=findViewById(R.id.progressBar2);
        applychanges=findViewById(R.id.applychange);

        String url=getIntent().getStringExtra("image");
        String name=getIntent().getStringExtra("name");
        String post=getIntent().getStringExtra("position");
        String party=getIntent().getStringExtra("party");
        String docid=getIntent().getStringExtra("id");
        String electName=getIntent().getStringExtra("electname");
        cand_name.setText(name);
        cand_post.setText(post);
        cand_party.setText(party);
        img=url;
        Glide.with(this).load(url).into(image);

        firebaseFirestore=FirebaseFirestore.getInstance();
        reference= FirebaseStorage.getInstance().getReference();
        profileclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(EditCandidate.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditCandidate.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else{
                    cropImage();
                }

            }
        });

        applychanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                c_name=cand_name.getText().toString().trim();
                String party=cand_party.getText().toString().trim();
                String post=cand_post.getText().toString().trim();
//                String userem=useremail.getText().toString().trim();
//                String userpas=userpassword.getText().toString().trim();
//                StorageReference imagepath = reference.child("image_profile").child(uid + ".jpg");
                DocumentReference docref=firebaseFirestore.collection(electName+"/election_admin/Candidates").document(docid);
//                CollectionReference docref=firebaseFirestore.collection(electName+"/election_admin/Candidates");
                Map<String, Object> map = new HashMap<>();
                map.put("name", c_name);
                map.put("party", party);
                map.put("position",post);
                map.put("image",img);
                docref.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pg1.setVisibility(View.GONE);
                        Toast.makeText(EditCandidate.this, "Candidate Profile Updated.", Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(EditCandidate.this, ManageElection.class);
                        intent.putExtra("keycode",electName);
                        startActivity(intent);
                        finish();
//                        startActivity(new Intent(EditCandidate.this,ManageElectionGetId.class));
//                        finish();
                    }
                });
            }
        });
    }

    private void cropImage() {
        ImagePicker.with(EditCandidate.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode,resultCode,data);
        Uri uri=data.getData();
        mainuri=data.getData();
        img=uri.toString();
        uploadimagetofirebase(uri);
    }
    private void uploadimagetofirebase(Uri uri) {

        pg1.setVisibility(View.VISIBLE);
        StorageReference storef = reference.child("candidate_img").child(uid+c_name+ ".jpg");
        storef.putFile(mainuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img=uri.toString();
//                        pg1.setVisibility(View.GONE);
                        Glide.with(EditCandidate.this).load(mainuri.toString()).into(image);
                        pg1.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}