package com.redbooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.FrameLayout;

public class SlidingDeck extends FrameLayout {
    private final static int MAXIMUM_ELEMENTS_ON_SCREEN = 4;
    private SlidingLayoutQueue itemsQueue =  new SlidingLayoutQueue();
    private Adapter adapter;

    private DataSetObserver adapterObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidateItemsQueue();
        }

        @Override
        public void onInvalidated() {
            invalidateItemsQueue();
        }
    };

    public void setAdapter(@NonNull Adapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("The adapter cannot be null.");
        }
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(adapterObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(adapterObserver);
        invalidateItemsQueue();
    }

    public SlidingDeck(Context context) {
        super(context);
    }

    public SlidingDeck(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SlidingDeck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingDeck(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    private void invalidateItemsQueue() {
        for (int position = 0; position < MAXIMUM_ELEMENTS_ON_SCREEN; position++) {
            if (position < MAXIMUM_ELEMENTS_ON_SCREEN) {
                SlidingLayout slidingLayout = itemsQueue.getView(position);
                if (slidingLayout != null) {
                    View convertView = slidingLayout.getPrimitiveView();
                    adapter.getView(position, convertView, this);
                } else {
                    View convertView = adapter.getView(position, null, this);
                    slidingLayout = new SlidingLayout(getContext(), convertView, position);
                    itemsQueue.add(slidingLayout);
                }
            }
        }
        removeAllViews();
        for (int position = 0; position < itemsQueue.size(); position++) {
            addView(itemsQueue.getView(position), 0);
        }
    }
}
