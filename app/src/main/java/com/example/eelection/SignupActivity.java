package com.example.eelection;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import com.github.dhaval2404.imagepicker.ImagePicker;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;


import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;


public class SignupActivity extends AppCompatActivity {

    boolean passwordVisible;
    private CircleImageView userProfile;
    private EditText userName,userPassword,userEmail,userNationalID,userPasswordConfirm;
    private Button signUpBtn;
    private Uri mainUri=null;
    private FirebaseAuth mAuth;
    private ProgressBar pg1;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference reference;

//    public static final String PREFERENCES="prefKey";
//    public static final String Name="nameKey";
//    public static final String Email="emailKey";
//    public static final String Password="passwordKey";
//    public static final String NationalId="nationalIdKey";
//    public static final String Image="imageKey";
//    public static final String UploadData="uploaddata";//was never here

//    SharedPreferences sharedPreferences;

    String name,password,email,nationalID,passwordconfirm;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        sharedPreferences= getApplicationContext().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
        reference = storage.getReference();

        findViewById(R.id.have_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });


        userProfile=findViewById(R.id.profile_image);
        userName=findViewById(R.id.user_name);
        userPassword=findViewById(R.id.user_password);
        userPasswordConfirm=findViewById(R.id.user_password_confirm);
        userEmail=findViewById(R.id.user_email);
        userNationalID=findViewById(R.id.user_national_id);
        signUpBtn=findViewById(R.id.signup_btn);
        pg1=findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();


        userProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(SignupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(SignupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else{
                    cropImage();
                }
            }
        });

        userPasswordConfirm.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final  int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=userPasswordConfirm.getRight()-userPasswordConfirm.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=userPasswordConfirm.getSelectionEnd();
                        if(passwordVisible)
                        {
                            userPasswordConfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.password_not_visible,0);
                            //for hide password
                            userPasswordConfirm.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            userPasswordConfirm.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.password_visible,0);
                            //for hide password
                            userPasswordConfirm.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        userPasswordConfirm.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });




        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                 name=userName.getText().toString().trim();
                 password=userPassword.getText().toString().trim();
                 email=userEmail.getText().toString().trim();
                 nationalID=userNationalID.getText().toString().trim();
                 passwordconfirm=userPasswordConfirm.getText().toString().trim();

                if (TextUtils.equals(passwordconfirm,password)) {
                    if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) &&
                            Patterns.EMAIL_ADDRESS.matcher(email).matches() && !TextUtils.isEmpty(nationalID) && mainUri!=null)
                    {
                        createUser(email,password);
                    }else{
                        pg1.setVisibility(View.GONE);
                        Toast.makeText(SignupActivity.this,"Please Enter all the Details.",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "Confirm Password and Password do not match.", Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private void cropImage() {
        ImagePicker.with(SignupActivity.this)
                .crop()	    			//Crop image(Optional), Check Customization for more option
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data)
    {
        super.onActivityResult(requestCode,resultCode,data);
        Uri resultUri=data.getData();
        mainUri=data.getData();
        userProfile.setImageURI(resultUri);
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    String uid = mAuth.getUid();
                    StorageReference imagePath = reference.child("image_profile").child(uid + ".jpg");
                    UploadTask storage = imagePath.putFile(mainUri);
//                    Toast.makeText(LoginActivity.this, "image stored" + storage, Toast.LENGTH_SHORT).show();
                    storage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, String> map = new HashMap<>();
                                        map.put("name", userName.getText().toString());
                                        map.put("email", userEmail.getText().toString());
                                        map.put("password", userPassword.getText().toString());
                                        map.put("nationalId", userNationalID.getText().toString());
                                        map.put("image", uri.toString());
                                        map.put("uid", uid);

                                        FirebaseFirestore.getInstance().collection("Users")
                                                .document(uid)
                                                .set(map)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {

//                                                        Toast.makeText(LoginActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();

                                                        if (task.isSuccessful()) {
                                                            pg1.setVisibility(View.GONE);
                                                            Toast.makeText(SignupActivity.this, "User Created Successfully.", Toast.LENGTH_SHORT).show();
                                                            FirebaseUser user = mAuth.getCurrentUser();
                                                            if (user != null) {
                                                                user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                                                    @Override
                                                                    public void onComplete(@NonNull Task<Void> task) {
                                                                        if (task.isSuccessful()) {
                                                                            Toast.makeText(SignupActivity.this, "Email Sent Successfully.", Toast.LENGTH_SHORT).show();
                                                                            FirebaseAuth.getInstance().signOut();
                                                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                                                            finish();
                                                                        } else {
                                                                            mAuth.signOut();
                                                                        }
                                                                        finish();
                                                                    }
                                                                });
                                                            }
//                                                            startActivity(new Intent(SignupActivity.this, LoginActivity.class));
//                                                            finish();
                                                        } else {
                                                            pg1.setVisibility(View.GONE);
                                                            Toast.makeText(SignupActivity.this, "Data not Stored.", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });
                                    }
                                });

//                    Toast.makeText(SignupActivity.this, "User Created Successfully", Toast.LENGTH_SHORT).show();
//                    verifyEmail();
                            } else {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(SignupActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }


                        }

                    });
//                            .addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(SignupActivity.this, "Something went wrong...", Toast.LENGTH_SHORT).show();
//                        }
//                    });
                }else {
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(SignupActivity.this, "User already exists.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

//    private void verifyEmail() {
//        FirebaseUser user = mAuth.getCurrentUser();
//        if(user!=null)
//        {
//            user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
//                @Override
//                public void onComplete(@NonNull Task<Void> task) {
//                    if(task.isSuccessful())
//                    {
////                        SharedPreferences.Editor pref = sharedPreferences.edit();
////                        pref.putString(Name,name);
////                        pref.putString(Password,password);
////                        pref.putString(Email,email);
////                        pref.putString(NationalId,nationalID);
////                        pref.putString(Image,mainUri.toString());
////                        pref.putBoolean(UploadData, false);
////                        pref.apply();
//
//                        //mail sent successfully
//                        Toast.makeText(SignupActivity.this, "Email Sent Successfully", Toast.LENGTH_SHORT).show();
//                        FirebaseAuth.getInstance().signOut();
//                        startActivity(new Intent(SignupActivity.this,LoginActivity.class));
//                    }else
//                    {
//                        mAuth.signOut();
//                    }
//                    finish();
//                }
//            });
//        }
//    }


}