package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Ui.FollowedRecyclerAdapter;
import Ui.FollowingRecyclerAdapter;
import util.FollowList;
import util.Follow_collection;
import util.JournalApi;

public class FollowingUserActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<FollowList> followListList;
    private FollowingRecyclerAdapter followingRecyclerAdapter;
    private RecyclerView recyclerView1;
    private SearchView searchView;
    private CollectionReference collectionReferencefollow = db.collection("Follow");
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_following_user);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Following");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        searchView =findViewById(R.id.followingSearchBar);
        followListList = new ArrayList<>();
        recyclerView1 = findViewById(R.id.recyclerView_following);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));

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
                startActivity(new Intent(FollowingUserActivity.this,JournalListActivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();


        collectionReferencefollow.whereEqualTo("FollowingUserID", JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                for (DocumentSnapshot snapshot1 : queryDocumentSnapshots) {
                    Follow_collection follow_collection =new Follow_collection();
                    Log.d("testing","id:"+snapshot1.getString("FollowedUserID"));
                    String followindUserId = snapshot1.getString("FollowedUserID");
                    String followingUsername = snapshot1.getString("FollowedUsername");
                    FollowList followList = new FollowList();
                    followList.setUsername(followingUsername);
                    followList.setUserId(followindUserId);
                    followListList.add(followList);

                }
                Log.d("follwingActivity","getting here");

                followListList.sort(FollowList.sortbyAzname);
                followingRecyclerAdapter = new FollowingRecyclerAdapter(FollowingUserActivity.this,followListList);
                recyclerView1.setAdapter(followingRecyclerAdapter);
                followingRecyclerAdapter.notifyDataSetChanged();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull @NotNull Exception e) {

            }
        });



        if(!(searchView == null))
        {
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    filter(newText);
                    return true;
                }
                private void filter(String newText) {
                    List<FollowList> list = new ArrayList<>();
                    for(FollowList ele :followListList)
                    {
                        if(ele.getUsername().toLowerCase().contains(newText.toLowerCase()))
                        {
                            list.add(ele);
                        }
                    }

                    list.sort(FollowList.sortbyAzname);
                    followingRecyclerAdapter= new FollowingRecyclerAdapter(FollowingUserActivity.this,list);
                    recyclerView1.setAdapter(followingRecyclerAdapter);
                    followingRecyclerAdapter.notifyDataSetChanged();


                }
            });
        }

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
}