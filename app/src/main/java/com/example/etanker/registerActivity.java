package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.MalformedParameterizedTypeException;
import java.util.HashMap;
import java.util.Map;

public class registerActivity extends AppCompatActivity {

    private EditText supplierName;
    private EditText supplierEmail;
    private EditText supplierPassword;
    private EditText supplierPhone;
    private EditText supplierTankerCount,supplierLocationAddress;
    private  FirebaseAuth fAuth;
    FirebaseFirestore firestoreDB;
    String userID;
    private ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        supplierEmail=(EditText)findViewById(R.id.supplier_email);
        supplierPassword=(EditText)findViewById(R.id.supplier_password);
        Button supplierRegister = (Button) findViewById(R.id.supplier_register);
        supplierName=(EditText)findViewById(R.id.supplier_name);
        supplierPhone=(EditText)findViewById(R.id.supplier_phone);
        supplierTankerCount=(EditText)findViewById(R.id.supplier_tanker_count);
        supplierLocationAddress=(EditText)findViewById(R.id.supplierLocationAddress);

        progressDialog=new ProgressDialog(this);

        fAuth=FirebaseAuth.getInstance();

        firestoreDB=FirebaseFirestore.getInstance();


        supplierRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regEmail=supplierEmail.getText().toString();
                String regPass=supplierPassword.getText().toString();
                String regName=supplierName.getText().toString();
                String regPhone=supplierPhone.getText().toString();
                String regTankerCount=supplierTankerCount.getText().toString();
                String regAddress=supplierLocationAddress.getText().toString();

                if(TextUtils.isEmpty(regEmail)||TextUtils.isEmpty(regPass) || TextUtils.isEmpty(regName) || TextUtils.isEmpty(regPhone) ||
                    TextUtils.isEmpty(regTankerCount)||TextUtils.isEmpty(regAddress)){
                    Toast.makeText(registerActivity.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    registerUser(regEmail,regPass,regName,regPhone,regTankerCount,regAddress);
                }
            }
        });


    }

    private void registerUser(final String regEmail, String regPass, final String regName, final String regPhone, final String regTankerCount,final String regAddress) {
        fAuth.createUserWithEmailAndPassword(regEmail,regPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){

                    Map<String,Object> userInfo=new HashMap<>();
                    userInfo.put("name",regName);
                    userInfo.put("email",regEmail);
                    userInfo.put("phone_number",regPhone);
                    userInfo.put("tanker_count",regTankerCount);
                    userInfo.put("location",regAddress);
                    userID=fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference=firestoreDB.collection("suppliers").document(userID);

                    documentReference.set(userInfo);

                    Toast.makeText(registerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),loginActivity.class));
                    finish();
                }else {
                    Toast.makeText(registerActivity.this, "Registration failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }
        });
    }



}

