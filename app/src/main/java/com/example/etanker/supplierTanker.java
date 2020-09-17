package com.example.etanker;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etanker.Interface.ItemClickListener;
import com.example.etanker.Model.Tanker;
import com.example.etanker.Utils.Common;
import com.example.etanker.ViewHolder.TankerViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class supplierTanker extends AppCompatActivity {

    RecyclerView supplierTankerList;
    FirebaseFirestore fstore;

    FirestoreRecyclerAdapter<Tanker,TankerViewHolder> adapter;
    String supplierId="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_tanker);

        fstore=FirebaseFirestore.getInstance();

        supplierTankerList=findViewById(R.id.tankerList);
        supplierTankerList.hasFixedSize();
        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        supplierTankerList.setLayoutManager(layoutManager);

        supplierId = getIntent().getStringExtra("SupplierId");
        loadsupplierTanker(supplierId);
    }

    private void loadsupplierTanker(final String supplierId) {

        CollectionReference collectionReference=fstore.collection(Common.Suppliers);
        Toast.makeText(this, "tankerCheckpoint"+supplierId, Toast.LENGTH_SHORT).show();
        Query query=fstore.collectionGroup("tankers").whereEqualTo("ownerEmail",supplierId);
        FirestoreRecyclerOptions<Tanker> options=new FirestoreRecyclerOptions.Builder<Tanker>()
                .setQuery(query,Tanker.class).build();

        adapter=new FirestoreRecyclerAdapter<Tanker, TankerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TankerViewHolder tankerViewHolder, final int i, @NonNull Tanker tanker) {
                tankerViewHolder.tankerNumber.setText(tanker.getTankerNumber());
                tankerViewHolder.tankerDriverName.setText(tanker.getDriverName());


                tankerViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {

                        Intent tankerDetails=new Intent(supplierTanker.this,TankerDetails.class);
                        tankerDetails.putExtra("tankerPlateNumber",adapter.getItem(i).getTankerNumber());
                        tankerDetails.putExtra("ownerEmail",supplierId);
                        startActivity(tankerDetails);
                    }
                });
            }

            @NonNull
            @Override
            public TankerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.tanker_view,parent,false);
                return new TankerViewHolder(itemView);
            }
        };

        adapter.notifyDataSetChanged();
        adapter.startListening();
        supplierTankerList.setAdapter(adapter);

    }


}
