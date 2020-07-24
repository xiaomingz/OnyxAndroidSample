package com.onyx.gallery.action.zoom;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.ui.ApplyFastModeEvent;
import com.onyx.gallery.request.zoom.ZoomBeginRequest;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/11/18 11:07
 *     desc   :
 * </pre>
 */
public class ZoomBeginAction extends BaseEditAction {

    @Override
    public void execute(final RxCallback callback) {
        getEventBus().post(new ApplyFastModeEvent(true));
        ZoomBeginRequest zoomBeginRequest = new ZoomBeginRequest();
        getGlobalEditBundle().enqueue(zoomBeginRequest, new RxCallback<ZoomBeginRequest>() {
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
