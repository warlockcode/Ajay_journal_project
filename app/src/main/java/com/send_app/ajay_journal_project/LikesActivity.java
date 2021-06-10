package com.send_app.ajay_journal_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.SearchView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import Ui.FollowRecyclerAdapter;
import Ui.LikesRecyclerAdapter;
import util.FollowList;
import util.Likes;

public class LikesActivity extends AppCompatActivity {
    private LikesRecyclerAdapter likesRecyclerAdapter;
    private List<Likes> likesList ;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceLikes =db.collection("Like");

    private RecyclerView recyclerView;
    String activity,imgurl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_likes);

        likesList = new ArrayList<>();
        recyclerView = findViewById(R.id.recyclerView_likes);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        Intent intent = getIntent();
        activity = intent.getStringExtra("context");
        Log.d("checkactivitylog", "onCreate: "+activity);
        imgurl = intent.getStringExtra("imageurl");
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
                if(activity.toLowerCase().equals("home".toLowerCase()))
                {
                    startActivity(new Intent(LikesActivity.this, HomeAcivity.class));
                }
                if(activity.toLowerCase().equals("journal".toLowerCase())) {

                    startActivity(new Intent(LikesActivity.this, JournalListActivity.class));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStart() {
        super.onStart();

        collectionReferenceLikes.whereEqualTo("imageurl",imgurl).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful())
                    {
                        for(QueryDocumentSnapshot snapshot : task.getResult())
                        {
                            Likes likes = new Likes();
                            likes = snapshot.toObject(Likes.class);
                            likesList.add(likes);
                        }
                        likesList.sort(Likes.likesComparator);
                        likesRecyclerAdapter = new LikesRecyclerAdapter(LikesActivity.this,likesList);
                        recyclerView.setAdapter(likesRecyclerAdapter);
                        likesRecyclerAdapter.notifyDataSetChanged();
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
}