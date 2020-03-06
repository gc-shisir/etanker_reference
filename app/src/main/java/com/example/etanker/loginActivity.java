package com.example.etanker;

import androidx.annotation.NonNull;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.Query;
import com.google.firebase.database.core.SnapshotHolder;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import io.opencensus.tags.Tag;

public class loginActivity extends AppCompatActivity {
    TextView userRegister, forgotPassword;
    EditText loginEmail;
    EditText loginPassword;
    CheckBox mcustomer, msupplier;
    Button btnLogin;
    FirebaseAuth fAuth;
    FirebaseFirestore fstore;
    String UserID;
    private ProgressDialog progressDialog;
    String TAG = "My Activity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userRegister = (TextView) findViewById(R.id.registerUser);
        loginEmail = (EditText) findViewById(R.id.login_email);
        loginPassword = (EditText) findViewById(R.id.login_password);
        btnLogin = (Button) findViewById(R.id.login_button);
        forgotPassword = findViewById(R.id.forgot_Password);

        mcustomer = findViewById(R.id.customer_Check);
        msupplier = findViewById(R.id.supplier_Check);

        fstore = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(this);

        userRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), register_customer.class));
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = loginEmail.getText().toString().trim();
                String password = loginPassword.getText().toString().trim();
                if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                    Toast.makeText(loginActivity.this, "Please enter email and passsword...", Toast.LENGTH_SHORT).show();
                }
                else if (msupplier.isChecked() && mcustomer.isChecked()) {
                    Toast.makeText(loginActivity.this, "select only one", Toast.LENGTH_SHORT).show();
                }
                else if (mcustomer.isChecked()) {
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    login(email,password);

                }
                else if (msupplier.isChecked()) {
                    progressDialog.setMessage("Processing...");
                    progressDialog.show();
                    login1(email, password);

                } else {
                    Toast.makeText(loginActivity.this, "select one boxes", Toast.LENGTH_SHORT).show();
                }
            }

        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText resetMail = new EditText(v.getContext());
                AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(v.getContext());
                passwordResetDialog.setMessage("Enter email to reset the password");
                passwordResetDialog.setView(resetMail);

                passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String mail = resetMail.getText().toString().trim();
                        fAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(loginActivity.this, "a link has been sent to the email", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(loginActivity.this, "error has occured", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
                passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                passwordResetDialog.show();
            }
        });
    }

    private void login(final String email, final String password) {
        fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    UserID = fAuth.getCurrentUser().getUid();
                    final DocumentReference documentReference = fstore.collection("customers").document(UserID);
                    documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                            try {
                                if ((documentSnapshot.getString("email").equals(email))) {
                                    Log.d(TAG, "checkpoint");
                                    Toast.makeText(loginActivity.this, "Successfully logged in...", Toast.LENGTH_SHORT).show();
                                    progressDialog.dismiss();
                                    startActivity(new Intent(loginActivity.this, customerDashboard.class));
                                    finish();
                                }
                            }
                            catch (NullPointerException e1) {
                                Log.d(TAG, "success");
                                progressDialog.dismiss();
                                Toast.makeText(loginActivity.this, "wrong check box", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });

//                        fstore.collection("customers").document(UserID).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                            @Override
//                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                                if(task.isSuccessful()) {
//
//                                    Toast.makeText(loginActivity.this, "Successfully logged in...", Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                    startActivity(new Intent(loginActivity.this, customerDashboard.class));
//                                    finish();
//                                    Log.d(TAG,"Document Snapshot");
//                                }
//                                else{
//                                    Toast.makeText(loginActivity.this, "wrong check box", Toast.LENGTH_SHORT).show();
//                                    progressDialog.dismiss();
//                                }
//                            }
//                        });
//                    }
//                    else {
//                    }
////                    else {
//                            Toast.makeText(loginActivity.this, "error! you checked the wrong box", Toast.LENGTH_SHORT).show();
//                            progressDialog.dismiss();
//                        }
                } else {
                    Toast.makeText(loginActivity.this, "error", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();

                }
            }
        });
    }
        private void login1(final String email, final String password){
            fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        UserID = fAuth.getCurrentUser().getUid();
                        final DocumentReference documentReference = fstore.collection("suppliers").document(UserID);
                        documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            @Override
                            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                try {
                                    if ((documentSnapshot.getString("email").equals(email))) {
                                        Log.d(TAG, "checkpoint");
                                        Toast.makeText(loginActivity.this, "Successfully logged in...", Toast.LENGTH_SHORT).show();
                                        progressDialog.dismiss();
                                        Intent intent = new Intent(loginActivity.this, supplierDashboard.class);
                                        intent.putExtra("userId", UserID);
                                        startActivity(intent);
                                        finish();

//                                       startActivity(new Intent(getApplicationContext(),supplierDashboard.class));
                                        // finish();
                                    }
                                }
                                catch (NullPointerException e1) {
                                    Log.d(TAG, "success1");
                                    progressDialog.dismiss();
                                    Toast.makeText(loginActivity.this, "wrong check box", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                        else {
                            Toast.makeText(loginActivity.this, "password and email does not match", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
////                        else{
//                        Toast.makeText(loginActivity.this,"error! you checked the wrong box",Toast.LENGTH_SHORT).show();
//                        progressDialog.dismiss();
//                    }
                }
            });

        }
}

