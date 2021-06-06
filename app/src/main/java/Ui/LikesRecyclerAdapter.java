package Ui;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
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
import com.send_app.ajay_journal_project.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import util.Likes;

public class LikesRecyclerAdapter extends RecyclerView.Adapter<LikesRecyclerAdapter.ViewHolder> {

    private Context context;
    private List<Likes> likesList;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReferenceprofile = db.collection("Profile_Picture");

    public LikesRecyclerAdapter(){};
    public LikesRecyclerAdapter(Context context, List<Likes> likesList) {
        this.context = context;
        this.likesList = likesList;
    }

    @NonNull
    @NotNull
    @Override
    public LikesRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
       View view = LayoutInflater.from(context).inflate(R.layout.likesrow,parent,false);
        return new ViewHolder(view ,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull LikesRecyclerAdapter.ViewHolder holder, int position) {

        Likes likes = likesList.get(position);
        holder.username.setText(likes.getLikedByusername());
        collectionReferenceprofile.whereEqualTo("userId", likes.getLikedByuserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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


    }

    @Override
    public int getItemCount() {
        return likesList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        ImageButton profile;
        public ViewHolder(@NonNull @NotNull View itemView, Context ctx) {
            super(itemView);
            context = ctx;
            username = itemView.findViewById(R.id.username_likes);
            profile = itemView.findViewById(R.id.user_image_likes);

        }
    }
}
