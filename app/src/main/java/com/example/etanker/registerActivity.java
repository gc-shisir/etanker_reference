package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

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

public class registerActivity extends AppCompatActivity {

    EditText supplierName;
    EditText supplierEmail;
    EditText supplierPassword;
    EditText supplierPhone;
    EditText supplierTankerCount;
    Button supplierRegister;
    FirebaseAuth fAuth;

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
    private void registerUser(String email,String pass){
        fAuth.createUserWithEmailAndPassword(email,pass).addOnCompleteListener(registerActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText(registerActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(registerActivity.this, "Registration unsuccessful", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}