package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import org.jetbrains.annotations.NotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import util.Journal;
import util.JournalApi;

public class AccountUpadteActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView user_image;
    private EditText username ;
    private Button saveButton ;
    public static final int GALLERY_CODE = 1 ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser ;
    private CollectionReference collectionReferenceUser = db.collection("Users");
    private CollectionReference collectionReferenceProfilePicture = db.collection("Profile_Picture");
    private Uri imageuri ;
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account_upadte);

        user_image = findViewById(R.id.update_profilepic);
        username = findViewById(R.id.update_username);
        saveButton=findViewById(R.id.save_update_account_profile);

        user_image.setOnClickListener(this);
        saveButton.setOnClickListener(this);
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

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
                startActivity(new Intent(AccountUpadteActivity.this,JournalListActivity.class));
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(imageuri==null) {
            collectionReferenceProfilePicture.whereEqualTo("userId", JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

                            String imgUrl = snapshot.getString("imageurl");
                            if (!imgUrl.equals("")) {
                                Picasso.get().load(imgUrl).placeholder(R.drawable.image_one).fit().into(user_image);
                            }
                        }
                    } else {

                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {


                }
            });
        }
        username.setText(JournalApi.getInstance().getUsername());



    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
           case R.id.update_profilepic:
               Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
               galleryIntent.setType("image/*");
               startActivityForResult(galleryIntent,GALLERY_CODE);
            break;

            case R.id.save_update_account_profile:
                    update_profile();

            break;
        }

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE)
        {
         //   Toast.makeText(this, "getting here", Toast.LENGTH_SHORT).show();
            if(data!=null){
                imageuri = data.getData();
                Picasso.get().load(imageuri).fit().into(user_image);
            //    user_image.setImageURI(imageuri);
            }
        }

    }

    private void update_profile() {
        String usernameString = username.getText().toString().trim();


      //  progressBar.setVisibility(View.VISIBLE);

        if(!TextUtils.isEmpty(usernameString) && imageuri!=null)
        {
            StorageReference filepath = storageReference.child("journal_images").child("my_image"+ Timestamp.now().getSeconds());

            filepath.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    filepath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String imageUrl = uri.toString();
                            collectionReferenceProfilePicture.whereEqualTo("userId",JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            DocumentReference documentReference = db.collection("Profile_Picture").document(document.getId());
                                            documentReference.update("imageurl",imageUrl);
                                            String url = document.getString("imageurl");
                                            Picasso.get().load(imageUrl).placeholder(R.drawable.outline_account_circle_black_36).fit().into(user_image);

                                        }
                                    }

                                }
                            });


                        }
                    });
                }
            });

            collectionReferenceUser.whereEqualTo("userID",JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                    if (task.isSuccessful()) {
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            DocumentReference documentReference = db.collection("Users").document(document.getId());
                            documentReference.update("username",usernameString);
                            username.setText(document.getString("username"));

                        }
                    }

                }
            });
        }

    }

}