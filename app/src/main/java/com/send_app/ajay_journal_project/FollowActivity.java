package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Ui.FollowRecyclerAdapter;
import Ui.JournalRecyclerAdapter;
import kotlinx.coroutines.flow.FlowCollector;
import util.FollowList;
import util.Follow_collection;
import util.JournalApi;

public class FollowActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;
    private FirebaseUser currentUser;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private StorageReference storageReference;
    private List<FollowList> followListList;
    private ArrayList<String> follwedIDList;
    private FollowRecyclerAdapter followRecyclerAdapter;
    private RecyclerView recyclerView1;
    private SearchView searchView;
    private CollectionReference collectionReference_Users = db.collection("Users");
    private CollectionReference collectionReferencefollow = db.collection("Follow");



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        Objects.requireNonNull(getSupportActionBar()).setElevation(0);
        getSupportActionBar().setTitle("Follow");
        firebaseAuth = FirebaseAuth.getInstance();
        currentUser = firebaseAuth.getCurrentUser();
        searchView =findViewById(R.id.followSearchBar);
        followListList = new ArrayList<>();
        follwedIDList = new ArrayList<>();
        recyclerView1 = findViewById(R.id.recyclerView_follow);
        recyclerView1.setHasFixedSize(true);
        recyclerView1.setLayoutManager(new LinearLayoutManager(this));
       // searchView =findViewById(R.id.action_search);

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
                startActivity(new Intent(FollowActivity.this,HomeAcivity.class));
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReferencefollow.whereEqualTo("FollowingUserID",JournalApi.getInstance().getUserId()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
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

        collectionReference_Users.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {

                            if (checkID(snapshot) && !JournalApi.getInstance().getUserId().toLowerCase().equals(Objects.requireNonNull(snapshot.getString("userID")).toLowerCase())) {

                                FollowList followList = new FollowList();
                                followList.setUserId(snapshot.getString("userID"));
                                followList.setUsername(snapshot.getString("username"));
                                followListList.add(followList);
                            }

                    }
                    followListList.sort(FollowList.sortbyAzname);
                    followRecyclerAdapter = new FollowRecyclerAdapter(FollowActivity.this, followListList);
                    recyclerView1.setAdapter(followRecyclerAdapter);
                    followRecyclerAdapter.notifyDataSetChanged();
                }
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
                    followRecyclerAdapter = new FollowRecyclerAdapter(FollowActivity.this,list);
                    recyclerView1.setAdapter(followRecyclerAdapter);
                    followRecyclerAdapter.notifyDataSetChanged();


                }
            });
        }

    }
    private boolean checkID(DocumentSnapshot snapshot) {
        String followedID = snapshot.getString("userID");
        for( String fc : follwedIDList)
        {
            assert followedID != null;
            if(fc.toLowerCase().equals(followedID.toLowerCase()))
            {
                return false;
            }

        }
        return true;
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