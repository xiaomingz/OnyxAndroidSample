package com.onyx.gallery.action.shape;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.result.LoadImageResultEvent;
import com.onyx.gallery.request.image.CreateImageShapeRequest;

/**
 * <pre>
 *     author : lxw
 *     time   : 2019/3/14 15:51
 *     desc   :
 * </pre>
 */
public class CreateImageShapeAction extends BaseEditAction {

    private String filePath;
    private Rect scribbleRect;

    public CreateImageShapeAction setFilePath(String filePath) {
        this.filePath = filePath;
        return this;
    }

    public CreateImageShapeAction setScribbleRect(Rect scribbleRect) {
        this.scribbleRect = scribbleRect;
        return this;
    }

    @Override
    public void execute(final RxCallback callback) {
        final CreateImageShapeRequest shapeRequest = new CreateImageShapeRequest()
                .setScribbleRect(scribbleRect)
                .setImageFilePath(filePath);
        getNoteManager().enqueue(shapeRequest, new RxCallback<CreateImageShapeRequest>() {
            @Override
            public void onNext(@NonNull CreateImageShapeRequest request) {
                RxCallback.onNext(callback, request);
                getEventBus().post(new LoadImageResultEvent());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(callback, e);
            }
        });
    }
}
