package com.example.etanker;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class loginActivity extends AppCompatActivity {
    TextView userRegister;
    EditText loginEmail;
    EditText loginPassword;
    Button btnLogin;
    FirebaseAuth fAuth;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRegister=(TextView) findViewById(R.id.registerUser);
        loginEmail=(EditText)findViewById(R.id.login_email);
        loginPassword=(EditText)findViewById(R.id.login_password);
        btnLogin=(Button)findViewById(R.id.login_button);

        fAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(loginActivity.this, registerActivity.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=loginEmail.getText().toString();
                String password=loginPassword.getText().toString();
                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                    Toast.makeText(loginActivity.this, "Please enter email and passsword...", Toast.LENGTH_SHORT).show();
                }else{
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    login(email,password);
                }

            }
        });
    }

    private void login(String email, String password) {
        fAuth.signInWithEmailAndPassword(email,password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(loginActivity.this, "Successfully logged in...", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(loginActivity.this,supplierDashboard.class));
                progressDialog.dismiss();
                finish();
            }
        });
    }


}

