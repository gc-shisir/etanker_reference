package com.example.etanker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.etanker.Model.User;
import com.example.etanker.Utils.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

import info.hoang8f.widget.FButton;

public class tankerDetailsSupplier extends AppCompatActivity {

    TextView tankerNumber,driverContact,customerAddress,customerName,customerPhone,customerEmail;
    TextView tankerStatus;
    FButton customerLocation;

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_details_supplier);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

        tankerNumber=findViewById(R.id.tankerOrderNumber);
        driverContact=findViewById(R.id.tankerDriverContact);
        customerName=findViewById(R.id.OrderCustomerName);
        customerAddress=findViewById(R.id.OrderedCustomerAddress);
        customerEmail=findViewById(R.id.OrderCustomerEmail);
        customerPhone=findViewById(R.id.OrderCustomerContact);
        tankerStatus=findViewById(R.id.tankerOrderStatus);
        customerLocation=findViewById(R.id.customerMapLocation);

        if(getIntent()!=null){
            tankerNumber.setText(getIntent().getStringExtra("tankerNumber"));
            driverContact.setText(getIntent().getStringExtra("driverContact"));

        }
        getTankerDetails();

        customerLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              fstore.collection(Common.Suppliers).document(Objects.requireNonNull(fAuth.getCurrentUser()).getUid()).collection("orderDetails").whereEqualTo("tankerNumber",tankerNumber.getText()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                  @Override
                  public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                      if (e != null) {
                          Log.w("My Activity", "Listen failed.", e);
                          return;
                      }

                      assert queryDocumentSnapshots != null;
                      for (QueryDocumentSnapshot doc:queryDocumentSnapshots){

                          String latitude= Objects.requireNonNull(doc.get("latitude")).toString();
                          String longitude= Objects.requireNonNull(doc.get("longitude")).toString();
                          Intent customerMap=new Intent(tankerDetailsSupplier.this,CustomerMapLocation.class);
                          customerMap.putExtra("latitude",latitude);
                          customerMap.putExtra("longitude",longitude);
                          startActivity(customerMap);

                      }

                  }
              });

            }
        });

    }

    private void getTankerDetails() {
        fstore.collection(Common.Suppliers).document(fAuth.getCurrentUser().getUid()).collection("orderDetails").whereEqualTo("tankerNumber",tankerNumber.getText()).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w("My Activity", "Listen failed.", e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                for (QueryDocumentSnapshot doc:queryDocumentSnapshots){
                    User user=doc.toObject(User.class);
                    customerName.setText(user.getName());
                    customerEmail.setText(user.getEmail());
                    customerPhone.setText(user.getPhone());
                    customerAddress.setText(user.getAddress());
                }
            }
        });

    }
}
