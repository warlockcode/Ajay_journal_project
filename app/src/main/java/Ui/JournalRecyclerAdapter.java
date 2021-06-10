package Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.send_app.ajay_journal_project.CommentActivity;
import com.send_app.ajay_journal_project.JournalListActivity;
import com.send_app.ajay_journal_project.LikesActivity;
import com.send_app.ajay_journal_project.R;
import com.squareup.picasso.Picasso;


import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import util.Journal;
import util.JournalApi;
import util.LikedorNotJournal;
import util.LikedornotHome;


public class JournalRecyclerAdapter extends RecyclerView.Adapter<JournalRecyclerAdapter.ViewHolder>
{

    private Context context;
    private List<Journal> journalList;
    private AlertDialog alertDialog ;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Journal");
    CollectionReference collectionReferenceprofile = db.collection("Profile_Picture");
    CollectionReference collectionReferenceLike = db.collection("Like");

    public JournalRecyclerAdapter(){};
    public JournalRecyclerAdapter(Context context, List<Journal> journalList) {
        this.context = context;
        this.journalList = journalList;
    }

    @NonNull
    @NotNull
    @Override
    public JournalRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.journal_row,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull JournalRecyclerAdapter.ViewHolder holder, int position) {
        Journal journal = journalList.get(position);
        String imgUrl;
        holder.title.setText(journal.getTitle());
        holder.thoughts.setText(journal.getThoughts());
        holder.name.setText(journal.getUsername());
        imgUrl = journal.getImageurl();
        Picasso.get().load(imgUrl).placeholder(R.drawable.image_one).fit().into(holder.imageView);
        String timeago = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeago);
        //update profile

        collectionReferenceprofile.whereEqualTo("userId", JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        DocumentReference documentReference = db.collection("Profile_Picture").document(document.getId());
                        String url = document.getString("imageurl");
                        if(!url.equals("")) {
                            Picasso.get().load(url).placeholder(R.drawable.outline_account_circle_black_20).fit().into(holder.profile);
                        }
                    }
                }

            }
        });

        //delete button
        holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                collectionReference.whereEqualTo("imageurl",journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                               DocumentReference documentReference = db.collection("Journal").document(document.getId());
                               documentReference.delete();
                              context.startActivity(new Intent(context,JournalListActivity.class));
                            }
                        } else {

                        }
                    }
                });
            }
        });

        // update Journal buttons
        holder.updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(context);
                LayoutInflater lainf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View view = lainf.inflate(R.layout.alertbox, null);
                builder.setView(view);
                android.app.AlertDialog alert = builder.create();
                EditText title_text = view.findViewById(R.id.update_title);
                EditText thought_text = view.findViewById(R.id.update_thoughts);
                Button update_button = view.findViewById(R.id.update_button);
                alert.show();
                collectionReference.whereEqualTo("imageurl", journal.getImageurl()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        for (QueryDocumentSnapshot snapshot : value) {
                            String Title = snapshot.getString("title").trim();
                            String thought = snapshot.getString("thoughts").trim();
                            title_text.setText(Title);
                            thought_text.setText(thought);
                        }
                    }
                });
                update_button.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String title_new = title_text.getText().toString().trim();
                        String thought_new = thought_text.getText().toString().trim();
                        collectionReference.whereEqualTo("imageurl", journal.getImageurl()).addSnapshotListener(new EventListener<QuerySnapshot>() {
                            @Override
                            public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                                for (QueryDocumentSnapshot snapshot : value) {
                                    DocumentReference documentReference = db.collection("Journal").document(snapshot.getId());
                                    documentReference.update("title", title_new);
                                    documentReference.update("thoughts", thought_new);
                                    context.startActivity(new Intent(context,JournalListActivity.class));

                                }
                            }
                        });

                    }
                });


//
            }
        });
        collectionReferenceLike.whereEqualTo("imageurl",journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (Objects.requireNonNull(documentSnapshot.getString("likedByuserId")).toLowerCase().equals(JournalApi.getInstance().getUserId().toLowerCase())) {
                            holder.like_button.setBackgroundColor(Color.rgb(255, 0, 0));
                        }

                    }
                }
            }
        });
        if(!journal.getRecentLiked().equals(" ")) {
            if(Integer.parseInt(journal.getLikes()) == 1) {
                holder.likes_textView.setText(String.format("Liked By %s", journal.getRecentLiked()));
            }else {
                holder.likes_textView.setText(String.format("Liked By %s and %s Others", journal.getRecentLiked(), journal.getLikes()));
            }
        }
        else
        {
            holder.likes_textView.setText("Liked by 0");

        }

        // setup comment button
        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("context","journal");
                intent.putExtra("imageurl",journal.getImageurl());
                context.startActivity(intent);

            }
        });
        // setup LikeList
        holder.likes_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LikesActivity.class);
                intent.putExtra("context","journal");
                intent.putExtra("imageurl",journal.getImageurl());
                context.startActivity(intent);
            }
        });

        // setting up the like button
        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LikedorNotJournal likedorNotJournal =new LikedorNotJournal(journal,holder);


                Thread t1 = new Thread()
                {
                    public void run()
                    {
                        likedorNotJournal.checklikedornot();

                    }
                };
                t1.start();
                Log.d("test like", "onSuccess: getting here " + String.valueOf(likedorNotJournal.getResult()));


            }





      });


    }

    @Override
    public int getItemCount() {
        return journalList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateAdded,name,likes_textView;
        public ImageView imageView;
        public ImageButton deleteButton,updateButton;
        public ImageView like_button,comment_button;
        public CircleImageView profile;
        String userId;
        String username;
        public ViewHolder(@NonNull @NotNull View itemView,Context ctx) {
            super(itemView);
            context =ctx;
            title = itemView.findViewById(R.id.journal_title_list);
            thoughts = itemView.findViewById(R.id.journal_thought_list);
            dateAdded = itemView.findViewById(R.id.jouranl_timestamp_list);
            imageView = itemView.findViewById(R.id.journal_image_List);
           name = itemView.findViewById(R.id.journalrow_Username);
            profile = itemView.findViewById(R.id.jouranl_row_profile);
           updateButton = itemView.findViewById(R.id.update_journal);
           deleteButton =itemView.findViewById(R.id.delete_journal);
           like_button = itemView.findViewById(R.id.like_button_journal);
           comment_button = itemView.findViewById(R.id.comment_journal);
           likes_textView = itemView.findViewById(R.id.likedList_journal);
        }
    }
}
