package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class supplierDashboard extends AppCompatActivity {

    Button btnAddTanker;
    Button logout;
    FirebaseAuth fAuth;
    String TAG ="My activity";
    TextView name;
    TextView phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dashboard);

        logout=findViewById(R.id.logout);
        btnAddTanker=(Button)findViewById(R.id.btnAddTanker);
        fAuth=FirebaseAuth.getInstance();

        name=(TextView) findViewById(R.id.name);
        phoneNumber=(TextView) findViewById(R.id.phoneNumber);


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fAuth.signOut();
                startActivity(new Intent(supplierDashboard.this,loginActivity.class));
                Toast.makeText(supplierDashboard.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
            }
        });


        btnAddTanker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent tankerIntent=new Intent(supplierDashboard.this,tankerRegistration.class);
                startActivity(tankerIntent);
            }
        });

        Intent intent=getIntent();
        String userId=intent.getStringExtra("userId");
        Log.e("message",userId);
//        Log.e("message",FirebaseAuth.getInstance().getCurrentUser().getUid());

        DocumentReference docRef=FirebaseFirestore.getInstance().collection("suppliers").document(userId);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    DocumentSnapshot document=task.getResult();
                    if(document.exists()){
                        Log.e("successMessage","DocumentSnapshotData"+document.getData());
                    }else{
                        Log.e("failureMessage","no document");
                    }
                }
                else{
                    Log.e("exception","get failed with",task.getException());
                }
            }
        });


    }
}

