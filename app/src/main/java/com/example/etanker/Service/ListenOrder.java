package com.example.etanker.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.etanker.R;
import com.example.etanker.SupplierHome;
import com.example.etanker.Utils.Common;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

public class ListenOrder extends Service implements EventListener {
    FirebaseFirestore fstore;
    FirebaseAuth firebaseAuth;
    DocumentReference doc;
    CollectionReference collectionReference;
    String UserId="";

    public ListenOrder() {
    }

    @Override
    public IBinder onBind(Intent intent) {
//        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        firebaseAuth= FirebaseAuth.getInstance();
        fstore=FirebaseFirestore.getInstance();
        UserId= Objects.requireNonNull(firebaseAuth.getCurrentUser()).getUid();
        collectionReference=fstore.collection(Common.Suppliers).document(UserId).collection("orderDetails");
        Log.d("My Activity",collectionReference.getPath());
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//       collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
//           @Override
//           public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
//               for(DocumentChange doc:queryDocumentSnapshots.getDocumentChanges()){
//                   switch (doc.getType()){
//                       case ADDED:
//                           User user=queryDocumentSnapshots.toObjects(User.class).get(0);
//                           showNotification(queryDocumentSnapshots.getQuery(), User.class);
//                           break;
//                       case MODIFIED:
//                           showNotification(queryDocumentSnapshots.getQuery(), User.class);
//                           break;
//                       case REMOVED:
//                           break;
//                   }
//               }
//           }
//       });

        collectionReference.addSnapshotListener(this);
        return super.onStartCommand(intent, flags, startId);
    }

    private void showNotification() {

        Intent intent=new Intent(getBaseContext(), SupplierHome.class);
       // intent.putExtra("userPhone",request.getPhone());

        PendingIntent contentIntent=PendingIntent.getActivity(getBaseContext(),0,intent,PendingIntent.FLAG_UPDATE_CURRENT);

        //A PendingIntent is a token that you give to a foreign application
        // (e.g. NotificationManager, AlarmManager, Home Screen AppWidgetManager,
        // or other 3rd party applications), which allows the foreign application
        // to use your application's permissions to execute a predefined piece of code

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel=
                    new NotificationChannel("tankerOrder","Water Tanker", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder=new NotificationCompat.Builder(getBaseContext(),"tankerOrder");

        builder.setAutoCancel(false)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setTicker("Water Tanker")
                .setContentTitle("Order Status")
                .setContentText(" your tanker has been ordered for the service")
                .setContentIntent(contentIntent)
                .setContentInfo("Info")
                .setSmallIcon(R.mipmap.ic_launcher_foreground);


        NotificationManager notificationManager=(NotificationManager) getBaseContext().getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(1,builder.build());


    }


    @Override
    public void onEvent(@Nullable Object o, @Nullable FirebaseFirestoreException e) {
        Log.d("My Activity",collectionReference.getPath());
        Log.d("My Activity","Notification point 1");

        collectionReference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                Log.d("My Activity","Inside OnEvent");
                if(e!=null){
                    Log.d("My Activity", Objects.requireNonNull(e.getMessage()));
                }else {
                    assert queryDocumentSnapshots != null;
                    for (DocumentChange doc : queryDocumentSnapshots.getDocumentChanges()) {
                        switch (doc.getType()){
                            case ADDED:
                                //showNotification();
                                Log.d("My Activity","Document Added");
                                break;
                            case REMOVED:
                                Log.d("My Activity","Document Removed");
                                break;
                            case MODIFIED:
                                showNotification();
                                break;
                        }

                    }
                }

            }
        });//.remove();

    }
}
