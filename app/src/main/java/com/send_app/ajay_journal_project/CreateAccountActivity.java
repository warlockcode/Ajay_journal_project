package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import util.JournalApi;

public class CreateAccountActivity extends AppCompatActivity {
    private Button createAccountButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    //fireSTore connection
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    private CollectionReference collectionReference = db.collection("Users");
    private CollectionReference collectionReferenceProfilePicture = db.collection("Profile_Picture");
    private EditText emailEditText;
    private EditText passwordEditText;
    private ProgressBar progressBar;
    private EditText usernameEditText;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        firebaseAuth = FirebaseAuth.getInstance();
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Crete Account");
        //setting up buttons and views
        createAccountButton = findViewById(R.id.create_acct_button);
        progressBar= findViewById(R.id.create_acct_Progress);
        emailEditText=findViewById(R.id.email_account);
        passwordEditText = findViewById(R.id.password_account);
        usernameEditText=findViewById(R.id.username_account);

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!=null)
                {

                }
                else{

                }
                }
        };

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!TextUtils.isEmpty(emailEditText.getText().toString()) && !TextUtils.isEmpty(passwordEditText.getText().toString()) &&!TextUtils.isEmpty(usernameEditText.getText().toString())  ) {
                    String email = emailEditText.getText().toString().trim();
                    String password = passwordEditText.getText().toString().trim();
                    String username = usernameEditText.getText().toString().trim();
                    createUserEmailAccouut(email, password, username);
                }
                else {
                    Toast.makeText(CreateAccountActivity.this, "Empty Fields Are Not Allowed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void createUserEmailAccouut(String email,String password,String username)
    {
        if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(username))
        {
            progressBar.setVisibility(View.VISIBLE);

            firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                    if(task.isSuccessful()){

                       sendVerificationEmail(username);

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });
        }
        else {

        }

    }

    private void sendVerificationEmail(String username) {
        currentUser = firebaseAuth.getCurrentUser();
        assert currentUser != null;
             currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<Void> task) {
                    if(task.isSuccessful())
                    {
                        Toast.makeText(CreateAccountActivity.this, "check your email for verification", Toast.LENGTH_LONG).show();
                        String currentUserId = currentUser.getUid();
                        // image
                        Map<String, String> profileupadate = new HashMap<>();
                        profileupadate.put("imageurl", "");
                        profileupadate.put("userId", currentUserId);

                        //invoke collection ref
                        collectionReferenceProfilePicture.add(profileupadate).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                //  progressBar.setVisibility(View.INVISIBLE);
                                documentReference.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable DocumentSnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                        if (!value.exists()) {

                                        }
                                    }

                                });

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {
                            }
                        });


                        // create a user map so we can create a user in the user collection
                        Map<String,String> userobj = new HashMap<>();
                        userobj.put("userID",currentUserId);
                        userobj.put("username",username);
                        userobj.put("Followed","0");
                        userobj.put("Following","0");

                        // save to our fire database
                        collectionReference.add(userobj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                        if(Objects.requireNonNull(task.getResult()).exists())
                                        {
                                            progressBar.setVisibility(View.INVISIBLE);
                                            String name = task.getResult().getString("username");
                                            JournalApi journalApi = JournalApi.getInstance();
                                            journalApi.setUserId(currentUserId);
                                            journalApi.setUsername(name);
                                            Intent intent = new Intent(CreateAccountActivity.this,LoginActivity.class);
                                            FirebaseAuth.getInstance().signOut();
                                            startActivity(intent);

                                        }
                                        else
                                        {
                                            Log.d("check_Oncomplete_in_create-account", "onComplete: "+"no data received");
                                            progressBar.setVisibility(View.INVISIBLE);
                                        }

                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });

                    }
                    else
                        {
                            Toast.makeText(CreateAccountActivity.this, "E-Mail not verified enter a corret E-mail", Toast.LENGTH_LONG).show();
                            firebaseAuth.getCurrentUser().delete();
                            overridePendingTransition(0, 0);
                            finish();
                            overridePendingTransition(0, 0);
                            startActivity(new Intent(CreateAccountActivity.this,CreateAccountActivity.class));

                     }
                }
            });

    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        currentUser = firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);
    }
}