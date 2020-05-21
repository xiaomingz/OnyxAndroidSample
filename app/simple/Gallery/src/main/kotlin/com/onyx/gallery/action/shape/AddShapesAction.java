package com.onyx.gallery.action.shape;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.gallery.common.BaseEditAction;
import com.onyx.gallery.event.ui.PenEvent;
import com.onyx.gallery.request.shape.AddShapesRequest;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxm on 2018/2/25.
 */

public class AddShapesAction extends BaseEditAction {

    private List<Shape> shapes;

    public AddShapesAction setShapes(List<Shape> shapes) {
        this.shapes = shapes;
        return this;
    }

    public AddShapesAction setShape(Shape shape) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            shapes = new ArrayList<>();
        }
        shapes.add(shape);
        return this;
    }

    @Override
    public void execute(final RxCallback rxCallback) {
        AddShapesRequest request = new AddShapesRequest(shapes);
        getNoteManager().getRxManager().enqueue(request, new RxCallback<AddShapesRequest>() {
            @Override
            public void onNext(@NonNull AddShapesRequest addShapesRequest) {
                RxCallback.onNext(rxCallback, addShapesRequest);
                getNoteManager().postEvent(PenEvent.resumeRawDrawing());
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                RxCallback.onError(rxCallback, e);
            }
        });
    }
}
