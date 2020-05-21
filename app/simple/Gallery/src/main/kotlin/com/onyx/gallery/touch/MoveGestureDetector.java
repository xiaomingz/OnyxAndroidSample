package com.onyx.gallery.touch;

import android.view.MotionEvent;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.android.sdk.utils.TouchUtils;
import com.onyx.gallery.R;
import com.onyx.gallery.request.zoom.TranslateViewPortRequest;

import static android.view.MotionEvent.INVALID_POINTER_ID;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/23 17:45
 *     desc   :
 * </pre>
 */
public class MoveGestureDetector extends BaseTouchDetector {

    private static final int MOVE_INTERVAL_TIME_MS = 80;
    private static final int MOVE_DISTANCE_THRESHOLD = ResManager.getInteger(R.integer.move_distance_threshold);
    private long lastMoveTime = System.currentTimeMillis();
    private float lastMoveX, lastMoveY;
    private int activePointerId = INVALID_POINTER_ID;
    private boolean isTranslating;
    private boolean multiTouched;

    public boolean onTouchEvent(MotionEvent ev) {
        if (!getNoteManager().getRenderContext().isViewScaling()) {
            return false;
        }
        if (!getEditBundle().getCanFingerTouch()) {
            return false;
        }
        if (isMultiTouch(ev)) {
            multiTouched = true;
            return false;
        }
        if (TouchUtils.isPenTouchType(ev)) {
            return false;
        }
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                onTouchDown(ev);
                break;
            case MotionEvent.ACTION_MOVE:
                onMove(ev);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                onTouchUp(ev);
                break;
            default:
                break;
        }
        return true;
    }

    private void onTouchDown(MotionEvent ev) {
        lastMoveX = ev.getX();
        lastMoveY = ev.getY();
        activePointerId = ev.getPointerId(0);
    }

    private void onTouchUp(MotionEvent ev) {
        multiTouched = false;
        if (isPointerInvalid()) {
            return;
        }
        activePointerId = INVALID_POINTER_ID;
        onMove(ev);
    }

    private boolean isPointerInvalid() {
        return activePointerId == INVALID_POINTER_ID;
    }

    private void onMove(MotionEvent ev) {
        if (isPointerInvalid()) {
            return;
        }
        final int pointerIndex = ev.findPointerIndex(activePointerId);
        if (pointerIndex < 0) {
            return;
        }
        final float x = ev.getX(pointerIndex);
        final float y = ev.getY(pointerIndex);
        float moveX = x - lastMoveX;
        float moveY = y - lastMoveY;
        if (filterMoveEvent(moveX, moveY)) {
            return;
        }
        isTranslating = true;
        TranslateViewPortRequest request = new TranslateViewPortRequest(moveX, moveY);
        getNoteManager().enqueue(request, new RxCallback<TranslateViewPortRequest>() {
            @Override
            public void onNext(@NonNull TranslateViewPortRequest translateViewRequest) {
                isTranslating = false;
            }
        });
        lastMoveTime = System.currentTimeMillis();
        lastMoveX = x;
        lastMoveY = y;
    }

    private boolean filterMoveEvent(float moveX, float moveY) {
        if (Math.abs(moveX) < MOVE_DISTANCE_THRESHOLD || Math.abs(moveY) < MOVE_DISTANCE_THRESHOLD) {
            return true;
        }
        if ((System.currentTimeMillis() - lastMoveTime) < MOVE_INTERVAL_TIME_MS) {
            return true;
        }
        if (multiTouched) {
            return true;
        }
        return isTranslating;
    }

}
