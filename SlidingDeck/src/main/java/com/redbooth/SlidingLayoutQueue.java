package com.redbooth;

import android.support.v4.util.CircularArray;

class SlidingLayoutQueue  {
    private final CircularArray<SlidingLayout> queue;

    public SlidingLayoutQueue() {
        queue = new CircularArray<>();
    }

    public int size() {
        return queue.size();
    }

    public SlidingLayout getView(int position) {
        SlidingLayout result = null;
        if (position < queue.size()) {
            result = queue.get(position);
        }
        return result;
    }

    public void add(SlidingLayout view) {
        queue.addLast(view);
    }
}
