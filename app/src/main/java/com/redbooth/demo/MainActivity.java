package com.redbooth.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.redbooth.SlidingDeck;

public class MainActivity extends AppCompatActivity {
    private SlidingDeck slidingDeck;
    private SlidingDeckAdapter slidingAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeSlidingDeck();
    }

    private void initializeSlidingDeck() {
        slidingAdapter = new SlidingDeckAdapter(this);
        slidingAdapter.add(new SlidingDeckModel("Txus Ballesteros", "http://lorempixel.com/100/100/nature/", "Attach and emoji keyboard is over content", "When emoji keyboard or attach panel is visible, it stay over all the content (for example chat messages). This happens in chat, task and conversations."));
        slidingAdapter.add(new SlidingDeckModel("Fran Sirvent", "http://lorempixel.com/100/100/sports/", "Add Deep Link to Chat Detail", "This task let to the Redbooth Calls application do the integration with the Redbooth App and allow the navigation to chat details."));
        slidingAdapter.add(new SlidingDeckModel("Joan Fuentes", "http://lorempixel.com/100/100/city/", "Make the release process automated", "Make the release process automated"));
        slidingAdapter.add(new SlidingDeckModel("Ruben Serrano", "http://lorempixel.com/100/100/food/", "Create a list of potential blog posts", "Let's be evil :smiling_imp: I've created a list. As I've added some proposals, I've changed my :hankey: emoji by the :+1: one, and I've passed the task to the next one. Do the same Joan Fuentes , until everyone has added at least one proposal :smiling_imp:"));
        slidingAdapter.add(new SlidingDeckModel("Bimal Ghundhu", "http://lorempixel.com/100/100/cats/", "Apply improvements to Notifications list activity", "Apply cache changes for task lists, task details to notifications"));
        slidingAdapter.add(new SlidingDeckModel("Ben Falk", "http://lorempixel.com/100/100/nightlife/", "Fix FilesDownloader bad naming in local repository", "Some technical debt we created in a PR. This is karma."));
        slidingAdapter.add(new SlidingDeckModel("Annie Du", "http://lorempixel.com/100/100/transport/", "Investigate whats happening when development crashes", "This is so we have further disucssion around dealing with collisions in the code and how we can test them with spoon or jenkins or something else."));
        slidingDeck = (SlidingDeck)findViewById(R.id.slidingDeck);
        slidingDeck.setAdapter(slidingAdapter);
        slidingDeck.setSwipeEventListener(new SlidingDeck.SwipeEventListener() {
            @Override
            public void onSwipe(SlidingDeck view) {
                slidingAdapter.remove(slidingAdapter.getItem(0));
                slidingAdapter.notifyDataSetChanged();
            }
        });
    }
}
