package com.example.etanker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

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
    RadioGroup stickerColor;
    RadioButton stickerGreen;
    RadioButton stickerBlue;
    RadioButton stickerRed;
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
        stickerColor=(RadioGroup)findViewById(R.id.stickerColor);
        stickerRed=(RadioButton)findViewById(R.id.red);
        stickerBlue=(RadioButton)findViewById(R.id.blue);
        stickerGreen=(RadioButton)findViewById(R.id.green);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        
        
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                
                String colorSticker;
                String tankerNumber=tankerNo.getText().toString();
                String licenseNo=tankerLicenseNumber.getText().toString();
                String driverName=tankerDriverName.getText().toString();
                String driverContact=tankerDriverPhoneNumber.getText().toString();
                String waterSource=tankerWaterSource.getText().toString();
                String capacity=tankerCapacity.getText().toString();
                
                if (stickerBlue.isSelected()){
                    colorSticker="Blue";
                }else if(stickerGreen.isSelected()){
                    colorSticker="Green";
                }else{
                    colorSticker="red";
                }
                
                registerTanker(tankerNumber,licenseNo,waterSource,capacity,driverName,driverContact,colorSticker);
                
                
                
            }
        });
        
    }

    private void registerTanker(String tankerNumber, String licenseNo, String waterSource, String capacity, String driverName, String driverContact, String colorSticker) {
//        DocumentReference docRef =db.getInstance().collection("suppliers").document();

        Map<String,Object> map=new HashMap<>();
        map.put("tankerNumber",tankerNumber);
        map.put("licenseNo",licenseNo);
        map.put("source",waterSource);
        map.put("stickerColor",colorSticker);
        map.put("literCapacity",capacity);
        map.put("driverName",driverName);
        map.put("driverContact",driverContact);

        db.collection("suppliers").document(fauth.getCurrentUser().getUid()).collection("tankers").add(map);
    }
}
