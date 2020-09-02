package com.onyx.gallery.action.zoom;

import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.ui.ApplyFastModeEvent;
import com.onyx.gallery.event.ui.PenEvent;
import com.onyx.gallery.request.zoom.ZoomFinishRequest;

import static com.onyx.gallery.helpers.ConstantsKt.ZOOM_DELAY_RESUME_PEN_TIME_MS;


/**
 * <pre>
 *     author : lxw
 *     time   : 2019/8/21 15:39
 *     desc   :
 * </pre>
 */
public class ZoomFinishAction extends BaseEditAction {

    private float scale;
    private TouchPoint scalePoint;

    public ZoomFinishAction(EditBundle editBundle, float scale, TouchPoint scalePoint) {
        super(editBundle);
        this.scale = scale;
        this.scalePoint = scalePoint;
    }

    @Override
    public void execute(final RxCallback callback) {
        EditBundle editBundle = getEditBundle();
        final ZoomFinishRequest zoomFinishRequest = new ZoomFinishRequest(editBundle, scale, scalePoint);
        editBundle.enqueue(zoomFinishRequest, new RxCallback<ZoomFinishRequest>() {
            @Override
            public void onNext(ZoomFinishRequest zoomFinishRequest) {
                RxCallback.onNext(callback, zoomFinishRequest);
                getEventBus().post(PenEvent.resumeRawDrawing(ZOOM_DELAY_RESUME_PEN_TIME_MS));
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(callback, e);
            }

            @Override
            public void onFinally() {
                super.onFinally();
                getEventBus().post(new ApplyFastModeEvent(false));
            }
        });
    }
}
