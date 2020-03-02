package com.example.etanker;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class register_customer extends AppCompatActivity {

    EditText customerName,customerPhone,customerEmail,customerAddress;
    EditText customerPassword;
    TextView swappToSupplier;
    FirebaseAuth FAuth;
    FirebaseFirestore firestoreDB;
    String UserID;
    ProgressDialog progressDialog;
    Button customerRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_customer);

        customerName=findViewById(R.id.customer_Name);
        customerEmail=findViewById(R.id.customer_Email);
        customerPassword=findViewById(R.id.customer_Password);
        customerPhone=findViewById(R.id.customer_Phone);
        swappToSupplier=findViewById(R.id.swap_Supplier);
        FAuth=FirebaseAuth.getInstance();
        firestoreDB=FirebaseFirestore.getInstance();
        progressDialog=new ProgressDialog(this);
        customerRegister=findViewById(R.id.register_Customer);
        customerAddress=findViewById(R.id.customer_Address);

        swappToSupplier.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),registerActivity.class));
            }
        });

        customerRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String emailReg=customerEmail.getText().toString().trim();
                String nameReg=customerName.getText().toString().trim();
                String passReg=customerPassword.getText().toString().trim();
                String phoneReg=customerPhone.getText().toString().trim();
                String addrReg=customerAddress.getText().toString().trim();

                if (TextUtils.isEmpty(emailReg)){
                    customerEmail.setError("Email is required");
                    return;
                }

                else if (TextUtils.isEmpty(passReg)){
                    customerPassword.setError("password is required");
                    return;
                }

                else if (passReg.length()<6){
                    customerPassword.setError("password must be atleast 7 character");
                    return;
                }

                else if (TextUtils.isEmpty(addrReg)){
                    customerAddress.setError("Address is reqiuired");
                }

                else if (TextUtils.isEmpty(phoneReg)){
                    customerPhone.setError("phone number is required");
                }

                else if (TextUtils.isEmpty(nameReg)){
                    customerName.setError("Customer name is required");
                }
                else{
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    registerCus(nameReg,emailReg,phoneReg,addrReg,passReg);
                }

            }
        });
    }

    private void registerCus(final String name, final String email,final String phone,final String address,final String password){
        FAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Map<String, Object> userInfo = new HashMap<>();
                    userInfo.put("name", name);
                    userInfo.put("email", email);
                    userInfo.put("address", address);
                    userInfo.put("phone", phone);

                    UserID = FAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firestoreDB.collection("customers").document(UserID);
                    documentReference.set(userInfo);

                    Toast.makeText(register_customer.this, "registration successful", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    startActivity(new Intent(getApplicationContext(),loginActivity.class));
                    finish();
                }
                else{
                    Toast.makeText(register_customer.this, "registration failed", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            }

        });
    }



}
