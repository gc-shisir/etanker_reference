package com.example.etanker;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.etanker.Interface.ItemClickListener;
import com.example.etanker.Model.Tanker;
import com.example.etanker.Service.ListenOrder;
import com.example.etanker.Utils.Common;
import com.example.etanker.ViewHolder.TankerViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Objects;

public class SupplierHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    DocumentReference supplierData;
    String UserId;
    Intent intent;

    RecyclerView supplierTankerList;
    FirestoreRecyclerAdapter<Tanker, TankerViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_home);
        Toolbar toolbar = findViewById(R.id.Suppliertoolbar);
        setSupportActionBar(toolbar);

        fAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        DrawerLayout drawer = findViewById(R.id.supplier_drawer_layout);
        ActionBarDrawerToggle toggle=new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.open,R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.supplierNavView);
        navigationView.setNavigationItemSelectedListener(this);

        UserId= Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        supplierData=fstore.collection(Common.Suppliers).document(UserId);

        View headerView=navigationView.getHeaderView(0);
        final TextView SupplierName=headerView.findViewById(R.id.SupplierName);
        final TextView SupplierEmailAddress=headerView.findViewById(R.id.SupplierEmailAddress);

        assert supplierData!=null;
        supplierData.addSnapshotListener(this,new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent( DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {

                if (e!=null){
                    Log.d("My Activity","Error:"+e.getMessage());
                }else{
                    SupplierName.setText(Objects.requireNonNull(documentSnapshot.get(Common.Name)).toString());
                    SupplierEmailAddress.setText(Objects.requireNonNull(documentSnapshot.get(Common.Email)).toString());
                }
            }
        });

        supplierTankerList=findViewById(R.id.SupplierTankerList);
        supplierTankerList.hasFixedSize();

        RecyclerView.LayoutManager layoutManager=new LinearLayoutManager(this);
        supplierTankerList.setLayoutManager(layoutManager);

        loadMytanker();

        //Register the service
        Intent service=new Intent(SupplierHome.this, ListenOrder.class);
        startService(service);

    }

    private void loadMytanker() {
        Query query=fstore.collection(Common.Suppliers).document(UserId).collection("tankers");

        FirestoreRecyclerOptions<Tanker> options=new FirestoreRecyclerOptions.Builder<Tanker>()
                .setQuery(query,Tanker.class).build();

        adapter=new FirestoreRecyclerAdapter<Tanker, TankerViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull TankerViewHolder tankerViewHolder, int i, @NonNull final Tanker tanker) {
                tankerViewHolder.tankerNumber.setText(tanker.getTankerNumber());
                tankerViewHolder.tankerDriverName.setText(tanker.getDriverName());

                tankerViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Toast.makeText(SupplierHome.this, "Coming soon", Toast.LENGTH_SHORT).show();
                        Intent tankerDetails=new Intent(SupplierHome.this,tankerDetailsSupplier.class);
                        tankerDetails.putExtra("tankerNumber",tanker.getTankerNumber());
                        tankerDetails.putExtra("driverContact",tanker.getDriverContact());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.supplier_home, menu);
        return true;
    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.AddTanker){

            Intent addTanker=new Intent(this,tankerRegistration.class);
            startActivity(addTanker);

        }else if(item.getItemId()==R.id.OrderDetailsSupplier){
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId()==R.id.FeedbackSupplier){
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if (item.getItemId()==R.id.logoutSupplier){

            fAuth.signOut();
            Toast.makeText(this, "Successfully sign out", Toast.LENGTH_SHORT).show();
            Intent signIn=new Intent(SupplierHome.this,loginActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);
        }else if(item.getItemId()==R.id.SupplierLocationUpdate){
            Intent updateLocation=new Intent(SupplierHome.this, Location.class);
            startActivity(updateLocation);
        }

        DrawerLayout drawerLayout=findViewById(R.id.supplier_drawer_layout);
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}
