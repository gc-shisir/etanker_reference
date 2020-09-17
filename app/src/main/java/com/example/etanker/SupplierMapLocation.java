package com.example.etanker;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;

import com.example.etanker.Model.Supplier;
import com.example.etanker.Model.User;
import com.example.etanker.Utils.Common;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class SupplierMapLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    String lat,lon;

    FirebaseAuth firebaseAuth;
    FirebaseFirestore fstore;
    DocumentReference documentReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_supplier_map_location);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        firebaseAuth=FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("My Activity","Map Checkpoint");

        fstore.collection(Common.Suppliers).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    System.err.println("Listen failed:" + e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                for (DocumentSnapshot doc:queryDocumentSnapshots){
                    Supplier supplier=doc.toObject(Supplier.class);
                    assert supplier != null;
                    if(supplier.getLatitude()!=null){
                        Log.d("My Activity","success");
                        Log.d("My Activity","Map Checkpoint2");
                        double lat=Double.parseDouble(supplier.getLatitude());
                        double lon=Double.parseDouble(supplier.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)
                                                    ).title(supplier.getName()));
                        LatLng supplierLocation = new LatLng(lat,lon);
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(supplierLocation));
                        mMap.animateCamera(CameraUpdateFactory.zoomTo(10.0f));

                    }
                }
            }
        });
//        Log.d("My Activity","Map Checkpoint2");
//        double lat=Double.parseDouble(supplier.getLatitude());
//        double lon=Double.parseDouble(supplier.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(new LatLng(lat,lon)
//        ).title(supplier.getName()));



    }
}