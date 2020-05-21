package com.onyx.gallery.event.ui;

/**
 * Created by lxm on 2018/2/28.
 */

public class PenEvent {

    public static final int DELAY_ENABLE_RAW_DRAWING_MILLS = 150;

    private boolean resumeDrawingRender;
    private boolean resumeRawInputReader;
    private boolean waitViewUpdate;
    private int delayResumePenTimeMs = DELAY_ENABLE_RAW_DRAWING_MILLS;

    public PenEvent(boolean resumeDrawingRender) {
        this.resumeDrawingRender = resumeDrawingRender;
    }

    public PenEvent(boolean resumeDrawingRender, boolean resumeRawInputReader) {
        this.resumeDrawingRender = resumeDrawingRender;
        this.resumeRawInputReader = resumeRawInputReader;
    }

    public PenEvent(boolean resumeDrawingRender, boolean resumeRawInputReader, int delayResumePenTimeMs) {
        this.resumeDrawingRender = resumeDrawingRender;
        this.resumeRawInputReader = resumeRawInputReader;
        this.delayResumePenTimeMs = delayResumePenTimeMs;
    }

    public PenEvent(boolean resumeDrawingRender, boolean resumeRawInputReader, boolean waitViewUpdate) {
        this.resumeDrawingRender = resumeDrawingRender;
        this.resumeRawInputReader = resumeRawInputReader;
        this.waitViewUpdate = waitViewUpdate;
    }

    public boolean isResumeDrawingRender() {
        return resumeDrawingRender;
    }

    public boolean isResumeRawInputReader() {
        return resumeRawInputReader;
    }

    public boolean isWaitViewUpdate() {
        return waitViewUpdate;
    }

    public int getDelayResumePenTimeMs() {
        return delayResumePenTimeMs;
    }

    public static PenEvent pauseDrawingRender() {
        return new PenEvent(false);
    }

    public static PenEvent resumeDrawingRender() {
        return new PenEvent(true);
    }

    public static PenEvent resumeRawDrawing(int delayResumePenTimeMs) {
        return new PenEvent(true, true, delayResumePenTimeMs);
    }

    public static PenEvent noDelayResumeRawDrawing() {
        return new PenEvent(true, true, 0);
    }

    public static PenEvent resumeRawDrawing() {
        return new PenEvent(true, true);
    }

    public static PenEvent resumeRawInputReader() {
        return new PenEvent(false, true);
    }

    public static PenEvent waitAndResumeRawDrawing() {
        return new PenEvent(true, true, true);
    }

}
