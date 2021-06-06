package util;

import android.app.Application;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import Ui.HomeRecyclerAdapter;
import kotlin.jvm.Synchronized;

public class Likedornot extends Thread{
    public boolean result= true;
    public Likedornot(){};
    HomeRecyclerAdapter.ViewHolder holder;
    Journal journal = new Journal();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference collectionReferenceLike = db.collection("Like");
    CollectionReference collectionReferenceJournal = db.collection("Journal");
    public Likedornot(Journal journal, HomeRecyclerAdapter.ViewHolder holder)
    {
            this.journal = journal;
            this.holder = holder;
    }


      synchronized public void checklikedornot() {
        try {

            // Displaying the thread that is running
            Log.d("test remove like", "onSuccess: getting here ");
            collectionReferenceLike.whereEqualTo("imageurl", journal.getImageurl()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    Log.d("test remove like", "onSuccess: getting here ");
                    if (!queryDocumentSnapshots.isEmpty()) {
                        Log.d("test remove like", "onSuccess: getting here ");
                        for (com.google.firebase.firestore.DocumentSnapshot DocumentSnapshot : queryDocumentSnapshots) {
                            if (DocumentSnapshot.getString("likedByuserId").toLowerCase().equals(JournalApi.getInstance().getUserId().toLowerCase())) {

                                DocumentReference documentReference = db.collection("Like").document(DocumentSnapshot.getId());
                                holder.like_button.setBackgroundColor(Color.rgb(255, 255, 255));
                                documentReference.delete();
                                result =false;
                                collectionReferenceJournal.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot snapshot : task.getResult()) {
                                                DocumentReference documentReference1 = db.collection("Journal").document(snapshot.getId());
                                                int likes = Integer.parseInt(Objects.requireNonNull(snapshot.getString("likes")));
                                                likes -= 1;
                                                documentReference1.update("likes", String.valueOf(likes));
                                                collectionReferenceLike.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                        List<Likes> likesList = new ArrayList<>();
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                Likes likes = documentSnapshot.toObject(Likes.class);
                                                                likesList.add(likes);

                                                            }
                                                            if (!likesList.isEmpty()) {
                                                                likesList.sort(Likes.likesComparator);
                                                                Likes recent_like = likesList.get(0);
                                                                if (likesList.size() == 1) {
                                                                    holder.likes_textView.setText(String.format("Liked By %s", recent_like.getLikedByusername()));
                                                                } else {
                                                                    holder.likes_textView.setText(String.format("Liked By %s and %s Others", recent_like.getLikedByusername(), String.valueOf(likesList.size() - 1)));

                                                                }

                                                            }
                                                            else
                                                            {
                                                                holder.likes_textView.setText("Liked By 0");
                                                            }
                                                        }
                                                    }

                                                });

                                            }
                                        }
                                    }

                                });
                            }


                        }  addlike(result);

                    }

                    else
                    {
                        Log.d("test add like", "onSuccess: getting here ");

                        Likes likes = new Likes();
                        likes.setImageurl(journal.getImageurl());
                        likes.setLikedByuserId(JournalApi.getInstance().getUserId());
                        likes.setLikedByusername(JournalApi.getInstance().getUsername());
                        likes.setTimeago(Timestamp.now());
//                                                Map<String, String> likes = new HashMap<>();
//                                                likes.put("imageurl", journal.getImageurl());
//                                                likes.put("LikedByuserId", JournalApi.getInstance().getUserId());
//                                                likes.put("LikedByusername", JournalApi.getInstance().getUsername());

                        collectionReferenceLike.add(likes).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                holder.like_button.setBackgroundColor(Color.rgb(255, 0, 0));
                                collectionReferenceJournal.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> taskJournal) {

                                        if (taskJournal.isSuccessful()) {
                                            for (QueryDocumentSnapshot snapshot : taskJournal.getResult()) {
                                                DocumentReference documentReference1 = db.collection("Journal").document(snapshot.getId());
                                                int likes = Integer.parseInt(Objects.requireNonNull(snapshot.getString("likes")));
                                                likes += 1;
                                                String likesS = String.valueOf(likes);
                                                documentReference1.update("likes", likesS);
                                                collectionReferenceLike.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                                        List<Likes> likesList = new ArrayList<>();
                                                        if (task.isSuccessful()) {
                                                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                                Likes likes = documentSnapshot.toObject(Likes.class);
                                                                likesList.add(likes);

                                                            }
                                                            if (!likesList.isEmpty()) {
                                                                likesList.sort(Likes.likesComparator);
                                                                Likes recent_like = likesList.get(0);
                                                                if (likesList.size() == 1) {
                                                                    holder.likes_textView.setText(String.format("Liked By %s", recent_like.getLikedByusername()));
                                                                } else {
                                                                    holder.likes_textView.setText(String.format("Liked By %s and %s Others", recent_like.getLikedByusername(), String.valueOf(likesList.size() - 1)));

                                                                }
                                                            }
                                                            else
                                                            {
                                                                holder.likes_textView.setText("Liked By 0");
                                                            }
                                                        }
                                                    }

                                                });



                                            }
                                        }


                                    }
                                });
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull @NotNull Exception e) {

                            }
                        });






                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });

        } catch (Exception e) {
            // Throwing an exception
            System.out.println("Exception is caught");
        }
    }

    public boolean getResult(){

        return  result;

    }
    synchronized public void addlike(boolean result1) {
        if(result1 == true)
        {
            Log.d("test add like method", "onSuccess: getting here ");

            Likes likes = new Likes();
            likes.setImageurl(journal.getImageurl());
            likes.setLikedByuserId(JournalApi.getInstance().getUserId());
            likes.setLikedByusername(JournalApi.getInstance().getUsername());
            likes.setTimeago(Timestamp.now());
//                                                Map<String, String> likes = new HashMap<>();
//                                                likes.put("imageurl", journal.getImageurl());
//                                                likes.put("LikedByuserId", JournalApi.getInstance().getUserId());
//                                                likes.put("LikedByusername", JournalApi.getInstance().getUsername());

            collectionReferenceLike.add(likes).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                @Override
                public void onSuccess(DocumentReference documentReference) {
                    holder.like_button.setBackgroundColor(Color.rgb(255, 0, 0));
                    collectionReferenceJournal.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> taskJournal) {

                            if (taskJournal.isSuccessful()) {
                                for (QueryDocumentSnapshot snapshot : taskJournal.getResult()) {
                                    DocumentReference documentReference1 = db.collection("Journal").document(snapshot.getId());
                                    int likes = Integer.parseInt(Objects.requireNonNull(snapshot.getString("likes")));
                                    likes += 1;
                                    String likesS = String.valueOf(likes);
                                    documentReference1.update("likes", likesS);
                                    collectionReferenceLike.whereEqualTo("imageurl", journal.getImageurl()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                                            List<Likes> likesList = new ArrayList<>();
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    Likes likes = documentSnapshot.toObject(Likes.class);
                                                    likesList.add(likes);

                                                }
                                                if (!likesList.isEmpty()) {
                                                    likesList.sort(Likes.likesComparator);
                                                    Likes recent_like = likesList.get(0);
                                                    if (likesList.size() == 1) {
                                                        holder.likes_textView.setText(String.format("Liked By %s", recent_like.getLikedByusername()));
                                                    } else {
                                                        holder.likes_textView.setText(String.format("Liked By %s and %s Others", recent_like.getLikedByusername(), String.valueOf(likesList.size() - 1)));

                                                    }
                                                }
                                                else
                                                {
                                                    holder.likes_textView.setText("Liked By 0");
                                                }
                                            }
                                        }

                                    });



                                }
                            }


                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull @NotNull Exception e) {

                }
            });





        }



    }




}
