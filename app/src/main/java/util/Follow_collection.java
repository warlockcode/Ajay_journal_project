package util;

import android.app.Activity;
import android.app.Application;

public class Follow_collection extends Application {

    private String followingUsername;
    private String followingUserid;
    private String followedUsername;
    private String followedUserid;
    public Follow_collection(){};
    public Follow_collection(String followingUsername, String followingUserid, String followedUsername, String followedUserid) {
        this.followingUsername = followingUsername;
        this.followingUserid = followingUserid;
        this.followedUsername = followedUsername;
        this.followedUserid = followedUserid;
    }

    public String getFollowingUsername() {
        return followingUsername;
    }

    public void setFollowingUsername(String followingUsername) {
        this.followingUsername = followingUsername;
    }

    public String getFollowingUserid() {
        return followingUserid;
    }

    public void setFollowingUserid(String followingUserid) {
        this.followingUserid = followingUserid;
    }

    public String getFollowedUsername() {
        return followedUsername;
    }

    public void setFollowedUsername(String followedUsername) {
        this.followedUsername = followedUsername;
    }

    public String getFollowedUserid() {
        return followedUserid;
    }

    public void setFollowedUserid(String followedUserid) {
        this.followedUserid = followedUserid;
    }
}
