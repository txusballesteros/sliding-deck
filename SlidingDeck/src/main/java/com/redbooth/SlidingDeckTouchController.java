package com.redbooth;

import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;

class SlidingDeckTouchController {
    private static final int SNAP_VELOCITY = 5000;
    private static final int VELOCITY_UNITS = 1000;
    private static final int INITIAL_POSITION = 0;
    private static final int INITIAL_OFFSET = 0;
    private static final int MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX = 20;
    private int accumulatedOffsetX = INITIAL_OFFSET;
    private int accumulatedOffsetY = INITIAL_OFFSET;
    private int initialPositionX = INITIAL_POSITION;
    private int initialPositionY = INITIAL_POSITION;
    private final SlidingDeck ownerView;
    private MotionType motionType = MotionType.UNKNOWN;
    private VelocityTracker velocityTracker;

    enum MotionType {
        UNKNOWN,
        HORIZONTAL,
        VERTICAL
    }

    SlidingDeckTouchController(SlidingDeck ownerView) {
        this.ownerView = ownerView;
    }

    boolean onTouchEvent(MotionEvent event) {
        boolean result = false;
        int motionAction = event.getAction();
        switch (motionAction) {
            case MotionEvent.ACTION_DOWN:
                if (velocityTracker == null) {
                    velocityTracker = VelocityTracker.obtain();
                } else {
                    velocityTracker.clear();
                }
                velocityTracker.addMovement(event);
                initialPositionX = (int)event.getX();
                initialPositionY = (int)event.getY();
                result = true;
                break;
            case MotionEvent.ACTION_MOVE:
                velocityTracker.addMovement(event);
                velocityTracker.computeCurrentVelocity(VELOCITY_UNITS);
                float xVelocity = velocityTracker.getXVelocity();
                float yVelocity = velocityTracker.getYVelocity();
                if (Math.abs(xVelocity) >= SNAP_VELOCITY) {
                    ownerView.performHorizontalSwipe();
                } else if (Math.abs(yVelocity) >= SNAP_VELOCITY) {
                    ownerView.performVerticalSwipe();
                }
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
                    applyOffsets(accumulatedOffsetX, accumulatedOffsetY);
                    initialPositionX = (int)event.getX();
                    initialPositionY = (int)event.getY();
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                velocityTracker.recycle();
                velocityTracker = null;
                initialPositionX = INITIAL_POSITION;
                initialPositionY = INITIAL_POSITION;
                accumulatedOffsetX = INITIAL_OFFSET;
                accumulatedOffsetY = INITIAL_OFFSET;
                motionType = MotionType.UNKNOWN;
                ownerView.performReleaseTouch();
                break;
        }
        return result;
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
               Math.abs(accumulatedOffsetX) >= MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX ||
               Math.abs(accumulatedOffsetY) >= MINIMUM_OFFSET_TO_TRIGGER_MOVEMENT_IN_PX;
    }

    private void applyHorizontalMotion(int offset) {
        ownerView.setOffsetLeftRight(offset);
    }

    private void applyVerticalMotion(int offset) {
        ownerView.setOffsetTopBottom(offset);
    }
}
