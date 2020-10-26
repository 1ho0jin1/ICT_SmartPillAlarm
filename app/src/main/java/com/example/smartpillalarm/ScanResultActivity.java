package com.example.smartpillalarm;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ScanResultActivity extends AppCompatActivity {

    private static final String TAG = "ScanResultActivity";
    private FirebaseAuth firebaseAuth;
    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();

    Context thisContext;

    String prodCode;
    String drugName;
    String drugInfo;
    int num_pill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        thisContext = this;
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();

        prodCode = extras.getString(getString(R.string.extra_key_prodCode));
        drugName = extras.getString(getString(R.string.extra_key_drugName));
        drugInfo = extras.getString(getString(R.string.extra_key_drugInfo));
        num_pill = extras.getInt(getString(R.string.extra_key_numDrug));

        final TextView textViewDrugName = findViewById(R.id.scan_result_textview_drug_name);
        final TextView textViewDrugInfo = findViewById(R.id.scan_result_textview_drug_info);

        textViewDrugName.setText(drugName);
        textViewDrugInfo.setText(drugInfo);

        Button buttonConfirm = findViewById(R.id.scan_result_button_confirm);
        Button buttonCancel = findViewById(R.id.scan_result_button_cancel);

        firebaseAuth = FirebaseAuth.getInstance();
        Log.d(TAG, "What is my ID:"+firebaseAuth.getUid());
        DatabaseReference myRef = firebaseDatabase.getReference(firebaseAuth.getUid());
        DatabaseReference pillDataRef = myRef.child("PillData").child(prodCode);

        pillDataRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String infoText = "";
                for(DataSnapshot ds:snapshot.child("item_efficacy").getChildren()){
                    infoText += ds.getValue().toString();
                    infoText += "\n";
                }
                textViewDrugName.setText(snapshot.child("item_name").getValue().toString());
                textViewDrugInfo.setText(infoText);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        buttonConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent alarmGeneratorIntent = new Intent(thisContext, AlarmGeneratorActivity.class);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_prodCode), prodCode);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugName), drugName);
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_drugInfo), drugInfo); // TODO: 여기에 전달할 효능효과 등의 내용을 넣어주세요.
                alarmGeneratorIntent.putExtra(getString(R.string.extra_key_numDrug), num_pill);
                startActivity(alarmGeneratorIntent);
                finish();
            }
        });

        buttonCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(thisContext, MainActivity.class));
                finish();
            }
        });
    }
}