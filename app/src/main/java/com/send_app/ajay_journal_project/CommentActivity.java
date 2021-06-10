package com.send_app.ajay_journal_project;

import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.List;

import Ui.CommentRecyclerAdapter;
import util.Comments;
import util.JournalApi;

public class CommentActivity extends AppCompatActivity implements View.OnClickListener {
    private CommentRecyclerAdapter commentRecyclerAdapter;
    private List<Comments> commentsList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference collectionReferenceComments = db.collection("Comments");
    private RecyclerView recyclerView;
    private EditText editcomment ;
    private ImageButton add_comment_button;
    private String imgurl,context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        commentsList = new ArrayList<>();
        recyclerView= findViewById(R.id.recyclerView_comment);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        editcomment = findViewById(R.id.comment_editText);
        add_comment_button = findViewById(R.id.add_comment_button);
        Intent intent = getIntent();
        context =intent.getStringExtra("context");
        imgurl = intent.getStringExtra("imageurl");
        add_comment_button.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();

        collectionReferenceComments.whereEqualTo("imageurl",imgurl).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

            if(task.isSuccessful())
            {
                for(QueryDocumentSnapshot snapshot : task.getResult())
                {
                    Comments comment = snapshot.toObject(Comments.class);
                    commentsList.add(comment);
                }
                commentsList.sort(Comments.commentsComparator);
                commentRecyclerAdapter = new CommentRecyclerAdapter(CommentActivity.this,commentsList);
                recyclerView.setAdapter(commentRecyclerAdapter);
                commentRecyclerAdapter.notifyDataSetChanged();

            }


            }
        });






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
                if(context.toLowerCase().equals("home".toLowerCase()))
                {
                    startActivity(new Intent(CommentActivity.this, HomeAcivity.class));
                }
                if(context.toLowerCase().equals("journal".toLowerCase())) {

                    startActivity(new Intent(CommentActivity.this, JournalListActivity.class));
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }



    @Override
    public void onClick(View v) {
        switch(v.getId())
        {
            case R.id.add_comment_button:
                Comments comments = new Comments();
                String comment = editcomment.getText().toString().trim();
                comments.setComment(comment);
                comments.setCommentedbyUserId(JournalApi.getInstance().getUserId());
                comments.setCommentedbyusername(JournalApi.getInstance().getUsername());
                comments.setTimeago(Timestamp.now());
                comments.setImageurl(imgurl);
                collectionReferenceComments.add(comments).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Intent intent = new Intent(CommentActivity.this,CommentActivity.class);
                        intent.putExtra("context",context);
                        intent.putExtra("imageurl",imgurl);
                        startActivity(intent);

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {

                    }
                });

                break;
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