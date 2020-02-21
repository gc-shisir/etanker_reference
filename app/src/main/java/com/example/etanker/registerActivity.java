package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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
    private EditText supplierTankerCount;
    private Button supplierRegister;
    private  FirebaseAuth fAuth;
    FirebaseFirestore firestoreDB;
    String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        supplierEmail=(EditText)findViewById(R.id.supplier_email);
        supplierPassword=(EditText)findViewById(R.id.supplier_password);
        supplierRegister=(Button)findViewById(R.id.supplier_register);

        supplierRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String regEmail=supplierEmail.getText().toString();
                String regPass=supplierPassword.getText().toString();

                if(TextUtils.isEmpty(regEmail)||TextUtils.isEmpty(regPass)){
                    Toast.makeText(registerActivity.this, "Please enter the details", Toast.LENGTH_SHORT).show();
                }else{
                    registerUser(regEmail,regPass);
                }
            }
        });


    }

    private void registerUser(String email, String password) {

        fAuth.getInstance().createUserWithEmailAndPassword(email,password).addOnCompleteListener(registerActivity.this,new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    supplierName=(EditText)findViewById(R.id.supplier_name);
                    supplierPhone=(EditText)findViewById(R.id.supplier_phone);
                    supplierTankerCount=(EditText)findViewById(R.id.supplier_tanker_count);

                    String fullName=supplierName.getText().toString();
                    String phoneNumber=supplierPhone.getText().toString();
                    int count=Integer.parseInt(supplierTankerCount.getText().toString());

                    firestoreDB=FirebaseFirestore.getInstance();
                    userID=fAuth.getCurrentUser().getUid();
                    DocumentReference documentReference=firestoreDB.collection("suppliers").document(userID);
                    Map<String,Object>  supplier=new HashMap<>();
                    supplier.put("full_name",fullName);
                    supplier.put("contact_number",phoneNumber);
                    supplier.put("no_of_tankers",count);

                    documentReference.set(supplier);

                    Toast.makeText(registerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(registerActivity.this,loginActivity.class));
                }
                else{
                    Toast.makeText(registerActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}

