package com.onyx.gallery.helpers;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.SurfaceView;

import androidx.annotation.NonNull;

import com.onyx.android.sdk.pen.RawInputCallback;
import com.onyx.android.sdk.pen.TouchHelper;
import com.onyx.android.sdk.pen.data.TouchPoint;
import com.onyx.android.sdk.pen.data.TouchPointList;
import com.onyx.android.sdk.rx.RxCallback;
import com.onyx.android.sdk.rx.RxManager;
import com.onyx.android.sdk.rx.RxRequest;
import com.onyx.android.sdk.scribble.command.CommandManager;
import com.onyx.android.sdk.scribble.shape.ImageShape;
import com.onyx.android.sdk.scribble.shape.RenderContext;
import com.onyx.android.sdk.scribble.shape.Shape;
import com.onyx.gallery.event.raw.BeginRawDrawEvent;
import com.onyx.gallery.event.raw.BeginRawErasingEvent;
import com.onyx.gallery.event.raw.EndRawDrawingEvent;
import com.onyx.gallery.event.raw.EndRawErasingEvent;
import com.onyx.gallery.event.raw.PenUpRefreshEvent;
import com.onyx.gallery.event.raw.RawDrawingPointsMoveReceivedEvent;
import com.onyx.gallery.event.raw.RawDrawingPointsReceivedEvent;
import com.onyx.gallery.event.raw.RawErasingPointMoveEvent;
import com.onyx.gallery.event.raw.RawErasingPointsReceived;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


/**
 * Created by lxm on 2018/2/6.
 */

public class NoteManager {

    private Context appContext;
    private EventBus eventBus;
    private RxManager rxManager;
    private SurfaceView noteView;
    private RawInputCallback rawInputCallback;

    private RendererHelper rendererHelper;
    private TouchHelper touchHelper;

    private List<Shape> shapeCacheList = new ArrayList<>();

    private CommandManager commandManager;

    private float strokeWidth = 20f;
    private int strokeColor = Color.BLACK;

    private Rect limitRect = new Rect();

    public NoteManager(Context context, EventBus eventBus) {
        this.eventBus = eventBus;
        appContext = context.getApplicationContext();
    }

    public Context getAppContext() {
        return appContext;
    }

    public EventBus getEventBus() {
        return eventBus;
    }

    public void postEvent(Object event) {
        getEventBus().post(event);
    }

    public void attachHostView(@NonNull SurfaceView view) {
        if (view.getWidth() == 0 && view.getHeight() == 0) {
            throw new IllegalStateException("can not start when view width and height is 0");
        }
        if (noteView != null && noteView == view) {
            return;
        }
        noteView = view;
        if (touchHelper == null) {
            touchHelper = TouchHelper.create(view, getRawInputCallback());
        } else {
            touchHelper.bindHostView(view, getRawInputCallback());
        }
        touchHelper.openRawDrawing();
        touchHelper.setRawDrawingEnabled(false);
        setStrokeStyle(TouchHelper.STROKE_STYLE_PENCIL);
        setStrokeWidth(10);
        getRendererHelper().createRendererBitmap(new Rect(0, 0, view.getWidth(), view.getHeight()));
    }

    public RxManager getRxManager() {
        if (rxManager == null) {
            rxManager = RxManager.Builder.sharedSingleThreadManager();
        }
        return rxManager;
    }

    public <T extends RxRequest> void enqueue(final T request, final RxCallback<T> callback) {
        getRxManager().enqueue(request, callback);
    }

    public void updateLimitRect(Rect rect) {
        if (getTouchHelper() == null) {
            return;
        }
        touchHelper.closeRawDrawing();
        limitRect.set(rect);
        getTouchHelper().setLimitRect(Collections.singletonList(limitRect));
        touchHelper.openRawDrawing();
    }

    public RendererHelper getRendererHelper() {
        if (rendererHelper == null) {
            rendererHelper = new RendererHelper();
        }
        return rendererHelper;
    }

    public CommandManager getCommandManager() {
        if (commandManager == null) {
            commandManager = new CommandManager();
        }
        return commandManager;
    }

    public TouchHelper getTouchHelper() {
        return touchHelper;
    }

