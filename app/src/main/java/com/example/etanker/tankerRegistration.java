package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class tankerRegistration extends AppCompatActivity {

    EditText tankerNo;
    EditText tankerLicenseNumber;
    EditText tankerDriverName;
    EditText tankerDriverPhoneNumber;
    EditText tankerWaterSource;
    EditText tankerCapacity;
    Button btnRegister;
    EditText stickerColor;
    String uid;
    FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_registration);

        firebaseAuth=FirebaseAuth.getInstance();

        tankerNo=(EditText)findViewById(R.id.tankerNo);
        tankerLicenseNumber=(EditText)findViewById(R.id.tankerLicenseNo);
        tankerDriverName=(EditText)findViewById(R.id.tankerDriverName);
        tankerDriverPhoneNumber=(EditText)findViewById(R.id.tankerDriverNumber);
        tankerWaterSource=(EditText)findViewById(R.id.tankerWaterSource);
        tankerCapacity=(EditText)findViewById(R.id.tankerCapacity);
        stickerColor=(EditText)findViewById(R.id.stickerColor);
        btnRegister=(Button)findViewById(R.id.btnRegister);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tankerNumber=tankerNo.getText().toString();
                String licenseNo=tankerLicenseNumber.getText().toString();
                String driverName=tankerDriverName.getText().toString();
                String driverContact=tankerDriverPhoneNumber.getText().toString();
                String waterSource=tankerWaterSource.getText().toString();
                String capacity=tankerCapacity.getText().toString();
                String color=stickerColor.getText().toString();

                if(tankerNumber.isEmpty() || licenseNo.isEmpty() || driverContact.isEmpty() || driverName.isEmpty() || waterSource.isEmpty() ||
                        capacity.isEmpty() || color.isEmpty()){
                    Toast.makeText(tankerRegistration.this, "Please enter all the details...", Toast.LENGTH_SHORT).show();
                }else{
                    if(color.toLowerCase().equals("green") || color.toLowerCase().equals("yellow") || color.toLowerCase().equals("blue")){
                        registerTanker(tankerNumber,licenseNo,waterSource,capacity,driverName,driverContact,color);
                    }else{
                        Toast.makeText(tankerRegistration.this, "Please enter proper sticker color..", Toast.LENGTH_SHORT).show();
                    }

                }

            }
        });

    }

    private void registerTanker(String tankerNumber, String licenseNo, String waterSource, String capacity, String driverName, String driverContact, String color) {
//
        try{
            if(FirebaseAuth.getInstance().getCurrentUser() != null){
                uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
            }
            Map<String,Object> map=new HashMap<>();
            map.put("tankerNumber",tankerNumber);
            map.put("licenseNo",licenseNo);
            map.put("source",waterSource);
            map.put("stickerColor",color);
            map.put("literCapacity",capacity);
            map.put("driverName",driverName);
            map.put("driverContact",driverContact);
            map.put("ownerEmail", Objects.requireNonNull(firebaseAuth.getCurrentUser()).getEmail());

            FirebaseFirestore.getInstance().collection("suppliers").document(uid).collection("tankers").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Toast.makeText(tankerRegistration.this, "Registration Successful...", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(),SupplierHome.class));
                        finish();
                    }else{
                        Toast.makeText(tankerRegistration.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });


        }catch(Exception e){
            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
            return;
        }

    }
}