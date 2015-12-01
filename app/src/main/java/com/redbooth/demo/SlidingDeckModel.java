package com.redbooth.demo;

import android.net.Uri;

public class SlidingDeckModel {
    private final String description;
    private final String userName;
    private final String avatarUrl;

    public String getDescription() {
        return description;
    }

    public String getName() {
        return userName;
    }

    public Uri getAvatarUri() {
        return Uri.parse(avatarUrl);
    }

    public SlidingDeckModel(String userName, String avatarUrl, String description) {
        this.description = description;
        this.userName = userName;
        this.avatarUrl = avatarUrl;
    }
}
