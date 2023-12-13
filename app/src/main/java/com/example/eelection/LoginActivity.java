package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
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

import com.google.android.gms.tasks.OnCompleteListener;
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

public class LoginActivity extends AppCompatActivity {

    boolean passwordVisible;
    private EditText userPassword,userEmail;
    private TextView forgotPassword;
    private Button loginBtn;
    private ProgressBar pg1;
    private FirebaseAuth mAuth;

//    public static final String PREFERENCES="prefKey";
//    public static final String Name="nameKey";
//    public static final String Email="emailKey";
//    public static final String Password="passwordKey";
//    public static final String NationalId="nationalIdKey";
//    public static final String Image="imageKey";
//    public static final String UploadData="uploaddata";

//    SharedPreferences sharedPreferences;
//    StorageReference reference;
//    FirebaseFirestore firebaseFirestore;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
//        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

//        sharedPreferences = getApplication().getSharedPreferences(PREFERENCES,MODE_PRIVATE);
//        reference = FirebaseStorage.getInstance().getReference();
//        firebaseFirestore=FirebaseFirestore.getInstance();

        userPassword=findViewById(R.id.user_password);
        userEmail=findViewById(R.id.user_email);
        loginBtn=findViewById(R.id.login_btn);
        forgotPassword=findViewById(R.id.forgot_password);
        pg1=findViewById(R.id.progressBar2);
        mAuth=FirebaseAuth.getInstance();

        userPassword.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View view, MotionEvent event) {
                final  int Right=2;
                if(event.getAction()==MotionEvent.ACTION_UP){
                    if(event.getRawX()>=userPassword.getRight()-userPassword.getCompoundDrawables()[Right].getBounds().width()){
                        int selection=userPassword.getSelectionEnd();
                        if(passwordVisible)
                        {
                            userPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.password_not_visible,0);
                            //for hide password
                            userPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
                            passwordVisible=false;
                        }else{
                            userPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,R.drawable.password_visible,0);
                            //for hide password
                            userPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                            passwordVisible=true;
                        }
                        userPassword.setSelection(selection);
                        return true;
                    }
                }
                return false;
            }
        });

        forgotPassword.findViewById(R.id.forgot_password).setOnClickListener(view -> startActivity(new Intent(LoginActivity.this,ForgotPasswordActivity.class)));

        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                pg1.setVisibility(View.VISIBLE);

                String password=userPassword.getText().toString().trim();
                String email=userEmail.getText().toString().trim();

                if(!TextUtils.isEmpty(password) && !TextUtils.isEmpty(email) &&
                        Patterns.EMAIL_ADDRESS.matcher(email).matches())
                {
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful())
                            {
//                                Toast.makeText(LoginActivity.this, "moving to verifyEmail", Toast.LENGTH_SHORT).show();
                                verifyEmail();
                            }else
                            {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(LoginActivity.this, "Incorrect Email or Password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this,"Please Enter all Details",Toast.LENGTH_SHORT).show();
                }


            }
        });

        findViewById(R.id.dont_have_acc).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
    }

    private void verifyEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        assert user!=null;
        if(user.isEmailVerified()) {

            pg1.setVisibility(View.GONE);

            startActivity(new Intent(LoginActivity.this,HomeActivity.class));
            finish();

//            boolean bol = sharedPreferences.getBoolean(UploadData, false);//false
//            Toast.makeText(LoginActivity.this, "verifyEmail:"+bol, Toast.LENGTH_SHORT).show();
//            if (bol) {
//            //if email is verified and data is uploaded then we don't need to upload the data again
//                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                    finish();
//            } else {
//
//                String name = sharedPreferences.getString(Name, null);
//                String password = sharedPreferences.getString(Password, null);
//                String email = sharedPreferences.getString(Email, null);
//                String nationalId = sharedPreferences.getString(NationalId, null);
//                String image = sharedPreferences.getString(Image, null);
//
//                if (name != null && password != null && email != null && nationalId != null && image != null) {
//                    String uid = mAuth.getUid();
//                    StorageReference imagePath = reference.child("image_profile").child(uid + ".jpg");
//                    UploadTask storage = imagePath.putFile(Uri.parse(image));
////                    Toast.makeText(LoginActivity.this, "image stored" + storage, Toast.LENGTH_SHORT).show();
//                    storage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                                    @Override
//                                    public void onSuccess(Uri uri) {
//                                        Map<String, String> map = new HashMap<>();
//                                        map.put("name", name);
//                                        map.put("email", email);
//                                        map.put("password", password);
//                                        map.put("nationalId", nationalId);
//                                        map.put("image", uri.toString());
//                                        map.put("uid", uid);
//
//                                        FirebaseFirestore.getInstance().collection("Users")
//                                                .document(uid)
//                                                .set(map)
//                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull Task<Void> task) {
//
////                                                        Toast.makeText(LoginActivity.this, "Data Inserted", Toast.LENGTH_SHORT).show();
//
//                                                        if (task.isSuccessful()) {
//                                                            sharedPreferences = getApplicationContext().getSharedPreferences(PREFERENCES, MODE_PRIVATE);
//                                                            SharedPreferences.Editor pref = sharedPreferences.edit();
//                                                            pref.putBoolean(UploadData, true);//true was there
//                                                            pref.apply();
//
//                                                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//                                                            finish();
//                                                        } else {
//                                                            Toast.makeText(LoginActivity.this, "Data not Stored", Toast.LENGTH_SHORT).show();
//                                                        }
//
//                                                    }
//                                                });
//                                    }
//                                });
//                            } else {
//                                Toast.makeText(LoginActivity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//
//                }else{
//                    Toast.makeText(LoginActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
//                }
//            }
        }else
        {
            pg1.setVisibility(View.GONE);
            mAuth.signOut();
            Toast.makeText(LoginActivity.this, "Please Verify your Email.", Toast.LENGTH_SHORT).show();
        }
    }
}