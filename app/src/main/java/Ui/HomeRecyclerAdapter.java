package Ui;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.send_app.ajay_journal_project.CommentActivity;
import com.send_app.ajay_journal_project.LikesActivity;
import com.send_app.ajay_journal_project.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import util.Journal;
import util.JournalApi;
import util.Likedornot;
import util.Likes;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Journal> journal_list;



    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReferenceprofile = db.collection("Profile_Picture");
    CollectionReference collectionReferenceLike = db.collection("Like");
    CollectionReference collectionReferenceJournal = db.collection("Journal");
    public HomeRecyclerAdapter(){};

    public HomeRecyclerAdapter(Context context, List<Journal> journal_list) {
        this.context = context;
        this.journal_list = journal_list;
    }

    @NonNull
    @NotNull
    @Override
    public HomeRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
         View view = LayoutInflater.from(context).inflate(R.layout.homeactivityrow,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull HomeRecyclerAdapter.ViewHolder holder, int position) {
       Journal journal = journal_list.get(position);
        String imgUrl;
        holder.title.setText(journal.getTitle());
        holder.thoughts.setText(journal.getThoughts());
        holder.name.setText(journal.getUsername());
        imgUrl = journal.getImageurl();
        Picasso.get().load(imgUrl).placeholder(R.drawable.image_one).fit().into(holder.imageView);
        String timeago = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        holder.dateAdded.setText(timeago);



        //adding profile picture
        collectionReferenceprofile.whereEqualTo("userId", journal.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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


        collectionReferenceLike.whereEqualTo("imageurl",journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                 List<Likes> likesList = new ArrayList<>();
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        Likes likes =  documentSnapshot.toObject(Likes.class);
                        likesList.add(likes);

                    }
                    if(!likesList.isEmpty()) {
                        likesList.sort(Likes.likesComparator);
                        Likes recent_like = likesList.get(0);
                        if (likesList.size() == 1) {
                            holder.likes_textView.setText(String.format("Liked By %s", recent_like.getLikedByusername()));
                        } else {
                            holder.likes_textView.setText(String.format("Liked By %s and %s Others", recent_like.getLikedByusername(), String.valueOf(likesList.size() - 1)));

                        }
                    }
                }
            }

        });

        // setup comment button
        holder.comment_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("context","home");
                intent.putExtra("imageurl",journal.getImageurl());
                context.startActivity(intent);

            }
        });
        // setup LikeList
        holder.likes_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, LikesActivity.class);
                intent.putExtra("context","home");
                intent.putExtra("imageurl",journal.getImageurl());
                context.startActivity(intent);
            }
        });
        // setup like button
        holder.like_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Likedornot likedornot =new Likedornot(journal,holder);


                    Thread t1 = new Thread()
                    {
                        public void run()
                        {
                            likedornot.checklikedornot();

                        }
                    };
                    t1.start();
//                       collectionReferenceLike.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                                                    @Override
//                                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
//                                                        List<Likes> likesList = new ArrayList<>();
//                                                        if (task.isSuccessful()) {
//                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
//                                                                Likes likes = documentSnapshot.toObject(Likes.class);
//                                                                likesList.add(likes);
//
//                                                            }
//                                                            if (!likesList.isEmpty()) {
//                                                                likesList.sort(Likes.likesComparator);
//                                                                Likes recent_like = likesList.get(0);
//                                                                if (likesList.size() == 1) {
//                                                                    holder.likes_textView.setText(String.format("Liked By %s", recent_like.getLikedByusername()));
//                                                                } else {
//                                                                    holder.likes_textView.setText(String.format("Liked By %s and %s Others", recent_like.getLikedByusername(), String.valueOf(likesList.size() - 1)));
//
//                                                                }
//                                                            }
//                                                        }
//                                                    }
//
//                                                });
                Log.d("test like", "onSuccess: getting here " + String.valueOf(likedornot.getResult()));


            }





        });

    }

    @Override
    public int getItemCount() {
        return journal_list.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title,thoughts,dateAdded,name,likes_textView;
        public ImageView imageView;
        public ImageView like_button,comment_button;
        public CircleImageView profile;
        public ViewHolder(@NonNull @NotNull View itemView, Context cxt) {
            super(itemView);
            title = itemView.findViewById(R.id.home_title_list);
            thoughts = itemView.findViewById(R.id.home_thought_list);
            dateAdded = itemView.findViewById(R.id.home_timestamp_list);
            imageView = itemView.findViewById(R.id.home_image_List);
            name = itemView.findViewById(R.id.homerow_Username);
            profile = itemView.findViewById(R.id.home_row_profile);
            like_button = itemView.findViewById(R.id.like_button_home);
            likes_textView = itemView.findViewById(R.id.likedList_home);
            comment_button = itemView.findViewById(R.id.comment_home);
     //       imageView.setBackgroundColor(Color.rgb(255,0,0));

        }
    }
}
