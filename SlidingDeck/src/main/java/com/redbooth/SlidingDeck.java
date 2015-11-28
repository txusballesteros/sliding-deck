package com.redbooth;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ListAdapter;

public class SlidingDeck extends ViewGroup {
    private final static boolean ENABLE_OVERDRAW_IMPROVEMENT = false;
    private final static int ANIMATION_DURATION_IN_MS = 200;
    private final static int INITIAL_OFFSET_IN_PX = 0;
    private final static int FIRST_VIEW = 0;
    private final static float INITIAL_ALPHA_FOR_LATEST_ITEM = 0.25f;
    private final static float MAXIMUM_OFFSET_TOP_BOTTOM_FACTOR = 0.75f;
    private final static float MAXIMUM_OFFSET_LEFT_RIGHT_FACTOR = 0.4f;
    private final static int MAXIMUM_ITEMS_ON_SCREEN = 5;
    private final static int MINIMUM_TOP_BOTTOM_OFFSET_DP = 8;
    private final static int MINIMUM_LEFT_RIGHT_OFFSET_DP = 8;
    private View[] viewsBuffer;
    private ListAdapter adapter;
    private SlidingDeckTouchController touchController;
    private int offsetTopBottom = INITIAL_OFFSET_IN_PX;
    private int offsetLeftRight = INITIAL_OFFSET_IN_PX;
    private int initialViewHeight = -1;
    private int maximumOffsetTopBottom;
    private int maximumOffsetLeftRight;
    private boolean performingSwipe = false;
    private boolean expandedVertically = false;
    private SwipeEventListener swipeEventListener;
    private View emptyView;

    private DataSetObserver dataSetObserver = new DataSetObserver() {
        @Override
        public void onChanged() {
            attachChildViews();
            if (emptyView != null) {
                if (adapter.getCount() == 0) {
                    SlidingDeck.this.setVisibility(GONE);
                    emptyView.setVisibility(VISIBLE);
                } else {
                    SlidingDeck.this.setVisibility(VISIBLE);
                    emptyView.setVisibility(GONE);
                }
            }
        }
    };

    public void setEmptyView(@NonNull View emptyView) {
        if (emptyView == null) {
            throw new IllegalArgumentException("The empty view cannot be null.");
        }
        this.emptyView = emptyView;
        this.emptyView.setVisibility(GONE);
    }

    public void setAdapter(@NonNull ListAdapter adapter) {
        if (adapter == null) {
            throw new IllegalArgumentException("The adapter cannot be null.");
        }
        if (this.adapter != null) {
            this.adapter.unregisterDataSetObserver(dataSetObserver);
        }
        this.adapter = adapter;
        this.adapter.registerDataSetObserver(dataSetObserver);
        viewsBuffer = new View[MAXIMUM_ITEMS_ON_SCREEN];
        attachChildViews();
    }

    public boolean isExpanded() {
        return expandedVertically;
    }

    public void setSwipeEventListener(SwipeEventListener swipeEventListener) {
        this.swipeEventListener = swipeEventListener;
    }

    public void swipeForegroundItem() {
        swipeForegroundItem(swipeEventListener);
    }

    public void swipeForegroundItem(SwipeEventListener listener) {
        performHorizontalSwipe(listener);
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
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean result;
        int childIndex = getChildIndex(child);
        if (applyOverdrawImprovement(childIndex)) {
            Rect viewClip = calculateClippingViewRect(child, childIndex);
            canvas.save();
            canvas.clipRect(viewClip);
            result = super.drawChild(canvas, child, drawingTime);
            canvas.restore();
        } else {
            result = super.drawChild(canvas, child, drawingTime);
        }
        return result;
    }

    private boolean applyOverdrawImprovement(int viewIndex) {
        boolean result = false;
        if (ENABLE_OVERDRAW_IMPROVEMENT) {
            if (getChildCount() > 2) {
                if (viewIndex < (getChildCount() - 2)) {
                    result = true;
                }
            }
        }
        return result;
    }

    private int getChildIndex(@NonNull View child) {
        int result = 0;
        for (int index = 0; index < getChildCount(); index++) {
            final View item = getChildAt(index);
            if ((item.getTop() == child.getTop()) && (item.getLeft() == child.getLeft())) {
                result = index;
                break;
            }
        }
        return result;
    }

