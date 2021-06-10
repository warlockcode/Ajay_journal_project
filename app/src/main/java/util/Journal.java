package util;

import android.text.format.DateUtils;

import java.sql.Timestamp;
import java.util.Comparator;
import java.util.Date;

public class Journal {

    private String title;
    private String thoughts;
    private String imageurl;
    private String userId;
    private com.google.firebase.Timestamp timeAdded;
    private String username;
    private String likes;
    private String recentLiked;
    public Journal(){}

    public Journal(String title, String thoughts, String imageurl, String userId, com.google.firebase.Timestamp timeAdded, String username,String likes,String recentLiked) {
        this.title = title;
        this.thoughts = thoughts;
        this.imageurl = imageurl;
        this.userId = userId;
        this.timeAdded = timeAdded;
        this.username = username;
        this.likes = likes;
        this.recentLiked = recentLiked;
    }
    public static Comparator<Journal> journaltimeSort =new Comparator<Journal>() {
        @Override
        public int compare(Journal o1, Journal o2) {
           return  o2.timeAdded.compareTo(o1.timeAdded);
        }
    };


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getThoughts() {
        return thoughts;
    }

    public void setThoughts(String thoughts) {
        this.thoughts = thoughts;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public com.google.firebase.Timestamp  getTimeAdded() {
       return timeAdded;
    }

    public void setTimeAdded(com.google.firebase.Timestamp timeAdded) {
        this.timeAdded = timeAdded;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
    public String getLikes() {
        return likes;
    }

    public void setLikes(String likes) {
        this.likes = likes;
    }

    public String getRecentLiked() {
        return recentLiked;
    }

    public void setRecentLiked(String recentLiked) {
        this.recentLiked = recentLiked;
    }
}
