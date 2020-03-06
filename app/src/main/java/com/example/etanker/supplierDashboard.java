package com.example.etanker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class supplierDashboard extends AppCompatActivity {

    LinearLayout addTanker;
//    LinearLayout logout;
    FirebaseAuth fAuth;
    String TAG ="My activity";
    TextView name;
    TextView phoneNumber;
    FirebaseFirestore firestoredb;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_dashboard);


//        logout=(LinearLayout) findViewById(R.id.logout);
        addTanker=(LinearLayout)findViewById(R.id.addTanker);
        fAuth=FirebaseAuth.getInstance();
        firestoredb=FirebaseFirestore.getInstance();

        name=(TextView) findViewById(R.id.name);
        phoneNumber=(TextView) findViewById(R.id.phoneNumber);



        userID=fAuth.getCurrentUser().getUid();


        DocumentReference documentReference=firestoredb.collection("suppliers").document(userID);
        documentReference.addSnapshotListener(supplierDashboard.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e){
                name.setText(documentSnapshot.getString("name"));
                phoneNumber.setText(documentSnapshot.getString("phone_number"));
            }
        });



//        logout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                fAuth.signOut();
//                Toast.makeText(supplierDashboard.this, "Logged out successfully...", Toast.LENGTH_SHORT).show();
//                Intent intent1=new Intent(supplierDashboard.this,loginActivity.class);
//                startActivity(intent1);
//                finish();
//                Log.d(TAG,"terminated");
//            }
//        });

//        Log.d(TAG,"termination1");
//        addTanker.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent tankerIntent=new Intent(supplierDashboard.this,tankerRegistration.class);
//                startActivity(tankerIntent);
//            }
//        });

//        Intent intent=getIntent();
//        String userId=intent.getStringExtra("userId");
//        Log.e("message",userId);
////        Log.e("message",FirebaseAuth.getInstance().getCurrentUser().getUid());
//
//        DocumentReference docRef=FirebaseFirestore.getInstance().collection("suppliers").document(userId);
//
//        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                if(task.isSuccessful()){
//                    DocumentSnapshot document=task.getResult();
//                    if(document.exists()){
//                        Log.e("successMessage","DocumentSnapshotData"+document.getData());
//                    }else{
//                        Log.e("failureMessage","no document");
//                    }
//                }
//                else{
//                    Log.e("exception","get failed with",task.getException());
//                }
//            }
//        });


    }
    public void Logout(View view){
        Toast.makeText(this, "logged out successfully..", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(supplierDashboard.this,loginActivity.class));
        finish();
    }

    public void addTanker(View view){
        Intent tankerIntent=new Intent(supplierDashboard.this,tankerRegistration.class);
        startActivity(tankerIntent);

    }
}

