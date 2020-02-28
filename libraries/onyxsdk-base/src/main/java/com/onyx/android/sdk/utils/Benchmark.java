package com.onyx.android.sdk.utils;

/**
 * Created by zhuzeng on 10/4/15.
 */
public class Benchmark {

    private static boolean showShortDurationInfoLog = true;
    private static final long SHORT_DURATION_MILLS = 5;

    private long benchmarkStart = 0;
    private long benchmarkEnd = 0;

    private static Benchmark sInstance = new Benchmark();

    public static Benchmark globalBenchmark() {
        return sInstance;
    }

    public Benchmark() {
        restart();
    }

    public void restart() {
        benchmarkStart = System.currentTimeMillis();
    }

    public void report(final String msg) {
        long duration = duration();
        if (duration < SHORT_DURATION_MILLS && !isShowShortDurationInfoLog()) {
            return;
        }
        Debug.i(getClass(), msg + " ---> " + String.valueOf(duration) + "ms");
    }

    public void reportError(final String msg) {
        Debug.e(getClass(), msg + " ---> " + String.valueOf(duration()) + "ms");
    }

    public void reportWarn(final String msg) {
        Debug.w(getClass(), msg + " ---> " + String.valueOf(duration()) + "ms");
    }

    public long duration() {
        benchmarkEnd = System.currentTimeMillis();
        return benchmarkEnd - benchmarkStart;
    }

    public static boolean isShowShortDurationInfoLog() {
        return showShortDurationInfoLog;
    }

    public static void setShowShortDurationInfoLog(boolean showShortDurationInfoLog) {
        Benchmark.showShortDurationInfoLog = showShortDurationInfoLog;
    }
}
