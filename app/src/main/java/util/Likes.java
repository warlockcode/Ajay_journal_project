package util;


import android.text.format.DateUtils;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Comparator;

public class Likes {
    String likedByuserId ;
    String likedByusername;
    String imageurl;
    Timestamp timeago;



    public Likes(){};

    public Likes(String likedByuserId, String likedByusername, String imageurl,Timestamp timeago) {
        this.likedByuserId = likedByuserId;
        this.likedByusername = likedByusername;
        this.imageurl = imageurl;
        this.timeago = timeago;
    }
    public static Comparator<Likes> likesComparator = new Comparator<Likes>() {
        @Override
        public int compare(Likes o1, Likes o2) {
            return o2.timeago.compareTo(o1.timeago);
        }
    };
    public String getLikedByuserId() {
        return likedByuserId;
    }

    public void setLikedByuserId(String likedByuserId) {
        this.likedByuserId = likedByuserId;
    }

    public String getLikedByusername() {
        return likedByusername;
    }

    public void setLikedByusername(String likedByusername) {
       this.likedByusername = likedByusername;
    }

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }
    public Timestamp getTimeago() {
        return timeago;
    }

    public void setTimeago(Timestamp timeago) {
        this.timeago = timeago;
    }


}
