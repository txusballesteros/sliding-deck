package com.redbooth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

class SlidingLayout extends FrameLayout {
    private final View primitiveView;
    private final int zIndex;

    public View getPrimitiveView() {
        return primitiveView;
    }

    public SlidingLayout(@NonNull Context context, @NonNull View primitiveView, int zIndex) {
        super(context);
        this.primitiveView = primitiveView;
        this.zIndex = zIndex;
        initializeView();
        addView(primitiveView);
    }

    private void initializeView() {
        FrameLayout.LayoutParams params = new FrameLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                      ViewGroup.LayoutParams.MATCH_PARENT);
        setLayoutParams(params);
    }
}
