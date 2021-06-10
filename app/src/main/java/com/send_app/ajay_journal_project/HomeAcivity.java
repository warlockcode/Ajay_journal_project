package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.type.Date;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import Ui.HomeRecyclerAdapter;
import util.Follow_collection;
import util.Journal;
import util.JournalApi;

public class HomeAcivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private ArrayList<String> follwedIDList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<Journal> journalList;
    private RecyclerView recyclerView;
    private HomeRecyclerAdapter homeRecyclerAdapter;
   // private JournalRecyclerAdapter journalRecyclerAdapter;

    private CollectionReference collectionReference = db.collection("Journal");
    private CollectionReference collectionReferencefollow = db.collection("Follow");
    private TextView nofollwing;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_acivity);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Home");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();

        nofollwing = findViewById(R.id.list_no_following_home);
        homeRecyclerAdapter = new HomeRecyclerAdapter();
        journalList = new ArrayList<>();
        follwedIDList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_home);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

    }

    // setting menubar for home Activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menuforhomeactivity,menu);
        return super.onCreateOptionsMenu(menu);
    }

    // setting up menu options for home activity
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId())
        {
            case R.id.journal_list_botton:
                //go back to JournalListACtivity
                startActivity(new Intent(HomeAcivity.this,JournalListActivity.class));
                finish();
                break;
            case R.id.follow_button:
                //go to the followActivity
                startActivity(new Intent(HomeAcivity.this,FollowActivity.class));
                finish();
                break;

        }


        return super.onOptionsItemSelected(item);
    }

    // getting the id's of users we are currently following
    @Override
    protected void onStart() {
        super.onStart();
        collectionReferencefollow.whereEqualTo("FollowingUserID", JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot snapshot1 : queryDocumentSnapshots) {
                    Follow_collection follow_collection =new Follow_collection();
                    Log.d("checking","id:"+snapshot1.getString("FollowedUserID"));
                    String follwedId = snapshot1.getString("FollowedUserID");
                    follwedIDList.add(follwedId);
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });

        // adding journal to the homeRecyclerAdapter

            collectionReference.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

                            if (checkID(snapshot)) {
                                Journal journal = snapshot.toObject(Journal.class);
                                journalList.add(journal);

                            }
                            else if(JournalApi.getInstance().getUserId().toLowerCase().equals(Objects.requireNonNull(snapshot.getString("userId")).toLowerCase()))
                            {
                                Journal journal = snapshot.toObject(Journal.class);
                                Timestamp time = snapshot.getTimestamp("timeAdded");
                             //   String timeago = String.valueOf(time.toDate().getDate());
                                String timeago = (String) DateUtils.getRelativeTimeSpanString(time.getSeconds() * 1000);
                                String day = "3 day ago";
                                Log.d("timeago",timeago);

                                //if(date)
                              if(!day.toLowerCase().equals(timeago.toLowerCase()))
                                journalList.add(journal);
                            }
                        }
                        journalList.sort(Journal.journaltimeSort);

                        homeRecyclerAdapter= new HomeRecyclerAdapter(HomeAcivity.this,journalList);
                        recyclerView.setAdapter(homeRecyclerAdapter);
                        homeRecyclerAdapter.notifyDataSetChanged();
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });

    }

    //checking and adding if the post is posted by the persons we are following
    private boolean checkID(DocumentSnapshot snapshot) {
        String followedID = snapshot.getString("userId");
        for( String fc : follwedIDList)
        {
            assert followedID != null;
            if(fc.toLowerCase().equals(followedID.toLowerCase()))
            {
                return true;
            }

        }
        return false;
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }


}