package com.redbooth;

import android.view.MotionEvent;
import android.view.View;

class SlidingDeckTouchController implements View.OnTouchListener {
    public SlidingDeckTouchController(View view) {
        view.setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return false;
    }
}
