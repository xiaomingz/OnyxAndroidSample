package com.onyx.android.sdk.utils;

import junit.framework.Assert;
import junit.framework.TestCase;

/**
 * Created by Joy on 2018/4/10.
 */
public class OsUtilsTest extends TestCase {
    public void testEnv() throws Exception {
        assertNull(OsUtil.getenv("NO_SUCH_ENV"));

        final String ENV_KEY = "ONYX_ENV_TEST";
        OsUtil.setenv(ENV_KEY, "xxx", true);
        assertEquals(OsUtil.getenv(ENV_KEY), "xxx");

        OsUtil.setenv(ENV_KEY, "yyy", false);
        assertEquals(OsUtil.getenv(ENV_KEY), "xxx");

        OsUtil.setenv(ENV_KEY, "yyy", true);
        assertEquals(OsUtil.getenv(ENV_KEY), "yyy");

        OsUtil.setenv(ENV_KEY, "", true);
        assertEquals(OsUtil.getenv(ENV_KEY), "");
    }
}