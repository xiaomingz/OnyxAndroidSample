package com.onyx.android.sdk.utils;

import org.junit.Assert;
import org.junit.Test;

/**
 * <pre>
 *     author : liao lin tao
 *     time   : 2019/5/17 10:45
 *     desc   :
 * </pre>
 */
public class DebugTest {

    @Test
    public void obfuscateLog() {
        Debug.setObfuscateLogEnabled(true);
        Benchmark benchmark = new Benchmark();
        String obfuscateLog = "";
        for (int i = 0; i < 10000; i++) {
            obfuscateLog = Debug.obfuscateLog("Benchmark: GenerateCoverBitmapRequest: ---> 56ms");
        }
        benchmark.reportWarn("obfuscateLog time");
        Assert.assertEquals("B: GCBR: ---> 56", obfuscateLog);
    }
}