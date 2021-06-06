package Ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.send_app.ajay_journal_project.FollowActivity;
import com.send_app.ajay_journal_project.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import util.FollowList;
import util.JournalApi;

public class FollowedRecyclerAdapter extends RecyclerView.Adapter<FollowedRecyclerAdapter.ViewHolder> {
    private Context context;
    private List<FollowList> followList;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference  collectionReferenceprofile = db.collection("Profile_Picture");
    CollectionReference collectionReferenceuser = db.collection("Users");
    CollectionReference collectionReferenceFollow = db.collection("Follow");
    public FollowedRecyclerAdapter(){};
    public FollowedRecyclerAdapter(Context context, List<FollowList> followList) {
        this.context = context;
        this.followList = followList;
    }

    @NonNull
    @NotNull
    @Override
    public FollowedRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.followed_activity_row,parent,false);
        return new FollowedRecyclerAdapter.ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowedRecyclerAdapter.ViewHolder holder, int position) {

        FollowList followlist = followList.get(position);
        holder.username.setText(followlist.getUsername());
        // profile picture
        collectionReferenceprofile.whereEqualTo("userId", followlist.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

        holder.unfollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalApi journalApi = JournalApi.getInstance();
                collectionReferenceFollow.whereEqualTo("FollowingUserID",followlist.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                DocumentReference documentReference = db.collection("Follow").document(document.getId());
                                if(JournalApi.getInstance().getUserId().toLowerCase().equals(Objects.requireNonNull(document.getString("FollowedUserID")).toLowerCase()))
                                {
                                    documentReference.delete();
                                }
                                else
                                {
                                    Log.d("problem","here");
                                }


                            }
                        }
                        collectionReferenceuser.whereEqualTo("userID",JournalApi.getInstance().getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference documentReference = db.collection("Users").document(document.getId());
                                        String follwednumber = document.getString("Followed");
                                        int number = Integer.parseInt(follwednumber);
                                        number -= 1;
                                        documentReference.update("Followed", Integer.toString(number));

                                    }
                                }
                            }
                        });
                        collectionReferenceuser.whereEqualTo("userID",followlist.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference documentReference = db.collection("Users").document(document.getId());
                                        String  follwingnumber = document.getString("Following");
                                        int number = Integer.parseInt(follwingnumber);
                                        number -= 1;
                                        documentReference.update("Following", Integer.toString(number));

                                    }
                                }
                            }
                        });

                    }
                });
            }
        });


    }

    @Override
    public int getItemCount() {

        return followList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageButton unfollow,profile;
        public ViewHolder(@NonNull @NotNull View itemView, Context cxt) {
            super(itemView);
            context =cxt;
            username = itemView.findViewById(R.id.username_followed);
            unfollow= itemView.findViewById(R.id.followed_user_delete);
            profile = itemView.findViewById(R.id.user_image_follwed);


        }
    }
}
