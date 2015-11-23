package com.redbooth;

import android.support.v4.util.CircularArray;

class SlidingLayoutQueue  {
    private final CircularArray<SlidingLayout> queue;
    private final int maxNumberOfElements;

    public SlidingLayoutQueue(int maxNumberOfElements) {
        this.maxNumberOfElements = maxNumberOfElements;
        queue = new CircularArray<>();
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
