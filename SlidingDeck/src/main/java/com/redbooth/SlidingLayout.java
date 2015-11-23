package com.redbooth;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

class SlidingLayout extends FrameLayout {
    public static final int WITHOUT_MARGIN = 0;
    public static final int MINIMUM_OFFSET_TOP_BOTTOM_IN_DP = 10;
    public static final int MINIMUM_OFFSET_LEFT_RIGHT_IN_DP = 10;
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
        float minimumMargin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                        MINIMUM_OFFSET_LEFT_RIGHT_IN_DP,
                                                        getResources().getDisplayMetrics());
        FrameLayout.LayoutParams params = new FrameLayout
                        .LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                      ViewGroup.LayoutParams.MATCH_PARENT);
        int margin = (int)(minimumMargin * zIndex);
        params.setMargins(margin, WITHOUT_MARGIN, margin, WITHOUT_MARGIN);
        setLayoutParams(params);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        applyOffsets();
    }

    private void applyOffsets() {
        if (zIndex > 0) {
            int topBottomOffset = calculateTopAndBottomOffset();
            offsetTopAndBottom(topBottomOffset);
        }
    }

    private int calculateTopAndBottomOffset() {
        float minimumOffset = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                                                        MINIMUM_OFFSET_TOP_BOTTOM_IN_DP,
                                                        getResources().getDisplayMetrics());
        float offset = minimumOffset * zIndex;
        if (zIndex > 1) {
            offset = offset - (minimumOffset * (zIndex / 10f));
        }
        return (int)offset;
    }
}
