package util;

import java.util.Comparator;

public
class FollowList {
    private String username;

    private String userId;

    public FollowList(){};

    public FollowList(String username, String userId) {
        this.username = username;
        this.userId = userId;
    }
    public static Comparator<FollowList> sortbyAzname = new Comparator<FollowList>() {
        @Override
        public int compare(FollowList o1, FollowList o2) {
            return o1.username.compareTo(o2.username);
        }
    };

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


}
