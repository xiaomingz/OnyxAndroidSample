package com.onyx.android.sdk.utils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by seeksky on 2018/4/10.
 */
public class StringUtilsTest extends TestCase {
    public void testTrimPunctuation() throws Exception {
        // english symbol
        String src = ",.,.';";
        String result = StringUtils.trimPunctuation(src);
        Assert.assertEquals(result, "");
        // chinese symbol
        result = StringUtils.trimPunctuation("‘");
        Assert.assertEquals(result, "");
        // mix symbol
        result = StringUtils.trimPunctuation("‘,");
        Assert.assertEquals(result, "");
        result = StringUtils.trimPunctuation("我");
        Assert.assertEquals(result, "我");
        result = StringUtils.trimPunctuation("‘你,");
        Assert.assertEquals(result, "你");
        src = ",.中文输入；";
        result = StringUtils.trimPunctuation(src);
        Assert.assertEquals(result,"中文输入");
        result = StringUtils.trimPunctuation(",中文输入，你好。。");
        Assert.assertEquals(result,"中文输入，你好");
        result = StringUtils.trimPunctuation(",中文输入，..’‘；；你好。。");
        Assert.assertEquals(result,"中文输入，..’‘；；你好");
        result = StringUtils.trimPunctuation("中文输入");
        Assert.assertEquals(result,"中文输入");
        result = StringUtils.trimPunctuation("。。。。。。。中文输入");
        Assert.assertEquals(result,"中文输入");
        result = StringUtils.trimPunctuation("中文输入。。。。。。。");
        Assert.assertEquals(result,"中文输入");
    }
}