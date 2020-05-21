package com.onyx.gallery.action.shape;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.ShapeFactory;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.request.shape.UpdateCurrentShapeTypeRequest;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2018/9/6 18:22
 *     desc   :
 * </pre>
 */
public class ShapeChangeAction extends BaseEditAction {
    private int shapeType = ShapeFactory.SHAPE_BRUSH_SCRIBBLE;

    public ShapeChangeAction setShapeType(int shapeType) {
        this.shapeType = shapeType;
        return this;
    }

    @Override
    public void execute(RxCallback rxCallback) {
        UpdateCurrentShapeTypeRequest request = new UpdateCurrentShapeTypeRequest(shapeType);
        getNoteManager().enqueue(request, new RxCallback<UpdateCurrentShapeTypeRequest>() {
            @Override
            public void onNext(@NonNull UpdateCurrentShapeTypeRequest updateCurrentShapeTypeRequest) {
                RxCallback.onNext(rxCallback, request);
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(rxCallback, e);
            }
        });
    }
}
