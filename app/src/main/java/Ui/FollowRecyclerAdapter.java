package Ui;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.api.LogDescriptor;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firestore.v1.Value;
import com.send_app.ajay_journal_project.FollowActivity;
import com.send_app.ajay_journal_project.R;
import com.send_app.ajay_journal_project.databinding.FollowActivityRowBinding;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import util.FollowList;
import util.Follow_collection;
import util.Journal;
import util.JournalApi;

public class FollowRecyclerAdapter extends RecyclerView.Adapter<FollowRecyclerAdapter.ViewHolder>  {
    private Context context;
    private List<FollowList> followList;
    private Follow_collection follow_collection;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReference = db.collection("Follow");
    CollectionReference collectionReferenceusers = db.collection("Users");
    CollectionReference collectionReferenceprofile = db.collection("Profile_Picture");

    public FollowRecyclerAdapter()
    {}

    public FollowRecyclerAdapter(Context context, List<FollowList> followList) {
        this.context = context;
        this.followList = followList;
    }

    @NonNull
    @NotNull
    @Override
    public FollowRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.follow_activity_row,parent,false);
        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull FollowRecyclerAdapter.ViewHolder holder, int position) {
        FollowList followlist = followList.get(position);
        holder.username.setText(followlist.getUsername());
        // profile piccture
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

        holder.follow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JournalApi journalApi = JournalApi.getInstance();

                Map<String,String> userobj = new HashMap<>();
                userobj.put("FollowedUserID", followlist.getUserId());
                userobj.put("FollowedUsername", followlist.getUsername());
                userobj.put("FollowingUserID", journalApi.getUserId());
                userobj.put("FollowingUsername", journalApi.getUsername());


                collectionReference.add(userobj).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        collectionReferenceusers.whereEqualTo("userID",followlist.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference documentReference = db.collection("Users").document(document.getId());
                                        String follwednumber = document.getString("Followed");
                                        int number = Integer.parseInt(follwednumber);
                                        number += 1;
                                        documentReference.update("Followed", Integer.toString(number));

                                    }
                                }
                                }
                            });
                        collectionReferenceusers.whereEqualTo("userID",journalApi.getUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                if(task.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                        DocumentReference documentReference = db.collection("Users").document(document.getId());
                                        String  follwingnumber = document.getString("Following");
                                        int number = Integer.parseInt(follwingnumber);
                                        number += 1;
                                        documentReference.update("Following", Integer.toString(number));

                                    }
                                }
                            }
                        });
//
                       context.startActivity(new Intent(context,FollowActivity.class));

                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull @NotNull Exception e) {
                        Log.d("follow_collection","failed") ;
                    }
                });
            }
        });

    }
    public void updateList(List<FollowList> list){
        followList = list;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return followList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageButton follow,profile;
        public ViewHolder(@NonNull @NotNull View itemView,Context cxt) {
            super(itemView);
            context =cxt;
            username = itemView.findViewById(R.id.username_follow);
            follow = itemView.findViewById(R.id.follow_user_id);
            profile = itemView.findViewById(R.id.user_image);
        }
    }
}
