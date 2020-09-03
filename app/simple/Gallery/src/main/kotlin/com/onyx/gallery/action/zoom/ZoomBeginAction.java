package com.onyx.gallery.action.zoom;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.bundle.EditBundle;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.ui.ApplyFastModeEvent;
import com.onyx.gallery.request.zoom.ZoomBeginRequest;

import org.jetbrains.annotations.NotNull;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/11/18 11:07
 *     desc   :
 * </pre>
 */
public class ZoomBeginAction extends BaseEditAction {

    public ZoomBeginAction(@NotNull EditBundle editBundle) {
        super(editBundle);
    }

    @Override
    public void execute(final RxCallback callback) {
        EditBundle editBundle = getEditBundle();
        getEventBus().post(new ApplyFastModeEvent(true));
        ZoomBeginRequest zoomBeginRequest = new ZoomBeginRequest(editBundle);
        editBundle.enqueue(zoomBeginRequest, new RxCallback<ZoomBeginRequest>() {
            @Override
            public void onNext(ZoomBeginRequest request) {
                RxCallback.onNext(callback, request);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(callback, e);
            }
        });
    }
}
