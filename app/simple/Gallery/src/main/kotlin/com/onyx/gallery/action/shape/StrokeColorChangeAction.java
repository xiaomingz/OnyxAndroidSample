package com.onyx.gallery.action.shape;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.request.shape.StrokeColorChangeRequest;

/**
 * <pre>
 *     author : lxw
 *     time   : 2018/4/19 16:54
 *     desc   :
 * </pre>
 */
public class StrokeColorChangeAction extends BaseEditAction {

    private int color;

    public StrokeColorChangeAction(int color) {
        this.color = color;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        final StrokeColorChangeRequest request = new StrokeColorChangeRequest()
                .setColor(color);
        getGlobalEditBundle().enqueue(request, new RxCallback<StrokeColorChangeRequest>() {
            @Override
            public void onNext(@NonNull StrokeColorChangeRequest strokeColorChangeRequest) {
                RxCallback.onNext(rxCallback, request);
            }
        });
    }
}
