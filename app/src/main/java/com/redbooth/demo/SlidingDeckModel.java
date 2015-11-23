package com.redbooth.demo;

public class SlidingDeckModel {
    private final String title;
    private final String description;

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public SlidingDeckModel(String title, String description) {
        this.title = title;
        this.description = description;
    }
}
