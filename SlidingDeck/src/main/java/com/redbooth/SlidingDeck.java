package com.redbooth;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListAdapter;

public class SlidingDeck extends ViewGroup {
    private final static int FIRST_VIEW = 0;
    private final static int MAXIMUM_ITEMS_ON_SCREEN = 4;
    private final static int MINIMUM_TOP_BOTTOM_OFFSET_DP = 10;
    private final static int MINIMUM_LEFT_RIGHT_OFFSET_DP = 15;
    private View[] viewsBuffer;
    private ListAdapter adapter;
    private SlidingDeckTouchController touchController;

    public void setAdapter(ListAdapter adapter) {
        this.adapter = adapter;
        viewsBuffer = new View[MAXIMUM_ITEMS_ON_SCREEN];
        attachChildViews();
        requestLayout();
    }

    public SlidingDeck(Context context) {
        super(context);
        initializeView();
    }

    public SlidingDeck(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeView();
    }

    public SlidingDeck(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeView();
    }

    @SuppressWarnings("unused")
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SlidingDeck(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        initializeView();
    }

    private void initializeView() {
        viewsBuffer = new View[MAXIMUM_ITEMS_ON_SCREEN];
        touchController = new SlidingDeckTouchController(this);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return touchController.onInterceptTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft;
        int childTop;
        int childRight;
        int childBottom;
        for (int index = FIRST_VIEW; index < getChildCount(); index++) {
            final View childView = getChildAt(index);
            childLeft = calculateViewLeft(left, index);
            childTop = calculateViewTop(bottom,   childView.getMeasuredHeight(), index);
            childRight = childLeft + childView.getMeasuredWidth();
            childBottom = childTop + childView.getMeasuredHeight();
            childView.layout(childLeft, childTop, childRight, childBottom);
        }
    }

    private int calculateViewLeft(int parentLeft, int zIndex) {
        int widthMinimumOffset = dp2px(MINIMUM_LEFT_RIGHT_OFFSET_DP);
        return parentLeft + getPaddingLeft() + ((widthMinimumOffset / 2)
                            * ((MAXIMUM_ITEMS_ON_SCREEN -1) -  zIndex));
    }

    private int calculateViewTop(int parentBottom, int viewHeight, int zIndex) {
        int topMinimumOffset = dp2px(MINIMUM_TOP_BOTTOM_OFFSET_DP);
        return parentBottom - getPaddingBottom() - viewHeight - (topMinimumOffset
                            * ((MAXIMUM_ITEMS_ON_SCREEN -1) - zIndex));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMeasureMode == MeasureSpec.AT_MOST) {
            int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            int viewHeight = calculateWrapContentHeight();
            setMeasuredDimension(viewWidth, viewHeight);
            configureChildViewsMeasureSpecs(widthMeasureSpec, heightMeasureSpec);
        } else {
            configureChildViewsMeasureSpecs(widthMeasureSpec, heightMeasureSpec);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private int calculateWrapContentHeight() {
        int maxChildHeight = 0;
        for (int index = 0; index < getChildCount(); index++) {
            final View childView = getChildAt(index);
            measureChildView(childView);
            if (childView.getVisibility() != View.GONE) {
                maxChildHeight = Math.max(maxChildHeight, getChildAt(index).getMeasuredHeight());
            }
        }
        int itemsElevationPadding = dp2px(MINIMUM_TOP_BOTTOM_OFFSET_DP)
                                                * (MAXIMUM_ITEMS_ON_SCREEN - 1);
        return maxChildHeight + getPaddingTop() + getPaddingBottom() + itemsElevationPadding;
    }

    private void configureChildViewsMeasureSpecs(int widthMeasureSpec, int heightMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec)
                                    - getPaddingLeft()
                                    - getPaddingRight();
        int viewWidth;
        int viewHeight;
        for (int index = FIRST_VIEW; index < getChildCount(); index++) {
            final View childView = getChildAt(index);
            measureChildView(childView);
            viewWidth = calculateViewWidth(parentWidth, index);
            viewHeight = childView.getMeasuredHeight();
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY);
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
        }
    }

    private int calculateViewWidth(int parentWidth, int zIndex) {
        int widthMinimumOffset = dp2px(MINIMUM_LEFT_RIGHT_OFFSET_DP);
        return (parentWidth - (widthMinimumOffset * ((MAXIMUM_ITEMS_ON_SCREEN - 1) - zIndex)));
    }

    private void measureChildView(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                     MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private void attachChildViews() {
        for (int position = FIRST_VIEW; position < adapter.getCount(); position++) {
            if (getChildCount() < MAXIMUM_ITEMS_ON_SCREEN) {
                viewsBuffer[position] = adapter.getView(position, viewsBuffer[position], this);
                addViewInLayout(viewsBuffer[position], FIRST_VIEW,
                                    viewsBuffer[position].getLayoutParams());
            }
        }
    }

    @Nullable
    View getFirstView() {
        if (getChildCount() > 0) {
            return getChildAt(getChildCount() - 1);
        } else {
            return null;
        }
    }

    private int dp2px(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getContext().getResources().getDisplayMetrics());
    }
}
