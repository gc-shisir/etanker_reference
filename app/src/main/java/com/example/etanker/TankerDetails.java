package com.example.etanker;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;

import com.example.etanker.Model.User;
import com.example.etanker.Utils.Common;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import info.hoang8f.widget.FButton;

public class TankerDetails extends AppCompatActivity {
    FirebaseFirestore fstore;
    FirebaseAuth fAuth;
    TextView tankerNumber,literCapacity,price,driverName,driverContactNumber,ownerContactNumber,waterSource,tankerStatus,stickerColor;
    ImageView callOwner,callDriver;
    FButton placeOrder;
    CollapsingToolbarLayout collapsing;

    String supplierEmail="";
    String tankerNum="";
    String UserId;
    private static final int PERMISSION_CALL_PHONE = 17;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tanker_details);

        fAuth=FirebaseAuth.getInstance();

        tankerNumber=findViewById(R.id.tanker_number);
        literCapacity=findViewById(R.id.tanker_capacity);
        price=findViewById(R.id.price);
        driverName=findViewById(R.id.driverName);
        driverContactNumber=findViewById(R.id.driverContactNumber);
        ownerContactNumber=findViewById(R.id.OwnerContactNumber);
        stickerColor=findViewById(R.id.tankerStickerColor);
        waterSource=findViewById(R.id.waterSource);
        tankerStatus=findViewById(R.id.tankerStatus1);

        callDriver=findViewById(R.id.callDriver);
        callOwner=findViewById(R.id.callOwner);
        placeOrder=findViewById(R.id.tankerOrder);
        collapsing=findViewById(R.id.collapsing);
        collapsing.setExpandedTitleTextAppearance(R.style.ExpandedAppbar);
        collapsing.setCollapsedTitleTextAppearance(R.style.CollapsedAppbar);

        fstore=FirebaseFirestore.getInstance();

        if(getIntent()!=null){
            supplierEmail=getIntent().getStringExtra("ownerEmail");
            tankerNum=getIntent().getStringExtra("tankerPlateNumber");
        }

        getTankerDetail(supplierEmail,tankerNum);

        callDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(TankerDetails.this);
                alertDialog.setTitle("Do you want to make a call to "+driverContactNumber.getText())
                        .setIcon(R.drawable.ic_call_black_24dp)
                        .setPositiveButton("Call", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent callIntent=new Intent(Intent.ACTION_CALL);
                                callIntent.setData(Uri.parse("tel:"+driverContactNumber.getText()));

                                if (ActivityCompat.checkSelfPermission(TankerDetails.this, Manifest.permission.CALL_PHONE)!= PackageManager.PERMISSION_GRANTED){
                                    //requestRuntimePermission
                                    Toast.makeText(TankerDetails.this, "Permission should be provided to continue with this feature", Toast.LENGTH_SHORT).show();
                                    if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.CALL_PHONE}, PERMISSION_CALL_PHONE);
                                    }
                                }else{
                                    startActivity(callIntent);
                                }
                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                alertDialog.show();
            }
        });


        placeOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog=new AlertDialog.Builder(TankerDetails.this);
                alertDialog.setTitle("Are you sure?").setMessage("You will place a order for the tanker number "+tankerNum)
                        .setPositiveButton("Order", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                final CollectionReference collectionReference=fstore.collection(Common.Suppliers);//.document().collection("tankers");
                                collectionReference.whereEqualTo("email",supplierEmail).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        Log.d("My Activity",queryDocumentSnapshots.getDocuments().get(0).getId());
                                        final CollectionReference collectionReference1=collectionReference.document(queryDocumentSnapshots.getDocuments().get(0).getId()).collection("orderDetails");
                                        UserId= Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                                        DocumentReference doc=fstore.collection(Common.Consumers).document(UserId);
                                        doc.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                            @Override
                                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                                assert documentSnapshot != null;
                                                User user = documentSnapshot.toObject(User.class);
                                                assert user != null;

                                                if(user.getLatitude()==null || user.getLongitude()==null || user.getLongitude().isEmpty() || user.getLatitude().isEmpty()){
                                                    Toast.makeText(TankerDetails.this, "Please update your location first", Toast.LENGTH_SHORT).show();
                                                    return;
                                                }


                                                Map<String,Object> info=new HashMap<>();
                                                info.put("tankerNumber",tankerNum);
                                                info.put("supplierEmail",supplierEmail);

                                                collectionReference1.document(tankerNum).set(info);


                                                collectionReference1.document(tankerNum).set(user,SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(TankerDetails.this, "Order Placed Successfully", Toast.LENGTH_SHORT).show();
                                                        showNotificationForCustomer();
                                                        startActivity(new Intent(TankerDetails.this, CustomerHome.class));

                                                    }
                                                }).addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(TankerDetails.this, "error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }
                                        });

                                    }
                                });




                            }
                        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });

                alertDialog.show();

            }
        });





    }

    private void showNotificationForCustomer(){
        Intent intent=new Intent(getApplicationContext(),CustomerHome.class);
        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel("TankerOrder","Water Tankers", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext(),"TankerOrder");

        builder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Water Tankers")
                .setContentTitle("Order Status")
                .setContentText("Your order for the water tanker has been placed")
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher_foreground);


        NotificationManager notificationManager=(NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1,builder.build());

    }

    private void getTankerDetail(final String supplierEmail, final String tankerNum) {
        fstore.collectionGroup("tankers").whereEqualTo("ownerEmail",supplierEmail)
                .whereEqualTo("tankerNumber",tankerNum).addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {

                if (e != null) {
                    Log.w("My Activity", "Listen failed.", e);
                    return;
                }

                assert queryDocumentSnapshots != null;
                for(QueryDocumentSnapshot doc:queryDocumentSnapshots){
                    if(doc.exists()){
                        Log.d("My Activity", Objects.requireNonNull(doc.getString("tankerNumber")));
                        tankerNumber.setText(doc.getString("tankerNumber"));
                        literCapacity.setText(doc.getString("literCapacity"));
                        waterSource.setText(doc.getString("source"));
                        driverContactNumber.setText(doc.getString("driverContact"));
                        driverName.setText(doc.getString("driverName"));
                        stickerColor.setText(doc.getString("stickerColor"));


                    }else{
                        Log.d("My Activity","checkpoint Two");
                    }
                }


            }
        });

    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case PERMISSION_CALL_PHONE:
                if( grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    return;
                }else{
                    Toast.makeText(this, "You can not use this feature without giving permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
}
