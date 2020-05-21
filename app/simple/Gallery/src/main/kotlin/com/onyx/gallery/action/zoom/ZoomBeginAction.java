package com.onyx.gallery.action.zoom;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.request.RendererToScreenRequest;

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
        RendererToScreenRequest request = new RendererToScreenRequest();
        getNoteManager().enqueue(request, new RxCallback<RendererToScreenRequest>() {
            @Override
            public void onNext(RendererToScreenRequest rendererToScreenRequest) {
                RxCallback.onNext(callback, rendererToScreenRequest);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(callback, e);
            }
        });
    }
}
