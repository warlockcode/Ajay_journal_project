package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import util.Journal;
import util.JournalApi;

public class LoginActivity extends AppCompatActivity {
    private Button loginButton;
    private Button createAccountButton;
    private AutoCompleteTextView emailAddress;
    private ProgressBar progressBar;
    private EditText password;
    private FirebaseAuth  firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;


    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReference = db.collection("Users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Log In");
        loginButton = findViewById(R.id.email_sign_in_button);
        createAccountButton = findViewById(R.id.create_acct_button_login);
        emailAddress = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);
        progressBar = findViewById(R.id.loginProgress);
        firebaseAuth = FirebaseAuth.getInstance();

        createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,CreateAccountActivity.class));
            }
        });
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                loginEmailPasswordUser(emailAddress.getText().toString().trim(),password.getText().toString().trim());

            }

        });

}

    private void loginEmailPasswordUser(String email, String pwd) {
       if(!TextUtils.isEmpty(email) && !TextUtils.isEmpty(pwd))
        {
            firebaseAuth.signInWithEmailAndPassword(email,pwd).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                         FirebaseUser user = firebaseAuth.getCurrentUser();
                         assert user != null;
                         String currentUserId = user.getUid();

                         collectionReference.whereEqualTo("userID",currentUserId).addSnapshotListener(new EventListener<QuerySnapshot>() {
                             @Override
                             public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                                     if(error !=null)
                                     {
                                     }
                                 assert value != null;
                                 if(!value.isEmpty())
                                 {
                                     progressBar.setVisibility(View.INVISIBLE);
                                     for(QueryDocumentSnapshot snapshot : value) {
                                         JournalApi journalApi = JournalApi.getInstance();
                                         journalApi.setUsername(snapshot.getString("username"));
                                         journalApi.setUserId(snapshot.getString("userID"));

                                         // go to ListActivity
                                         startActivity(new Intent(LoginActivity.this,JournalListActivity.class));
                                         finish();
                                     }
                                 }
                             }
                         });
                }
            });
        }
        else
        {
            progressBar.setVisibility(View.INVISIBLE);
            Toast.makeText(this, "Please enter correct email and password", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }
}