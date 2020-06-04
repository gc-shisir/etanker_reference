package com.example.etanker;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.etanker.Interface.ItemClickListener;
import com.example.etanker.Model.Supplier;
import com.example.etanker.Utils.Common;
import com.example.etanker.ViewHolder.SupplierViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SnapshotMetadata;
import com.mancj.materialsearchbar.MaterialSearchBar;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class CustomerHome extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    DocumentReference customerData;
    String UserId;


    RecyclerView customerRecyclerView;
    FirestoreRecyclerAdapter<Supplier, SupplierViewHolder> adapter, searchBarAdapter;

    List<String> suggestionsList=new ArrayList<>();
    SearchView searchBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_home);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        fAuth = FirebaseAuth.getInstance();
        fstore = FirebaseFirestore.getInstance();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        DrawerLayout drawer = findViewById(R.id.customer_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar,
                R.string.open, R.string.close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();


        NavigationView navigationView = findViewById(R.id.customerNavView);
        navigationView.setNavigationItemSelectedListener(this);

        UserId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        customerData = fstore.collection(Common.Consumers).document(UserId);

        View headerView = navigationView.getHeaderView(0);
        final TextView UserName = headerView.findViewById(R.id.customerUserName);
        final TextView UserEmailAddress = headerView.findViewById(R.id.customerEmailAddress);


        customerData.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (e!=null){
                    Log.d("My Activity","Error:"+e.getMessage());
                }else{
                    UserName.setText(Objects.requireNonNull(documentSnapshot.get("name")).toString());
                    UserEmailAddress.setText(Objects.requireNonNull(documentSnapshot.get(Common.Email)).toString());
                }

            }
        });

        customerRecyclerView = findViewById(R.id.SupplierListView);
        customerRecyclerView.hasFixedSize();
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        customerRecyclerView.setLayoutManager(layoutManager);

        loadSupplierList();

        final SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchBar = findViewById(R.id.searchView);
        loadSuggestList();
        searchBar.setQueryHint("search here");
        searchBar.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //called when we pressed the search
                StartSearch(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //called when we even type one letter
                assert searchManager != null;
                searchBar.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
                return  false;
            }


        });




    }

    private void StartSearch(CharSequence text) {
        Query query=FirebaseFirestore.getInstance().collectionGroup(Common.Suppliers)
                .whereEqualTo("name",text);

        FirestoreRecyclerOptions<Supplier> options=new FirestoreRecyclerOptions.Builder<Supplier>()
                .setQuery(query,Supplier.class).build();

        searchBarAdapter=new FirestoreRecyclerAdapter<Supplier, SupplierViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SupplierViewHolder supplierViewHolder, final int i, @NonNull Supplier supplier) {
                supplierViewHolder.supplierName.setText(supplier.getName());
                supplierViewHolder.supplierPhone.setText(supplier.getPhone_number());


                supplierViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent supplierTanker=new Intent(getApplicationContext(), supplierTanker.class);

                        Log.d("My Activity",searchBarAdapter.getItem(i).getEmail());
                        supplierTanker.putExtra("SupplierId",searchBarAdapter.getItem(i).getEmail());
                        startActivity(supplierTanker);

                    }
                });

            }

            @NonNull
            @Override
            public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.supplier_view,parent,false);

                return new SupplierViewHolder(itemView);
            }
        };

        searchBarAdapter.startListening();
        customerRecyclerView.setAdapter(searchBarAdapter);

    }

    private void loadSuggestList() {

        fstore.collection(Common.Suppliers).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    Supplier supp = doc.toObject(Supplier.class);
                    assert supp != null;
                    suggestionsList.add(supp.getName());
                }
            }
        });
    }


    private void loadSupplierList() {

        FirebaseFirestore db=FirebaseFirestore.getInstance();

        Query query=db.collection(Common.Suppliers);
        FirestoreRecyclerOptions<Supplier> options=new FirestoreRecyclerOptions.Builder<Supplier>()
                .setQuery(query,Supplier.class).build();

        adapter=new FirestoreRecyclerAdapter<Supplier, SupplierViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull SupplierViewHolder supplierViewHolder, final int i, @NonNull Supplier supplier) {
                supplierViewHolder.supplierName.setText(supplier.getName());
                supplierViewHolder.supplierPhone.setText(supplier.getPhone_number());


                supplierViewHolder.setItemClickListener(new ItemClickListener() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        Intent supplierTanker=new Intent(getApplicationContext(), supplierTanker.class);

                        Log.d("My Activity",adapter.getItem(i).getEmail());
                        supplierTanker.putExtra("SupplierId",adapter.getItem(i).getEmail());
                        startActivity(supplierTanker);

                    }
                });
            }

            @NonNull
            @Override
            public SupplierViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View itemView= LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.supplier_view,parent,false);

                return new SupplierViewHolder(itemView);
            }
        };

        adapter.notifyDataSetChanged();
        adapter.startListening();
        customerRecyclerView.setAdapter(adapter);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.customer_home, menu);
        return true;
    }



    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {

        if(item.getItemId()==R.id.OrderTanker){
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.Order_Details){
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.logoutUser){

            fAuth.signOut();
            Toast.makeText(this, "Successfully sign out", Toast.LENGTH_SHORT).show();
            Intent signIn=new Intent(CustomerHome.this,loginActivity.class);
            signIn.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(signIn);


        }else if(item.getItemId()==R.id.Feedback){
            Toast.makeText(this, "Coming Soon", Toast.LENGTH_SHORT).show();
        }else if(item.getItemId()==R.id.LocationDetails){
            startActivity(new Intent(CustomerHome.this,CustomerLocation.class));
        }


        DrawerLayout drawer = findViewById(R.id.customer_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


}
