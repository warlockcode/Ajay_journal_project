package Ui;

import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.send_app.ajay_journal_project.CommentActivity;
import com.send_app.ajay_journal_project.R;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Comment;

import java.util.List;
import java.util.Objects;


import de.hdodenhof.circleimageview.CircleImageView;
import util.Comments;
import util.Journal;
import util.JournalApi;

public
class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder> {
    Context context ;
    private List<Comments> commentsList ;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference   collectionReferenceprofile = db.collection("Profile_Picture");
    CollectionReference   collectionReferenceJournal = db.collection("Journal");
    CollectionReference   collectionReferencecomments = db.collection("Comments");

    public CommentRecyclerAdapter(){};

    public CommentRecyclerAdapter(Context context, List<Comments> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @NotNull
    @Override
    public CommentRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context).inflate(R.layout.commentrow,parent,false);

        return new ViewHolder(view,context);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull CommentRecyclerAdapter.ViewHolder holder, int position) {

        Comments comment = commentsList.get(position);
        holder.username.setText(comment.getCommentedbyusername());
        holder.comment.setText(comment.getComment());
        // String timeago = (String) DateUtils.getRelativeTimeSpanString(journal.getTimeAdded().getSeconds() * 1000);
        String time = (String) DateUtils.getRelativeTimeSpanString(comment.getTimeago().getSeconds()*1000);
        holder.timeago_textview.setText(time);
        //delete button setup
        holder.delete_comment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

               collectionReferencecomments.whereEqualTo("imageurl",comment.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                   @Override
                   public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {

                    if(task.isSuccessful())
                    {
                        Log.d("at delete", "onComplete: commenturl"+comment.getComment());
                        for(QueryDocumentSnapshot documentSnapshot : Objects.requireNonNull(task.getResult()))
                        {
                            Comments commentsdata = documentSnapshot.toObject(Comments.class);
                            if(Objects.requireNonNull( DateUtils.getRelativeTimeSpanString(commentsdata.getTimeago().getNanoseconds())).equals(DateUtils.getRelativeTimeSpanString(comment.getTimeago().getNanoseconds())))
                            {

                                DocumentReference documentReference = db.collection("Comments").document(documentSnapshot.getId());
                                collectionReferenceJournal.whereEqualTo("imageurl",comment.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            for (QueryDocumentSnapshot documentSnapshot1 : task.getResult())
                                            {
                                                String AdminUsername = documentSnapshot1.getString("userId");

                                                assert AdminUsername != null;
                                                if(JournalApi.getInstance().getUserId().toLowerCase().equals(AdminUsername.toLowerCase().toLowerCase())|| JournalApi.getInstance().getUserId().toLowerCase().equals(comment.getCommentedbyUserId().toLowerCase())) {


                                                    documentReference.delete();
                                                    Intent intent = new Intent(context,CommentActivity.class);
                                                    intent.putExtra("context","home");
                                                    intent.putExtra("imageurl",comment.getImageurl());
                                                    context.startActivity(intent);


                                                }

                                            }
                                        }



                                    }
                                });


                            }



                        }
                    }





                   }
               });




            }
        });
        collectionReferenceprofile.whereEqualTo("userId", comment.getCommentedbyUserId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
        return commentsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private CircleImageView profile;
        private ImageButton delete_comment;
        private TextView username,comment,timeago_textview;

        public ViewHolder(@NonNull @NotNull View itemView, Context cxt)
        {
            super(itemView);
            context = cxt;
            profile = itemView.findViewById(R.id.user_image_comment);
            username = itemView.findViewById(R.id.username_comment);
            comment = itemView.findViewById(R.id.comment_text);
            timeago_textview =itemView.findViewById(R.id.timeago_commentrow);
            delete_comment = itemView.findViewById(R.id.delete_comment_button);

        }
    }
}