    private RawInputCallback getRawInputCallback() {
        rawInputCallback = new RawInputCallback() {
            @Override
            public void onBeginRawDrawing(boolean shortcutDrawing, TouchPoint point) {
                getEventBus().post(new BeginRawDrawEvent(shortcutDrawing, point));
            }

            @Override
            public void onEndRawDrawing(boolean outLimitRegion, TouchPoint point) {
                getEventBus().post(new EndRawDrawingEvent(outLimitRegion, point));
            }

            @Override
            public void onRawDrawingTouchPointMoveReceived(TouchPoint point) {
                getEventBus().post(new RawDrawingPointsMoveReceivedEvent(point));
            }

            @Override
            public void onRawDrawingTouchPointListReceived(TouchPointList pointList) {
                getEventBus().post(new RawDrawingPointsReceivedEvent(pointList));
            }

            @Override
            public void onBeginRawErasing(boolean shortcutErasing, TouchPoint point) {
                getEventBus().post(new BeginRawErasingEvent(shortcutErasing, point));
            }

            @Override
            public void onEndRawErasing(boolean outLimitRegion, TouchPoint point) {
                getEventBus().post(new EndRawErasingEvent(outLimitRegion, point));
            }

            @Override
            public void onRawErasingTouchPointMoveReceived(TouchPoint point) {
                getEventBus().post(new RawErasingPointMoveEvent(point));
            }

            @Override
            public void onRawErasingTouchPointListReceived(TouchPointList pointList) {
                getEventBus().post(new RawErasingPointsReceived(pointList));
            }

            @Override
            public void onPenUpRefresh(RectF refreshRect) {
                getEventBus().post(new PenUpRefreshEvent(refreshRect));
            }
        };
        return rawInputCallback;
    }

    public SurfaceView getNoteView() {
        return noteView;
    }

    public void quit() {
        noteView = null;
        rawInputCallback = null;
        shapeCacheList.clear();
        resetCommand();
        if (getTouchHelper() != null) {
            getTouchHelper().closeRawDrawing();
            touchHelper = null;
        }
        getRendererHelper().resetRenderContext();
    }

    public void cacheShape(Shape shape) {
        shapeCacheList.add(shape);
    }

    public void cacheShape(List<Shape> shapeList) {
        shapeCacheList.addAll(shapeList);
    }

    public void setSingleRegionMode() {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setSingleRegionMode();
    }

    public void setDrawLimitRect(List<Rect> limitRectList) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setLimitRect(limitRectList);
    }

    public void setDrawExcludeRect(List<Rect> excludeRectList) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setExcludeRect(excludeRectList);
    }

    public void setRawDrawingEnabled(boolean enable) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setRawDrawingEnabled(enable);
    }

    public void setRawDrawingRenderEnabled(final boolean enable) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setRawDrawingRenderEnabled(enable);
    }

    public void setRawInputReaderEnable(boolean enable) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setRawInputReaderEnable(enable);
    }

    public boolean isRawDrawingInputEnabled() {
        return getTouchHelper() != null && getTouchHelper().isRawDrawingInputEnabled();
    }

    public boolean isRawDrawingRenderEnabled() {
        return getTouchHelper() != null && getTouchHelper().isRawDrawingRenderEnabled();
    }

    public void setStrokeStyle(int style) {
        if (getTouchHelper() == null) {
            return;
        }
        getTouchHelper().setStrokeStyle(style);
    }

    public void setStrokeColor(int color) {
        if (getTouchHelper() == null) {
            return;
        }
        strokeColor = color;
        getTouchHelper().setStrokeColor(color);
    }

    public int getStrokeColor() {
        return strokeColor;
    }

    public void renderToBitmap(Shape shape) {
        List<Shape> shapes = new ArrayList<>();
        shapes.add(shape);
        renderToBitmap(shapes);
    }

    public void renderVarietyShapesToScreen(List<Shape> shapes) {
        try {
            rendererHelper.renderVarietyShapesToScreen(noteView, shapes);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void renderToBitmap(List<Shape> shapes) {
        getRendererHelper().renderToBitmap(shapes);
    }

    public void setStrokeWidth(float penWidth) {
        strokeWidth = penWidth;
        getTouchHelper().setStrokeWidth(penWidth);
    }

    public float getStrokeWidth() {
        return strokeWidth;
    }

    public RenderContext getRenderContext() {
        return getRendererHelper().getRenderContext();
    }

    public void resetCommand() {
        getCommandManager().reset();
    }

    public void renderToScreen() {
        rendererHelper.renderToSurfaceView(noteView);
    }

    public void renderShapesToBitmap() {
        rendererHelper.renderToBitmap(shapeCacheList);
    }

    public List<Shape> getShapeCacheList() {
        return shapeCacheList;
    }

    public List<Shape> getHandwritingShape() {
        List<Shape> shapeList = new ArrayList<>();
        for (Shape shape : shapeCacheList) {
            if (shape instanceof ImageShape) {
                continue;
            }
            shapeList.add(shape);
        }
        return shapeList;
    }

    public Rect getLimitRect() {
        return limitRect;
    }
}
