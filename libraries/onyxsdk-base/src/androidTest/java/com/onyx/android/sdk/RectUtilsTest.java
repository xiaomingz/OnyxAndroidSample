package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.test.ApplicationTestCase;

import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.BitmapUtils;
import com.onyx.android.sdk.utils.Debug;
import com.onyx.android.sdk.utils.RectUtils;

import junit.framework.Assert;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * Created by joy on 6/1/17.
 */

public class RectUtilsTest extends ApplicationTestCase<Application> {

    public RectUtilsTest() {
        super(Application.class);
    }

    private boolean contains(final List<RectF> list, float x, float y) {
        for (RectF r : list) {
            if (r.contains(x, y) || r.contains(x - 1, y - 1)) {
                return true;
            }
        }
        return false;
    }

    private boolean isOnEdge(final List<RectF> list, float x, float y) {
        for (RectF r : list) {
            if (Float.compare(r.left, x) == 0 || Float.compare(r.right, x) == 0 ||
                    Float.compare(r.top, y) == 0 || Float.compare(r.bottom, y) == 0) {
                return true;
            }
        }
        return false;
    }

    private boolean testCutResult(RectF rect, List<RectF> excluding, List<RectF> cutting) {
        if (cutting.size() > 1) {
            for (int i = 0; i < cutting.size(); i++) {
                for (int j = i + 1; j < cutting.size(); j++) {
                    assertFalse(RectF.intersects(cutting.get(i), cutting.get(j)));
                }
            }
        }

        for (int i = 0; i < (int)rect.width(); i++) {
            for (int j = 0; j < (int)rect.height(); j++) {
                if (!contains(excluding, i, j) && !contains(cutting, i, j)) {
                    Debug.e(getClass(), "test (%d, %d) failed because not hit", i, j);
                    assertTrue(false);
                }
                if (contains(excluding, i, j) && contains(cutting, i, j) &&
                        !isOnEdge(excluding, i, j) && !isOnEdge(cutting, i, j)) {
                    Debug.e(getClass(), "test (%d, %d) failed because both hit", i, j);
                    assertTrue(false);
                }
            }
        }

        return true;
    }

