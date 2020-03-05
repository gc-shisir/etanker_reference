package com.example.etanker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class tankerRegistration extends AppCompatActivity {

    EditText tankerNo;
    EditText tankerLicenseNumber;
    EditText tankerDriverName;
    EditText tankerDriverPhoneNumber;
    EditText tankerWaterSource;
    EditText tankerCapacity;
    Button btnRegister;
    EditText stickerColor;
    FirebaseFirestore db;
    FirebaseAuth fauth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_registration);

        fauth.getInstance();

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
                    registerTanker(tankerNumber,licenseNo,waterSource,capacity,driverName,driverContact,color);
                }

            }
        });

    }

    private void registerTanker(String tankerNumber, String licenseNo, String waterSource, String capacity, String driverName, String driverContact, String color) {
//        DocumentReference docRef =db.getInstance().collection("suppliers").document();

        String userId=fauth.getCurrentUser().getUid();
        Map<String,Object> map=new HashMap<>();
        map.put("tankerNumber",tankerNumber);
        map.put("licenseNo",licenseNo);
        map.put("source",waterSource);
        map.put("stickerColor",color);
        map.put("literCapacity",capacity);
        map.put("driverName",driverName);
        map.put("driverContact",driverContact);

        FirebaseFirestore.getInstance().collection("suppliers").document(userId).collection("tankers").add(map);
    }
}