    private Rect calculateClippingViewRect(View currentView, int viewIndex) {
        int nextViewIndex = viewIndex + 1;
        final View nextView = getChildAt(nextViewIndex);
        int left = currentView.getLeft();
        int top = currentView.getTop();
        int right = currentView.getRight();
        int bottom = top + (nextView.getTop() - top);
        return new Rect(left, top, right, bottom);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        int viewHeight = calculateWrapContentHeight();
        if (getChildCount() > 0) {
            if (initialViewHeight == -1) {
                initialViewHeight = viewHeight;
            } else if (viewHeight < initialViewHeight) {
                viewHeight = initialViewHeight;
            }
        } else {
            initialViewHeight = -1;
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
            float viewAlpha = INITIAL_ALPHA_FOR_LATEST_ITEM;
            if (offsetLeftRight > 0) {
                viewAlpha = calculateCurrentLeftRightOffsetFactor();
            } else if (offsetTopBottom > 0) {
                viewAlpha = calculateCurrentTopBottomOffsetFactor();
            }
            if (viewAlpha < INITIAL_ALPHA_FOR_LATEST_ITEM) {
                viewAlpha = INITIAL_ALPHA_FOR_LATEST_ITEM;
            }
            getChildAt(FIRST_VIEW).setAlpha(viewAlpha);
        }
    }

    private int calculateViewLeft(int parentLeft, int parentRight, int childWith, int zIndex) {
        int center = parentLeft + ((parentRight - parentLeft) / 2);
        int result = center - (childWith / 2);
        if (isTheForegroundView(zIndex)) {
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
        if (offsetLeftRight > 0 && isNotTheForegroundView(zIndex)) {
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

    private boolean isTheForegroundView(int zIndex) {
        return !isNotTheForegroundView(zIndex);
    }

    private boolean isNotTheForegroundView(int zIndex) {
        return zIndex < getViewsCount();
    }

    private int getOffsetTopBottom(int zIndex) {
        int result = 0;
        if (isNotTheForegroundView(zIndex)) {
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
        float widthMinimumOffset = dp2px(MINIMUM_LEFT_RIGHT_OFFSET_DP);
        float maximumWidthOffset = (widthMinimumOffset * MAXIMUM_OFFSET_TOP_BOTTOM_FACTOR);
        float widthMinimumOffsetFactor = getVerticalOffsetFactor();
        float widthOffset = widthMinimumOffset * widthMinimumOffsetFactor;
        if (widthOffset < maximumWidthOffset) {
            widthMinimumOffset -= widthOffset;
        } else {
            widthMinimumOffset -= maximumWidthOffset;
        }
        float viewWidth = (parentWidth - (widthMinimumOffset * (getViewsCount() - zIndex)));
        if (isNotTheForegroundView(zIndex)) {
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
        if (adapter.getCount() == 0) {
            offsetTopBottom = 0;
            offsetLeftRight = 0;
            expandedVertically = false;
            viewsBuffer = new View[MAXIMUM_ITEMS_ON_SCREEN];
        }
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
            expandedVertically = false;
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
        if (!performingSwipe && !expandedVertically) {
            if (offset >= 0) {
                offsetLeftRight = offset;
                requestLayout();
            }
        }
    }

    void setOffsetTopBottom(int offset) {
        if (!performingSwipe && !expandedVertically) {
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
        performHorizontalSwipe(swipeEventListener);
    }

    void performHorizontalSwipe(final SwipeEventListener listener) {
        if (!performingSwipe && !expandedVertically && getChildCount() > 0) {
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
                    if (listener != null) {
                        listener.onSwipe(SlidingDeck.this);
                    }
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

    public void performVerticalSwipe() {
        if (!performingSwipe && getChildCount() > 0) {
            performingSwipe = true;
            int initialValue = offsetTopBottom;
            int endValue = maximumOffsetTopBottom;
            if (expandedVertically) {
                initialValue = maximumOffsetTopBottom;
                endValue = 0;
            }
            ValueAnimator animator = ValueAnimator.ofInt(initialValue, endValue);
            animator.setInterpolator(new DecelerateInterpolator());
            animator.setDuration(ANIMATION_DURATION_IN_MS);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {
                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (!expandedVertically) {
                        offsetTopBottom = maximumOffsetTopBottom;
                    } else {
                        offsetTopBottom = 0;
                    }
                    expandedVertically = !expandedVertically;
                    performingSwipe = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                }

                @Override
                public void onAnimationRepeat(Animator animation) {
                }
            });
            animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                @Override
                public void onAnimationUpdate(ValueAnimator animation) {
                    offsetTopBottom = (int) animation.getAnimatedValue();
                    Log.d("Swipe", String.format("%d", offsetTopBottom));
                    requestLayout();
                }
            });
            animator.start();
        }
    }

    void performReleaseTouch() {
        if (!performingSwipe && !expandedVertically) {
            if (offsetLeftRight > 0) {
                if (offsetLeftRight < maximumOffsetLeftRight) {
                    collapseHorizontalOffset();
                } else {
                    performHorizontalSwipe(swipeEventListener);
                }
            }
            if (offsetTopBottom > 0) {
                collapseVerticalOffset();
            }
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