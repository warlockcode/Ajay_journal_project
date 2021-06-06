package com.send_app.ajay_journal_project;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import util.JournalApi;

public class MainActivity extends AppCompatActivity {
    private Button getStratedButton;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getStratedButton =findViewById(R.id.startButton);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("");

        firebaseAuth = FirebaseAuth.getInstance();
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                currentUser = firebaseAuth.getCurrentUser();
                if(currentUser!= null)
                {
                    currentUser = firebaseAuth.getCurrentUser();
                    String currentUserId = currentUser.getUid();
                    collectionReference.whereEqualTo("userID",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable  QuerySnapshot value, @Nullable  FirebaseFirestoreException error) {

                            if(error !=null)
                            {

                                return;
                            }
                            String name;
                            if(!value.isEmpty())
                            {
                                for(QueryDocumentSnapshot snapshot :value) {
                                    JournalApi journalApi = JournalApi.getInstance();
                                    journalApi.setUserId(snapshot.getString("userID"));
                                    journalApi.setUsername(snapshot.getString("username"));

                                    startActivity(new Intent(MainActivity.this,JournalListActivity.class));
                                    finish();
                                }
                            }
                            else
                            {

                            }
                        }
                    });
                }
            }
        };

        getStratedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //launching login activity
                startActivity(new Intent(MainActivity.this,LoginActivity.class));
                finish();
            }
        });


    }

    @Override
    protected void onStart() {
        super.onStart();
        currentUser =firebaseAuth.getCurrentUser();
        firebaseAuth.addAuthStateListener(authStateListener);

    }

    @Override
    protected void onPause() {
        super.onPause();
        if(firebaseAuth!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }

    }
}