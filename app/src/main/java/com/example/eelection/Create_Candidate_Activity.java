package com.example.eelection;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Create_Candidate_Activity extends AppCompatActivity {

    private CircleImageView candidateImg;
    private EditText candidateName,candidateParty,candidatePosition;
//    private TextView electionName;
    private Button submitBtn;
    private Uri mainUri=null;
    private ProgressBar pg1;
    StorageReference reference;
    FirebaseFirestore firebaseFirestore;

    String giveCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_candidate);

        reference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();

        candidateImg=findViewById(R.id.candidate_image);
        candidateName=findViewById(R.id.candidate_name);
        candidateParty=findViewById(R.id.candidate_party_name);
        candidatePosition=findViewById(R.id.candidate_position);
//        electionName=findViewById(R.id.election_code);
        submitBtn=findViewById(R.id.candidate_submit_btn);
        pg1=findViewById(R.id.progressBar2);

        giveCode=getIntent().getStringExtra("keycode");
//        electionName.setText(giveCode);

        candidateImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ContextCompat.checkSelfPermission(Create_Candidate_Activity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                    ActivityCompat.requestPermissions(Create_Candidate_Activity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                }
                else{
                    cropImage();
                }
            }
        });

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pg1.setVisibility(View.VISIBLE);
                String name=candidateName.getText().toString().trim();
                String party=candidateParty.getText().toString().trim();
                String position=candidatePosition.getText().toString().trim();
                String electName=giveCode.trim();

                if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(party) && !TextUtils.isEmpty(position) && !TextUtils.isEmpty(electName)&& mainUri!=null)
                {
                    String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    StorageReference imagePath = reference.child("candidate_img").child(uid+name+ ".jpg");
                    UploadTask storage = imagePath.putFile(mainUri);
//                    Toast.makeText(Create_Candidate_Activity.this, "image stored of candidate" + storage, Toast.LENGTH_SHORT).show();
                    storage.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()) {
                                imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        Map<String, Object> map = new HashMap<>();
                                        map.put("name", name);
                                        map.put("party", party);
                                        map.put("position", position);
                                        map.put("election_name", electName);
                                        map.put("image", uri.toString());
                                        map.put("timestamp", FieldValue.serverTimestamp());

                                        FirebaseFirestore.getInstance().collection(electName+"/election_admin/Candidates")
                                                .add(map)
                                                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<DocumentReference> task) {
                                                        if (task.isSuccessful()) {
                                                            pg1.setVisibility(View.GONE);
                                                            Toast.makeText(Create_Candidate_Activity.this, "Candidate is created successfully.", Toast.LENGTH_SHORT).show();
                                                            Intent intent=new Intent(Create_Candidate_Activity.this,ManageElection.class);
                                                            intent.putExtra("keycode",electName);
                                                            startActivity(intent);
//                                                            startActivity(new Intent(Create_Candidate.this, HomeActivity.class));
                                                            finish();
                                                        } else {
                                                            pg1.setVisibility(View.GONE);
                                                            Toast.makeText(Create_Candidate_Activity.this, "Candidate data not stored.", Toast.LENGTH_SHORT).show();
                                                        }

                                                    }
                                                });//need failure
                                    }
                                });
                            } else {
                                pg1.setVisibility(View.GONE);
                                Toast.makeText(Create_Candidate_Activity.this, "" + task.getException(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }else{
                    pg1.setVisibility(View.GONE);
                    Toast.makeText(Create_Candidate_Activity.this, "Please enter all the details.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void cropImage() {
        ImagePicker.with(Create_Candidate_Activity.this)
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
        candidateImg.setImageURI(resultUri);
    }
}