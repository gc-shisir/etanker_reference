package com.example.etanker;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class supplierDashboard extends AppCompatActivity {

    Button btnAddTanker;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dashboard);


        btnAddTanker=(Button)findViewById(R.id.btnAddTanker);
        btnAddTanker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tankerIntent=new Intent(supplierDashboard.this,tankerRegistration.class);
                startActivity(tankerIntent);
            }
        });
    }
}