    private void saveBitmap(final RectF rect, final List<RectF> list, String flag) {
        Bitmap bmp = Bitmap.createBitmap((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.WHITE);

        Random rand = new Random(System.currentTimeMillis());

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        for (int j = 0; j < list.size(); j++) {
            RectF r = list.get(j);
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            paint.setARGB(128, red, green, blue);
            canvas.drawRect(r, paint);
            paint.setAlpha(255);
            paint.setColor(Color.BLACK);
            canvas.drawText(flag + j, r.left, r.top + 10, paint);
        }

        BitmapUtils.saveBitmap(bmp, new File(getContext().getFilesDir(), flag + "rect.png").getAbsolutePath());
    }

    private void saveCombinedBitmap(final RectF rect, final List<RectF> list, final List<RectF> result) {
        Bitmap bmp = Bitmap.createBitmap((int)rect.width(), (int)rect.height(), Bitmap.Config.ARGB_8888);
        bmp.eraseColor(Color.WHITE);

        Random rand = new Random(System.currentTimeMillis());

        Canvas canvas = new Canvas(bmp);
        Paint paint = new Paint();
        for (int j = 0; j < list.size(); j++) {
            RectF r = list.get(j);
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            paint.setARGB(128, red, green, blue);
            canvas.drawRect(r, paint);
            paint.setAlpha(255);
            paint.setColor(Color.BLACK);
            canvas.drawText("X" + j, r.left, r.top + 10, paint);
        }

        for (int j = 0; j < result.size(); j++) {
            RectF r = result.get(j);
            int red = rand.nextInt(255);
            int green = rand.nextInt(255);
            int blue = rand.nextInt(255);
            paint.setStrokeWidth(2);
            paint.setARGB(64, red, green, blue);
            canvas.drawRect(r, paint);
            paint.setAlpha(255);
            paint.setColor(Color.BLACK);
            canvas.drawText("Z" + j, r.left, r.top + 10, paint);
        }

        BitmapUtils.saveBitmap(bmp, new File(getContext().getFilesDir(), "rect.png").getAbsolutePath());
    }

    public void testCutRectByExcludingRegions() {
        for (int i = 0; i < 50; i++) {
            RectF rect = new RectF(0, 0, 1000, 1000);
            List<RectF> list = new ArrayList<>();

            Random rand = new Random(System.currentTimeMillis());
            int n = rand.nextInt(20) + 1;
            for (int j = 0; j < n; j++) {
                float x = rand.nextInt((int) rect.width()) - 1;
                float y = rand.nextInt((int) rect.height()) - 1;
                float width = rand.nextInt((int) (rect.width() - (x + 1))) + 1;
                float height = rand.nextInt((int) (rect.height() - (y + 1)) + 1);
                list.add(new RectF(x, y, x + width, y + height));
            }

            // add special rect to make sure overlapped inputs
            list.add(new RectF(100, 300, 500, 500));

            List<RectF> copy = new ArrayList<>();
            for (int j = 0; j < list.size(); j++) {
                RectF r = list.get(j);
                Debug.e(getClass(), "excluding: " + j + ", " + r);
                copy.add(new RectF(r));
            }

            Benchmark.globalBenchmark().restart();
            List<RectF> cutResult = RectUtils.cutRectByExcludingRegions(rect, copy);
            Benchmark.globalBenchmark().reportError("result count: " + cutResult.size());

            for (int j = 0; j < cutResult.size(); j++) {
                RectF r = cutResult.get(j);
                Debug.e(getClass(), "result: " + j + ", " + r.toString());
            }

//            saveBitmap(rect, list, "X");
//            saveBitmap(rect, result, "Z");
//            saveCombinedBitmap(rect, list, result);

            testCutResult(rect, list, cutResult);

            Benchmark.globalBenchmark().restart();
            List<RectF> doubleCutResult = RectUtils.cutRectByExcludingRegions(rect, cutResult);
            Benchmark.globalBenchmark().reportError("double cut result count: " + cutResult.size());

            for (int j = 0; j < cutResult.size(); j++) {
                RectF r = cutResult.get(j);
                Debug.e(getClass(), "double cut result: " + j + ", " + r.toString());
            }

//            saveBitmap(rect, list, "X");
//            saveBitmap(rect, cutResult,"Y");
//            saveBitmap(rect, doubleCutResult, "Z");
//            saveCombinedBitmap(rect, cutResult, doubleCutResult);

            testCutResult(rect, cutResult, doubleCutResult);
        }
    }

    private void testMergeAdjacentRectangles(RectF[] input, RectF[] expected) {
        List<RectF> inputList = Arrays.asList(input);
        List<RectF> expectedList = new ArrayList<>(Arrays.asList(expected));

        ArrayList<RectF> result = RectUtils.mergeAdjacentRectangles(inputList);
        assertEquals(expectedList.size(), result.size());

        while (!expectedList.isEmpty()) {
            RectF r = expectedList.remove(0);
            int i = 0;
            for (; i < result.size(); i++) {
                if (result.get(i).equals(r)) {
                    break;
                }
            }
            if (i >= result.size()) {
                assertTrue(false);
            }
            result.remove(i);
        }
        assertEquals(result.size(), 0);
        assertEquals(expectedList.size(), 0);
    }

    public void testMergeAdjacentRectangles() {
        testMergeAdjacentRectangles(new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(200, 200, 300, 300),
        }, new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(200, 200, 300, 300),
        });

