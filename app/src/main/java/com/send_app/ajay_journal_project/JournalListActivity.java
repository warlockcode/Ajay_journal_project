package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.Inflater;

import Ui.JournalRecyclerAdapter;
import de.hdodenhof.circleimageview.CircleImageView;
import util.Journal;
import util.JournalApi;

public class JournalListActivity extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private JournalRecyclerAdapter journalRecyclerAdapter;
    private TextView username ;
    private CircleImageView userimage;
    private TextView followed_view;
    private TextView following_view;



    private CollectionReference collectionReferenceJournal = db.collection("Journal");

    private CollectionReference collectionReferenceuser = db.collection("Users");

    private CollectionReference collectionReferenceFollow = db.collection("Follow");

    private  CollectionReference collectionReferenceprofile = db.collection("Profile_Picture");

    private TextView noJournalEntry;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_journal_list);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Journals");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        noJournalEntry = findViewById(R.id.list_no_thoughts);

        journalList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        username = findViewById(R.id.username_in_journalist);
        userimage =findViewById(R.id.profile_view_journalList);
        followed_view =findViewById(R.id.followed_view_j);
        following_view =findViewById(R.id.following_view_j);
        following_view.setOnClickListener(this);
        followed_view.setOnClickListener(this);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch(item.getItemId())
        {
            case R.id.action_add:
                if(currentUser!=null &&  firebaseAuth!=null)
                {
                    startActivity(new Intent( JournalListActivity.this,PostJournalActivity.class));
                    finish();
                    //finish();
                }
                break;
            case R.id.action_signout:
                if(currentUser!=null && firebaseAuth !=null)
                {
                    firebaseAuth.signOut();
                    startActivity(new Intent(JournalListActivity.this,MainActivity.class));
                    finish();
                }
                break;
            case R.id.home_activity_icon:
            {
                startActivity(new Intent (JournalListActivity.this,HomeAcivity.class));
                finish();
                break;
            }

            case R.id.update_profileandusername:
            {
                startActivity(new Intent(JournalListActivity.this,AccountUpadteActivity.class));
                finish();
                break;
            }
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    protected void onStart() {
        super.onStart();
            collectionReferenceuser.whereEqualTo("userID",JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if(!queryDocumentSnapshots.isEmpty())
                    {   String usernameString = "";
                        String followedby ="";
                        String following ="";
                        for(DocumentSnapshot snapshot :queryDocumentSnapshots)
                        {
                            usernameString = snapshot.getString("username");
                            followedby = "Followed\n"+snapshot.getString("Followed");
                            following = "Following\n"+snapshot.getString("Following");
                        }
                        followed_view.setText(followedby);
                        following_view.setText(following);
                        username.setText(usernameString);


                    }
                    
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {
                    
                }
            });

        collectionReferenceprofile.whereEqualTo("userId",JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference documentReference = db.collection("Profile_Picture").document(document.getId());
                        String url = document.getString("imageurl");
                           if(!url.equals("")) {
                            Picasso.get().load(url).placeholder(R.drawable.image_one).fit().into(userimage);
                        }
                    }
                }

            }
        });


        collectionReferenceJournal.whereEqualTo("userId", JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if(!queryDocumentSnapshots.isEmpty())
                {

                    for(QueryDocumentSnapshot journals : queryDocumentSnapshots)
                    {
                        Journal journal = journals.toObject(Journal.class);
                        journalList.add(journal);

                    }
                    journalList.sort(Journal.journaltimeSort);
                    journalRecyclerAdapter = new JournalRecyclerAdapter(JournalListActivity.this,journalList);
                    recyclerView.setAdapter(journalRecyclerAdapter);
                    journalRecyclerAdapter.notifyDataSetChanged();

                }
                else
                {
                    noJournalEntry.setVisibility(View.VISIBLE);
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        collectionReferenceprofile.whereEqualTo("userId",JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference documentReference = db.collection("Profile_Picture").document(document.getId());
                        String url = document.getString("imageurl");
                        if(!url.equals("")) {
                            Picasso.get().load(url).placeholder(R.drawable.image_one).fit().into(userimage);
                        }
                    }
                }

            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.followed_view_j:
                startActivity(new Intent(JournalListActivity.this,FollwedUsersActivity.class));
                finish();

                break;

            case R.id.following_view_j:
                startActivity(new Intent(JournalListActivity.this,FollowingUserActivity.class));
                finish();
                break;

        }
    }
}