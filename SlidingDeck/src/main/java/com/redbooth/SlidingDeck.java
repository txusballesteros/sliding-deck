package com.redbooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Adapter;

public class SlidingDeck extends View {
    private Adapter adapter;

    private DataSetObserver adapterObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            invalidate();
        }

        @Override
        public void onInvalidated() {
            invalidate();
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
        invalidate();
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

    @Override
    public void invalidate() {
        super.invalidate();
    }
}
