package com.onyx.gallery.helpers;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.SurfaceView;

import androidx.annotation.WorkerThread;

import com.onyx.android.sdk.api.device.epd.EpdController;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.CollectionUtils;
import com.onyx.gallery.BuildConfig;
import com.onyx.gallery.utils.RendererUtils;

import org.jetbrains.annotations.Nullable;

import java.util.List;

import static com.onyx.gallery.utils.RendererUtils.createRenderContext;

/**
 * Created by lxm on 2018/2/7.
 */

public class RendererHelper {

    private RenderContext renderContext = createRenderContext().setEnableBitmapCache(true);

    public RenderContext getRenderContext() {
        return renderContext;
    }

    public void createRendererBitmap(Rect rect) {
        renderContext.createBitmap(rect);
        renderContext.updateCanvas();
    }

    public void recycleRendererBitmap() {
        renderContext.recycleBitmap();
    }

    public void resetRenderContext() {
        renderContext.reset();
        renderContext.matrix.reset();
    }

    public void eraseRendererBitmap() {
        renderContext.eraseBitmap();
    }

    public void resetRendererBitmap(Rect rect) {
        recycleRendererBitmap();
        createRendererBitmap(rect);
    }

    @WorkerThread
    public void renderToBitmap(List<Shape> shapes) {
        Benchmark benchmark = new Benchmark();
        for (Shape shape : shapes) {
            shape.render(renderContext);
        }
        if (BuildConfig.DEBUG) {
            benchmark.report(getClass().getSimpleName() + " -->> renderToBitmap ");
        }
    }

    public void beforeUnlockCanvas(SurfaceView surfaceView) {
        EpdController.enablePost(surfaceView, 1);
    }

    @WorkerThread
    public boolean render(List<Shape> shapes, @Nullable Canvas canvas) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return false;
        }
        RenderContext renderContext = RendererUtils.createRenderContext();
        if (canvas != null) {
            renderContext.canvas = canvas;
        }
        for (Shape shape : shapes) {
            shape.render(renderContext);
        }
        return true;
    }

    @WorkerThread
    public void renderToSurfaceView(SurfaceView surfaceView) {
        renderToSurfaceView(surfaceView, canvas -> {
            if (renderContext.scalingMatrix != null) {
                canvas.setMatrix(renderContext.getScalingMatrix());
            }
            Rect rect = RendererUtils.checkSurfaceView(surfaceView);
            renderBackground(surfaceView.getContext(), canvas, renderContext, rect);
            canvas.drawBitmap(renderContext.getBitmap(), 0, 0, null);
            return true;
        });
    }

    @WorkerThread
    private void renderBackground(Context context,
                                  Canvas canvas,
                                  RenderContext renderContext,
                                  Rect viewRect) {
        RendererUtils.clearBackground(canvas, new Paint(), viewRect);
        Matrix matrix = new Matrix(renderContext.getViewPortMatrix());
        if (renderContext.scalingMatrix != null) {
            matrix.postConcat(renderContext.scalingMatrix);
        }
        renderContext.drawBackGround(context, canvas, viewRect, matrix);
    }

    @WorkerThread
    public boolean renderVarietyShapesToScreen(SurfaceView surfaceView, final List<Shape> shapes) {
        if (CollectionUtils.isNullOrEmpty(shapes)) {
            return false;
        }
        return renderToSurfaceView(surfaceView, canvas -> {
            canvas.drawBitmap(renderContext.bitmap, 0, 0, null);
            render(shapes, canvas);
            return true;
        });
    }

    @WorkerThread
    private boolean renderToSurfaceView(SurfaceView surfaceView, Render render) {
        if (surfaceView == null) {
            return false;
        }
        Canvas canvas = surfaceView.getHolder().lockCanvas();
        if (canvas == null) {
            return false;
        }
        Benchmark benchmark = new Benchmark();
        try {
            return render.renderToCanvas(canvas);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            beforeUnlockCanvas(surfaceView);
            surfaceView.getHolder().unlockCanvasAndPost(canvas);
            if (BuildConfig.DEBUG) {
                benchmark.report(getClass().getSimpleName() + " -->> renderToScreen ");
            }
        }
        return false;
    }

}
