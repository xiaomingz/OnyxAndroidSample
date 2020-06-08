package com.onyx.gallery.touch;

import android.view.ScaleGestureDetector;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.utils.NumberUtils;
import com.onyx.android.sdk.utils.ResManager;
import com.onyx.gallery.R;
import com.onyx.gallery.action.zoom.ZoomBeginAction;
import com.onyx.gallery.action.zoom.ZoomFinishAction;
import com.onyx.gallery.bundle.GlobalEditBundle;
import com.onyx.gallery.handler.DrawHandler;
import com.onyx.gallery.helpers.ConstantsKt;
import com.onyx.gallery.request.zoom.ZoomingRequest;
import com.onyx.gallery.utils.ToastUtils;


/**
 * <pre>
 *     author : lxw
 *     time   : 2019/7/13 15:45
 *     desc   :
 * </pre>
 */
public class ZoomGestureListener implements ScaleGestureDetector.OnScaleGestureListener {

    private static final float SCALE_FACTOR_INTERVAL_THRESHOLD = 0.03f;
    private static final int SCALE_INTERVAL_TIME_MS = 50;

    private float preScale = NumberUtils.FLOAT_ONE;
    private float curScale = NumberUtils.FLOAT_ONE;
    private float viewScale = NumberUtils.FLOAT_ONE;
    private float lastScaleFactor = NumberUtils.FLOAT_ONE;
    private long lastToastTime = System.currentTimeMillis();
    private long lastScaleTime = System.currentTimeMillis();
    private TouchPoint touchPoint = new TouchPoint();

    @Override
    public boolean onScale(ScaleGestureDetector detector) {
        return onScaling(detector);
    }

    private void showScaleToast(int resId) {
        if ((System.currentTimeMillis() - lastToastTime) < ToastUtils.SHORT_DELAY) {
            return;
        }
        ToastUtils.showToast(ResManager.getAppContext(), resId);
        lastToastTime = System.currentTimeMillis();
    }

    @Override
    public boolean onScaleBegin(ScaleGestureDetector detector) {
        if (!supportZoom()) {
            return false;
        }
        touchPoint.x = detector.getFocusX();
        touchPoint.y = detector.getFocusY();
        reset();
        new ZoomBeginAction().execute(new RxCallback() {
            @Override
            public void onNext(@NonNull Object o) {
            }
        });
        return true;
    }

    private void reset() {
        curScale = NumberUtils.FLOAT_ONE;
        preScale = NumberUtils.FLOAT_ONE;
        lastScaleFactor = NumberUtils.FLOAT_ONE;
        viewScale = getDrawHandler().getRenderContext().getViewPortScale();
    }

    private boolean onScaling(ScaleGestureDetector detector) {
        if (!supportZoom()) {
            return false;
        }
        if (filterScale(detector)) {
            return false;
        }
        onScalingImpl(detector);
        lastScaleFactor = detector.getScaleFactor();
        lastScaleTime = System.currentTimeMillis();
        return true;
    }

    private boolean filterScale(ScaleGestureDetector detector) {
        if (Math.abs(detector.getScaleFactor() - lastScaleFactor) <= SCALE_FACTOR_INTERVAL_THRESHOLD) {
            return true;
        }
        if ((System.currentTimeMillis() - lastScaleTime) < SCALE_INTERVAL_TIME_MS) {
            return true;
        }
        return false;
    }

    private void onScalingImpl(ScaleGestureDetector detector) {
        float scale = detector.getScaleFactor() * preScale;
        float scaleResult = viewScale * scale;
        if (scaleResult < ConstantsKt.MIN_VIEW_SCALE) {
            curScale = ConstantsKt.MIN_VIEW_SCALE / viewScale;
            showScaleToast(R.string.scale_min_tips);
        } else if (scaleResult > ConstantsKt.MAX_VIEW_SCALE) {
            curScale = ConstantsKt.MAX_VIEW_SCALE / viewScale;
            showScaleToast(R.string.scale_max_tips);
        } else {
            curScale = scale;
        }
        preScale = curScale;
        ZoomingRequest request = new ZoomingRequest(curScale, touchPoint);
        getGlobalEditBundle().enqueue(request, null);
    }

    @Override
    public void onScaleEnd(ScaleGestureDetector detector) {
        if (!supportZoom()) {
            return;
        }
        new ZoomFinishAction(curScale, new TouchPoint(touchPoint)).execute(null);
    }

    private boolean supportZoom() {
        return getGlobalEditBundle().getSupportZoom();
    }

    private DrawHandler getDrawHandler() {
        return getGlobalEditBundle().getDrawHandler();
    }

    private GlobalEditBundle getGlobalEditBundle() {
        return GlobalEditBundle.Companion.getInstance();
    }

}
