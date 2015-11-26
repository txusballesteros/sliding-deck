package com.redbooth.demo;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

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
        for (int i = 1; i <= 3 ; i++) {
            String title = String.format("Title %d", i);
            String description = String.format("Description %d", i);
            slidingAdapter.add(new SlidingDeckModel(title, description));
        }
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
