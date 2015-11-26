package com.redbooth;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ListAdapter;

public class SlidingDeck extends ViewGroup {
    private final static int ANIMATION_DURATION_IN_MS = 200;
    private final static int INITIAL_OFFSET_IN_PX = 0;
    private final static int FIRST_VIEW = 0;
    private final static float MAXIMUM_OFFSET_TOP_BOTTOM_FACTOR = 0.75f;
    private final static float MAXIMUM_OFFSET_LEFT_RIGHT_FACTOR = 0.4f;
    private final static int MAXIMUM_ITEMS_ON_SCREEN = 5;
    private final static int MINIMUM_TOP_BOTTOM_OFFSET_DP = 4;
    private final static int MINIMUM_LEFT_RIGHT_OFFSET_DP = 8;
    private View[] viewsBuffer;
    private ListAdapter adapter;
    private SlidingDeckTouchController touchController;
    private int offsetTopBottom = INITIAL_OFFSET_IN_PX;
    private int offsetLeftRight = INITIAL_OFFSET_IN_PX;
    private int maximumOffsetTopBottom;
    private int maximumOffsetLeftRight;
    private boolean performingSwipe = false;
    private SwipeEventListener swipeEventListener;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            attachChildViews();
        }
    };

    public void setAdapter(ListAdapter adapter) {
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        viewsBuffer = new View[MAXIMUM_ITEMS_ON_SCREEN];
        attachChildViews();
    }

    public void setSwipeEventListener(SwipeEventListener swipeEventListener) {
        this.swipeEventListener = swipeEventListener;
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
    public boolean onTouchEvent(MotionEvent event) {
        boolean touchEventCaptured = touchController.onTouchEvent(event);
        if (!touchEventCaptured) {
            touchEventCaptured = super.onTouchEvent(event);
        }
        return touchEventCaptured;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = MeasureSpec.getSize(heightMeasureSpec);
        int heightMeasureMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMeasureMode == MeasureSpec.AT_MOST) {
            viewWidth = MeasureSpec.getSize(widthMeasureSpec);
            viewHeight = calculateWrapContentHeight();
        }
        setMeasuredDimension(viewWidth, viewHeight);
        configureChildViewsMeasureSpecs(widthMeasureSpec);
    }

    private int calculateWrapContentHeight() {
        int maxChildHeight = 0;
        for (int index = FIRST_VIEW; index < getChildCount(); index++) {
            final View childView = getChildAt(index);
            measureChildView(childView);
            if (childView.getVisibility() != View.GONE) {
                maxChildHeight = Math.max(maxChildHeight, getChildAt(index).getMeasuredHeight());
            }
        }
        int itemsElevationPadding = dp2px(MINIMUM_TOP_BOTTOM_OFFSET_DP) * getViewsCount();
        int measuredHeight = maxChildHeight + getPaddingTop() + getPaddingBottom() + itemsElevationPadding;
        int measuredOffset = offsetTopBottom * getViewsCount();
        return measuredHeight + measuredOffset;
    }

    private void configureChildViewsMeasureSpecs(int widthMeasureSpec) {
        int childWidthMeasureSpec;
        int childHeightMeasureSpec;
        final int parentWidth = MeasureSpec.getSize(widthMeasureSpec)
                - getPaddingLeft()
                - getPaddingRight();
        int viewWidth;
        int viewHeight;
        int minimumViewHeight = 0;
        int maximumViewWidth = 0;
        for (int index = FIRST_VIEW; index < getChildCount(); index++) {
            final View childView = getChildAt(index);
            measureChildView(childView);
            viewWidth = calculateViewWidth(parentWidth, index);
            viewHeight = childView.getMeasuredHeight();
            childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(viewWidth, MeasureSpec.EXACTLY);
            childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(viewHeight, MeasureSpec.EXACTLY);
            childView.measure(childWidthMeasureSpec, childHeightMeasureSpec);
            if (minimumViewHeight == 0) {
                minimumViewHeight = viewHeight;
            }
            if (maximumViewWidth == 0) {
                maximumViewWidth = viewWidth;
            }
            minimumViewHeight = Math.min(minimumViewHeight, viewHeight);
            maximumViewWidth = Math.max(maximumViewWidth, viewWidth);
        }
        maximumOffsetTopBottom = (int)(minimumViewHeight * MAXIMUM_OFFSET_TOP_BOTTOM_FACTOR);
        maximumOffsetLeftRight = (int)(maximumViewWidth * MAXIMUM_OFFSET_LEFT_RIGHT_FACTOR);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childLeft;
        int childTop;
        int childRight;
        int childBottom;
        for (int index = FIRST_VIEW; index < getChildCount(); index++) {
            if (isVisibleView(index)) {
                final View childView = getChildAt(index);
                childLeft = calculateViewLeft(left, right, childView.getMeasuredWidth(), index);
                childRight = childLeft + childView.getMeasuredWidth();
                childTop = calculateViewTop(bottom, childView.getMeasuredHeight(), index);
                childBottom = childTop + childView.getMeasuredHeight();
                childView.layout(childLeft, childTop, childRight, childBottom);
            }
        }
        if (getChildCount() > 1) {
            float viewAlpha = 0f;
            if (offsetLeftRight > 0) {
                viewAlpha = calculateCurrentLeftRightOffsetFactor();
            } else if (offsetTopBottom > 0) {
                viewAlpha = calculateCurrentTopBottomOffsetFactor();
            }
            getChildAt(FIRST_VIEW).setAlpha(viewAlpha);
        }
    }

    private int calculateViewLeft(int parentLeft, int parentRight, int childWith, int zIndex) {
        int center = parentLeft + ((parentRight - parentLeft) / 2);
        int result = center - (childWith / 2);
        if (isTheFromView(zIndex)) {
            result += offsetLeftRight;
        }
        return result;
    }

    private int calculateViewTop(int parentBottom, int viewHeight, int zIndex) {
        int topMinimumOffset = dp2px(MINIMUM_TOP_BOTTOM_OFFSET_DP);
        int viewTop = parentBottom - getPaddingBottom() - viewHeight - (topMinimumOffset
                * (getViewsCount() - zIndex));
        if (offsetTopBottom > 0) {
            viewTop -= getOffsetTopBottom(zIndex);
        }
        if (offsetLeftRight > 0 && isNotTheFromView(zIndex)) {
            viewTop += calculateOffsetLeftRight(MINIMUM_TOP_BOTTOM_OFFSET_DP);
        }
        return viewTop;
    }

    private int getViewsCount() {
        return (getChildCount() - 1);
    }

    private boolean isVisibleView(int zIndex) {
        final View view = getChildAt(zIndex);
        return view.getAlpha() > 0f && view.getVisibility() != View.GONE;
    }

    private boolean isTheFromView(int zIndex) {
        return !isNotTheFromView(zIndex);
    }

    private boolean isNotTheFromView(int zIndex) {
        return zIndex < getViewsCount();
    }

    private int getOffsetTopBottom(int zIndex) {
        int result = 0;
        if (isNotTheFromView(zIndex)) {
            result = offsetTopBottom * (getChildCount() - (zIndex + 1));
        }
        return result;
    }

    private int calculateOffsetLeftRight(int referenceValue) {
        float topMinimumOffset = dp2px(referenceValue);
        float offsetFactor = calculateCurrentLeftRightOffsetFactor();
        float result = (topMinimumOffset * offsetFactor);
        return (int)result;
    }

    private float calculateCurrentTopBottomOffsetFactor() {
        float offsetLimit = (maximumOffsetTopBottom * MAXIMUM_OFFSET_TOP_BOTTOM_FACTOR);
        float offsetFactor = ((float)offsetTopBottom / offsetLimit);
        if (offsetFactor > 1) {
            offsetFactor = 1f;
        }
        return offsetFactor;
    }

    private float calculateCurrentLeftRightOffsetFactor() {
        float offsetLimit = (maximumOffsetLeftRight * MAXIMUM_OFFSET_LEFT_RIGHT_FACTOR);
        float offsetFactor = ((float)offsetLeftRight / offsetLimit);
        if (offsetFactor > 1) {
            offsetFactor = 1f;
        }
        return offsetFactor;
    }

    private int calculateViewWidth(float parentWidth, int zIndex) {
        float widthMinimumOffsetFactor = getVerticalOffsetFactor();
        float widthMinimumOffset = dp2px(MINIMUM_LEFT_RIGHT_OFFSET_DP);
              widthMinimumOffset -= widthMinimumOffset * widthMinimumOffsetFactor;
        float viewWidth = (parentWidth - (widthMinimumOffset * (getViewsCount() - zIndex)));
        if (isNotTheFromView(zIndex)) {
            viewWidth += calculateOffsetLeftRight(MINIMUM_LEFT_RIGHT_OFFSET_DP);
        }
        return (int)viewWidth;
    }

    private float getVerticalOffsetFactor() {
        float result = 0f;
        if (Math.abs(offsetTopBottom) > 0) {
            result = (float)offsetTopBottom / (float)maximumOffsetTopBottom;
        }
        return result;
    }

    private void measureChildView(View view) {
        view.measure(MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
    }

    private void attachChildViews() {
        removeAllViews();
        for (int position = FIRST_VIEW; position < adapter.getCount(); position++) {
            if (position < MAXIMUM_ITEMS_ON_SCREEN) {
                viewsBuffer[position] = adapter.getView(position, viewsBuffer[position], this);
                addViewInLayout(viewsBuffer[position], FIRST_VIEW,
                                    viewsBuffer[position].getLayoutParams());
            }
        }
        requestLayout();
    }

    void collapseVerticalOffset() {
        if (offsetTopBottom > 0) {
            ValueAnimator animator = ValueAnimator.ofInt(offsetTopBottom, INITIAL_OFFSET_IN_PX);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(ANIMATION_DURATION_IN_MS);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetTopBottom = (int) animation.getAnimatedValue();
                    requestLayout();
                }
            });
            animator.start();
        }
    }

    void collapseHorizontalOffset() {
        if (offsetLeftRight > 0) {
            ValueAnimator animator = ValueAnimator.ofInt(offsetLeftRight, INITIAL_OFFSET_IN_PX);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(ANIMATION_DURATION_IN_MS);
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetLeftRight = (int) animation.getAnimatedValue();
                    requestLayout();
                }
            });
            animator.start();
        }
    }

    void setOffsetLeftRight(int offset) {
        if (!performingSwipe) {
            if (offset >= 0) {
                offsetLeftRight = offset;
                requestLayout();
            }
        }
    }

    void setOffsetTopBottom(int offset) {
        if (!performingSwipe) {
            if (offset >= 0) {
                if (offset > maximumOffsetTopBottom) {
                    offsetTopBottom = maximumOffsetTopBottom;
                } else {
                    offsetTopBottom = offset;
                }
                requestLayout();
            }
        }
    }

    void performHorizontalSwipe() {
        if (!performingSwipe && getChildCount() > 0) {
            performingSwipe = true;
            ValueAnimator animator = ValueAnimator.ofInt(offsetLeftRight, getMeasuredWidth());
            animator.setInterpolator(new AccelerateInterpolator());
            animator.setDuration(ANIMATION_DURATION_IN_MS);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) { }

                @Override
                public void onAnimationEnd(Animator animation) {
                    offsetLeftRight = 0;
                    performingSwipe = false;
                    removeItemFromAdapter();
                }

                @Override
                public void onAnimationCancel(Animator animation) { }

                @Override
                public void onAnimationRepeat(Animator animation) { }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetLeftRight = (int)animation.getAnimatedValue();
                    requestLayout();
                }
            });
            animator.start();
        }
    }

    void performReleaseTouch() {
        if (!performingSwipe) {
            if (offsetLeftRight > 0) {
                if (offsetLeftRight < maximumOffsetLeftRight) {
                    collapseHorizontalOffset();
                } else {
                    performHorizontalSwipe();
                }
            }
            if (offsetTopBottom > 0) {
                collapseVerticalOffset();
            }
        }
    }

    private void removeItemFromAdapter() {
        if (swipeEventListener != null) {
            swipeEventListener.onSwipe(this);
        }
    }

    private int dp2px(int value) {
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                value, getContext().getResources().getDisplayMetrics());
    }

    public interface SwipeEventListener {
        void onSwipe(SlidingDeck view);
    }
}