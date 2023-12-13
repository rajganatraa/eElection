package com.example.eelection;

import androidx.annotation.NonNull;
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
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfile extends AppCompatActivity {

    private CircleImageView userProfile;
    private EditText username, usernationalID;
    private TextView useremail,userpassword;
    private Button apply_change_btn;
    private ProgressBar pg1;
    private FirebaseAuth mauth;
    private Uri mainuri;
    String img;
    private FirebaseFirestore firebaseFirestore;
    StorageReference reference;

    String uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        userProfile = findViewById(R.id.profile_image2);
        username = findViewById(R.id.user_name2);
        useremail=findViewById(R.id.user_email2);
        userpassword=findViewById(R.id.user_password2);
        usernationalID = findViewById(R.id.user_national_id2);
        pg1=findViewById(R.id.progressBar2);
        apply_change_btn = findViewById(R.id.applychange);


        firebaseFirestore=FirebaseFirestore.getInstance();
        reference= FirebaseStorage.getInstance().getReference();
        firebaseFirestore.collection("Users").document(uid).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    String name = task.getResult().getString("name");
                    String nationalId = task.getResult().getString("nationalId");
                    String image = task.getResult().getString("image");
                    String email=task.getResult().getString("email");
                    String password=task.getResult().getString("password");
                    img=image;
                    username.setText(name);
                    usernationalID.setText(nationalId);
                    useremail.setText(email);
                    userpassword.setText(password);
                    Glide.with(EditProfile.this).load(image).into(userProfile);
                }
            }
        });

        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(EditProfile.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(EditProfile.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else{
                    cropImage();
                }

            }
        });

        apply_change_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String name=username.getText().toString().trim();
                String usernat=usernationalID.getText().toString().trim();
//                String userem=useremail.getText().toString().trim();
//                String userpas=userpassword.getText().toString().trim();
//                StorageReference imagepath = reference.child("image_profile").child(uid + ".jpg");
                DocumentReference docref=firebaseFirestore.collection("Users").document(uid);
                Map<String, Object> map = new HashMap<>();
                map.put("name", name);
                map.put("nationalId", usernat);
                map.put("image",img);
                docref.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        pg1.setVisibility(View.GONE);
                        Toast.makeText(EditProfile.this, "Profile Updated.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(EditProfile.this,HomeActivity.class));
                        finish();
                    }
                });

            }
        });
    }

    private void cropImage() {
        ImagePicker.with(EditProfile.this)
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
//        img=uri.toString();
//        userProfile.setImageURI(uri);
        uploadimagetofirebase(uri);
    }

    private void uploadimagetofirebase(Uri uri) {

        pg1.setVisibility(View.VISIBLE);
        StorageReference storef = reference.child("image_profile").child(uid + ".jpg");
        storef.putFile(mainuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        img=uri.toString();
                        Glide.with(EditProfile.this).load(mainuri.toString()).into(userProfile);
                        pg1.setVisibility(View.GONE);
                    }
                });
            }
        });
    }
}