        testMergeAdjacentRectangles(new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(101, 0, 300, 100),
        }, new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(101, 0, 300, 100),
        });

        testMergeAdjacentRectangles(new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(100, 0, 300, 100),
        }, new RectF[] {
                new RectF(0, 0, 300, 100),
        });

        testMergeAdjacentRectangles(new RectF[] {
                new RectF(0, 0, 100, 100),
                new RectF(50, 0, 80, 100),
                new RectF(99, 0, 300, 100),
        }, new RectF[] {
                new RectF(0, 0, 300, 100),
        });
    }
    
    public void testMergeBlockRectanglesByWord() {
        Debug.setDebug(true);
        List<RectF> orderedList = new ArrayList<>();
        orderedList.add(new RectF(16, 178, 18, 181));
        orderedList.add(new RectF(19, 178, 21, 180));
        orderedList.add(new RectF(21, 178, 23, 180));
        orderedList.add(new RectF(23, 178, 25, 180));
        orderedList.add(new RectF(26, 178, 28, 180));
        orderedList.add(new RectF(28, 178, 30, 180));
        orderedList.add(new RectF(30, 175, 31, 180));
        orderedList.add(new RectF(34, 178, 36, 180));
        orderedList.add(new RectF(36, 177, 39, 180));
        orderedList.add(new RectF(39, 178, 41, 180));
        orderedList.add(new RectF(41, 175, 42, 180));
        orderedList.add(new RectF(43, 177, 44, 180));
        orderedList.add(new RectF(45, 178, 47, 180));
        orderedList.add(new RectF(47, 175, 48, 180));
        orderedList.add(new RectF(49, 178, 51, 180));
        orderedList.add(new RectF(51, 178, 53, 180));
        orderedList.add(new RectF(54, 177, 55, 180));
        orderedList.add(new RectF(55, 178, 57, 181));
        orderedList.add(new RectF(57, 178, 59, 180));
        orderedList.add(new RectF(59, 175, 61, 180));
        orderedList.add(new RectF(62, 177, 63, 180));
        orderedList.add(new RectF(63, 177, 64, 178));
        orderedList.add(new RectF(64, 178, 66, 180));
        orderedList.add(new RectF(66, 178, 68, 180));
        orderedList.add(new RectF(69, 178, 70, 180));
        orderedList.add(new RectF(71, 178, 73, 180));
        orderedList.add(new RectF(73, 178, 74, 180));
        orderedList.add(new RectF(74, 177, 76, 180));
        orderedList.add(new RectF(76, 178, 78, 180));
        orderedList.add(new RectF(78, 178, 80, 180));
        orderedList.add(new RectF(80, 178, 82, 180));
        orderedList.add(new RectF(82, 175, 84, 180));
        orderedList.add(new RectF(85, 178, 87, 181));
        orderedList.add(new RectF(87, 178, 89, 180));
        orderedList.add(new RectF(89, 178, 91, 180));
        orderedList.add(new RectF(91, 175, 92, 180));
        orderedList.add(new RectF(94, 178, 95, 180));
        orderedList.add(new RectF(95, 177, 96, 180));
        orderedList.add(new RectF(97, 178, 99, 180));
        orderedList.add(new RectF(99, 178, 100, 180));
        orderedList.add(new RectF(101, 178, 102, 180));
        orderedList.add(new RectF(102, 178, 104, 180));
        orderedList.add(new RectF(105, 178, 108, 180));
        orderedList.add(new RectF(108, 178, 110, 180));
        orderedList.add(new RectF(110, 178, 112, 180));
        orderedList.add(new RectF(112, 178, 114, 180));
        orderedList.add(new RectF(114, 175, 115, 180));
        orderedList.add(new RectF(116, 177, 118, 180));
        orderedList.add(new RectF(118, 178, 120, 180));
        orderedList.add(new RectF(120, 178, 122, 180));
        orderedList.add(new RectF(122, 178, 124, 180));
        orderedList.add(new RectF(125, 178, 126, 180));
        orderedList.add(new RectF(127, 178, 130, 180));
        orderedList.add(new RectF(130, 178, 132, 180));
        orderedList.add(new RectF(132, 178, 134, 180));
        orderedList.add(new RectF(135, 177, 136, 180));
        orderedList.add(new RectF(136, 178, 138, 180));
        orderedList.add(new RectF(138, 175, 139, 180));
        orderedList.add(new RectF(141, 178, 143, 181));
        orderedList.add(new RectF(143, 178, 146, 180));
        orderedList.add(new RectF(146, 178, 148, 180));
        orderedList.add(new RectF(148, 177, 149, 180));
        orderedList.add(new RectF(149, 178, 151, 180));
        orderedList.add(new RectF(151, 178, 153, 180));
        orderedList.add(new RectF(153, 178, 156, 180));
        orderedList.add(new RectF(156, 178, 158, 180));
        orderedList.add(new RectF(158, 175, 159, 180));
        orderedList.add(new RectF(158, 180, 158, 180));
        orderedList.add(new RectF(158, 180, 158, 180));
        orderedList.add(new RectF(16, 182, 19, 186));
        orderedList.add(new RectF(19, 183, 21, 186));
        orderedList.add(new RectF(21, 182, 22, 186));
        orderedList.add(new RectF(22, 182, 23, 186));
        orderedList.add(new RectF(23, 183, 25, 186));
        orderedList.add(new RectF(25, 181, 27, 186));
        orderedList.add(new RectF(28, 183, 29, 186));
        orderedList.add(new RectF(30, 183, 32, 186));
        orderedList.add(new RectF(32, 183, 34, 186));
        orderedList.add(new RectF(34, 181, 35, 186));
        orderedList.add(new RectF(37, 183, 38, 186));
        orderedList.add(new RectF(39, 183, 41, 186));
        orderedList.add(new RectF(41, 183, 43, 186));
        orderedList.add(new RectF(43, 183, 45, 186));
        orderedList.add(new RectF(45, 183, 47, 186));
        orderedList.add(new RectF(48, 183, 49, 186));
        orderedList.add(new RectF(50, 182, 51, 186));
        orderedList.add(new RectF(51, 183, 53, 186));
        orderedList.add(new RectF(53, 183, 55, 186));
        orderedList.add(new RectF(56, 182, 57, 186));
        orderedList.add(new RectF(57, 181, 58, 186));
        orderedList.add(new RectF(59, 183, 62, 187));
        orderedList.add(new RectF(62, 182, 63, 186));
        orderedList.add(new RectF(63, 182, 65, 186));
        orderedList.add(new RectF(65, 181, 67, 186));
        orderedList.add(new RectF(68, 183, 70, 186));
        orderedList.add(new RectF(71, 183, 73, 186));
        orderedList.add(new RectF(73, 183, 74, 186));
        orderedList.add(new RectF(75, 183, 77, 187));
        orderedList.add(new RectF(77, 182, 78, 186));
        orderedList.add(new RectF(78, 183, 80, 186));
        orderedList.add(new RectF(80, 183, 83, 186));
        orderedList.add(new RectF(83, 183, 85, 186));
        orderedList.add(new RectF(85, 181, 86, 186));
        orderedList.add(new RectF(88, 183, 89, 186));
        orderedList.add(new RectF(89, 181, 91, 186));
        orderedList.add(new RectF(92, 183, 94, 187));
        orderedList.add(new RectF(94, 183, 96, 186));
        orderedList.add(new RectF(96, 183, 98, 186));
        orderedList.add(new RectF(98, 181, 99, 186));
        orderedList.add(new RectF(101, 183, 103, 186));
        orderedList.add(new RectF(103, 183, 105, 186));
        orderedList.add(new RectF(106, 183, 109, 186));
        orderedList.add(new RectF(109, 183, 113, 186));
        orderedList.add(new RectF(113, 183, 115, 186));
        orderedList.add(new RectF(115, 183, 117, 186));
        orderedList.add(new RectF(117, 183, 118, 186));
        orderedList.add(new RectF(119, 183, 121, 186));
        orderedList.add(new RectF(121, 183, 122, 186));
        orderedList.add(new RectF(122, 182, 123, 186));
        orderedList.add(new RectF(123, 183, 125, 186));
        orderedList.add(new RectF(125, 181, 127, 186));
        orderedList.add(new RectF(129, 182, 131, 186));
        orderedList.add(new RectF(131, 183, 133, 186));
        orderedList.add(new RectF(133, 182, 134, 186));
        orderedList.add(new RectF(134, 181, 135, 186));
        orderedList.add(new RectF(137, 183, 140, 186));
        orderedList.add(new RectF(140, 182, 141, 186));
        orderedList.add(new RectF(142, 183, 144, 186));
        orderedList.add(new RectF(144, 181, 145, 186));
        orderedList.add(new RectF(147, 183, 149, 187));
        orderedList.add(new RectF(149, 183, 151, 186));
        orderedList.add(new RectF(151, 183, 154, 186));
        orderedList.add(new RectF(154, 183, 155, 186));
        orderedList.add(new RectF(155, 183, 157, 186));
        orderedList.add(new RectF(157, 181, 158, 186));
        orderedList.add(new RectF(158, 186, 158, 186));
        orderedList.add(new RectF(158, 186, 158, 186));
        orderedList.add(new RectF(16, 188, 18, 191));
        orderedList.add(new RectF(18, 188, 19, 191));
        orderedList.add(new RectF(20, 186, 21, 191));
        orderedList.add(new RectF(22, 189, 24, 191));
        orderedList.add(new RectF(24, 188, 25, 191));
        orderedList.add(new RectF(26, 189, 27, 191));
        orderedList.add(new RectF(27, 188, 29, 191));
        orderedList.add(new RectF(29, 189, 31, 191));

        List<RectF> lineRecList = RectUtils.mergeBlockRectanglesByWord(orderedList);
        Debug.d("mergeBlockRectanglesByWord end " + lineRecList.toString());
        Assert.assertEquals(3, lineRecList.size());
    }

}
