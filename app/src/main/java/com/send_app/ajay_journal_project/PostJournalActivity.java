package com.send_app.ajay_journal_project;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.sql.Time;
import java.util.Date;
import java.util.Objects;

import util.Journal;
import util.JournalApi;

public class PostJournalActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int GALLERY_CODE = 1;
    private Button saveButton;
    private ProgressBar progressBar;
    private ImageView addPhotoButton;
    private EditText titleEditText;
    private EditText thoughtsEditText;
    private TextView currentUserTextview;
    private TextView dateTextview;
    private ImageView imageView ;
    private String currentUserId;
    private String currentUserName;


    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser user;

    //connection to firestore

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    private CollectionReference collectionReference = db.collection("Journal");
    private Uri imageuri ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_journal);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Post Journals");
        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.post_progressBar);
        titleEditText = findViewById(R.id.post_title_et);
        thoughtsEditText = findViewById(R.id.post_description_et);
        currentUserTextview = findViewById(R.id.post_username_textview);
        dateTextview = findViewById(R.id.post_date_textview);
        saveButton = findViewById(R.id.post_save_journal_button);
        saveButton.setOnClickListener(this);
        addPhotoButton = findViewById(R.id.postCameraButton);
        addPhotoButton.setOnClickListener(this);
        imageView = findViewById(R.id.post_imageview);

        progressBar.setVisibility(View.INVISIBLE);

        if (JournalApi.getInstance() != null) {
            currentUserId = JournalApi.getInstance().getUserId();
            currentUserName = JournalApi.getInstance().getUsername();

            currentUserTextview.setText(currentUserName);
        }
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull @NotNull FirebaseAuth firebaseAuth) {
                user = firebaseAuth.getCurrentUser();
                if (user != null) {

                } else {

                }
            }
        };
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_postadd,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.go_back:
                startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.post_save_journal_button:
                //save to journal
                saveJournal();
                break;
            case R.id.postCameraButton:
                //take photo
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent,GALLERY_CODE);
                break;
        }
    }

    private void saveJournal() {
        String title = titleEditText.getText().toString().trim();
        String thoughts = thoughtsEditText.getText().toString().trim();

        progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageuri!=null)
        {
            StorageReference filepath = storageReference.child("journal_images").child("my_image"+ Timestamp.now().getSeconds());

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            Journal journal = new Journal();
                            journal.setTitle(title);
                            journal.setThoughts(thoughts);
                            journal.setImageurl(imageUrl);
                            journal.setTimeAdded(new Timestamp(new Date()));
                            journal.setUsername(currentUserName);
                            journal.setUserId(currentUserId);
                            journal.setLikes("0");
                            journal.setRecentLiked(" ");

                            //invoke collection ref

                            collectionReference.add(journal).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    startActivity(new Intent(PostJournalActivity.this,JournalListActivity.class));
                                    finish();
                                    Toast.makeText(PostJournalActivity.this, "Saved", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull @NotNull Exception e) {
                                    Toast.makeText(PostJournalActivity.this, "Failed", Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    });



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    progressBar.setVisibility(View.INVISIBLE);

                }
            });
       }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE)
        {
          //  Toast.makeText(this, "getting here", Toast.LENGTH_SHORT).show();
            if(data!=null){
                imageuri = data.getData();
                imageView.setImageURI(imageuri);
            }
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        user = firebaseAuth.getCurrentUser() ;
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(firebaseAuth!=null)
        {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }
}