package com.example.eelection;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class GetResultId extends AppCompatActivity {

    private EditText GetResultElectionID;
    private Button ShowResultBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_result_id);

        GetResultElectionID=findViewById(R.id.get_result_election_id_receive);
        ShowResultBtn=findViewById(R.id.show_result_btn);

        ShowResultBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ID=GetResultElectionID.getText().toString();
                if(!TextUtils.isEmpty(ID)) {
                    if(ID.length()==8) {
                        Intent intent = new Intent(GetResultId.this, ResultActivity.class);
                        intent.putExtra("keycode", ID);
                        startActivity(intent);
                    }else{
                        Toast.makeText(GetResultId.this, "Length of ID must be eight.", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Toast.makeText(GetResultId.this, "Please enter the Election ID.", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }
}