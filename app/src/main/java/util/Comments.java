package util;

import com.google.firebase.Timestamp;

import java.util.Comparator;

public
class Comments {

    String imageurl;
    String commentedbyUserId;
    String commentedbyusername;
    String comment;
    Timestamp timeago;
    // without parameter
    public Comments(){};

    public Comments(String imageurl, String commentedbyUserId, String commentedbyusername, String comment, Timestamp timeago) {
        this.imageurl = imageurl;
        this.commentedbyUserId = commentedbyUserId;
        this.commentedbyusername = commentedbyusername;
        this.comment = comment;
        this.timeago = timeago;
    }
    public static Comparator<Comments> commentsComparator = new Comparator<Comments>() {
        @Override
        public int compare(Comments o1, Comments o2) {
            return String.valueOf(o1.timeago.getNanoseconds()).compareTo(String.valueOf(o2.timeago.getNanoseconds()));
        }
    };

    public String getImageurl() {
        return imageurl;
    }

    public void setImageurl(String imageurl) {
        this.imageurl = imageurl;
    }

    public String getCommentedbyUserId() {
        return commentedbyUserId;
    }

    public void setCommentedbyUserId(String commentedbyUserId) {
        this.commentedbyUserId = commentedbyUserId;
    }

    public String getCommentedbyusername() {
        return commentedbyusername;
    }

    public void setCommentedbyusername(String commentedbyusername) {
        this.commentedbyusername = commentedbyusername;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getTimeago() {
        return timeago;
    }

    public void setTimeago(Timestamp timeago) {
        this.timeago = timeago;
    }
}

