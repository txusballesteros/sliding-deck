package com.redbooth;

import android.view.MotionEvent;
import android.view.View;

class SlidingDeckTouchController {
    private static final int INITIAL_POSITION = 0;
    private static final int INITIAL_OFFSET = 0;
    private static final int MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX = 20;
    private int accumulatedOffsetX = INITIAL_OFFSET;
    private int accumulatedOffsetY = INITIAL_OFFSET;
    private int initialPositionX = INITIAL_POSITION;
    private int initialPositionY = INITIAL_POSITION;
    private final SlidingDeck ownerView;
    private MotionType motionType = MotionType.UNKNOWN;

    enum MotionType {
        UNKNOWN,
        HORIZONTAL,
        VERTICAL
    }

    SlidingDeckTouchController(SlidingDeck ownerView) {
        this.ownerView = ownerView;
    }

    boolean onInterceptTouchEvent(MotionEvent event) {
        int motionAction = event.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                initialPositionX = (int)event.getX();
                initialPositionY = (int)event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                int currentPositionX = (int)event.getX();
                int currentPositionY = (int)event.getY();
                int currentHorizontalOffset = currentPositionX - initialPositionX;
                int currentVerticalOffset = currentPositionY - initialPositionY;
                if (checkMinimumMovementTrigger(currentHorizontalOffset, currentVerticalOffset)) {
                    accumulatedOffsetX += currentHorizontalOffset;
                    accumulatedOffsetY += currentVerticalOffset;
                    if (motionType == MotionType.UNKNOWN) {
                        motionType = getMotionType();
                    }
                    applyOffsets(currentHorizontalOffset, accumulatedOffsetY);
                    initialPositionX = (int)event.getX();
                    initialPositionY = (int)event.getY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                if (motionType == MotionType.VERTICAL) {
                    ownerView.collapseVerticalOffset();
                } else {
                    applyOffsets(-accumulatedOffsetX, -accumulatedOffsetY);
                }
                initialPositionX = INITIAL_POSITION;
                initialPositionY = INITIAL_POSITION;
                accumulatedOffsetX = INITIAL_OFFSET;
                accumulatedOffsetY = INITIAL_OFFSET;
                motionType = MotionType.UNKNOWN;
                break;
        }
        return false;
    }

    private void applyOffsets(int horizontalOffset, int verticalOffset) {
        if (motionType == MotionType.HORIZONTAL) {
            applyHorizontalMotion(horizontalOffset);
        } else if (motionType == MotionType.VERTICAL) {
            applyVerticalMotion(verticalOffset);
        }
    }

    private MotionType getMotionType() {
        MotionType result = MotionType.HORIZONTAL;
        if (Math.abs(accumulatedOffsetY) > Math.abs(accumulatedOffsetX)) {
            result = MotionType.VERTICAL;
        }
        return result;
    }

    private boolean checkMinimumMovementTrigger(int currentHorizontalOffset, int currentVerticalOffset) {
        return Math.abs(currentHorizontalOffset) >= MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX ||
               Math.abs(currentVerticalOffset) >= MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX ||
               Math.abs(accumulatedOffsetX) >= MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX;
    }

    private void applyHorizontalMotion(int offset) {
        final View view = ownerView.getFirstView();
        if (view != null) {
            view.offsetLeftAndRight(offset);
        }
    }

    private void applyVerticalMotion(int offset) {
        ownerView.setOffsetTopBottom(offset);
    }
}
