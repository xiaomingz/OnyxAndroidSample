package com.onyx.android.sdk;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.test.ApplicationTestCase;
import com.onyx.android.sdk.utils.Benchmark;
import com.onyx.android.sdk.utils.BitmapUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * Created by joy on 10/18/16.
 */
public class BitmapTest extends ApplicationTestCase<Application> {
    public BitmapTest() {
        super(Application.class);
    }

    private Bitmap loadBitmapFromStream(InputStream is) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inDither = false;
        options.inPreferQualityOverSpeed = true;
        options.inMutable = true; // set mutable to be true, so we can always get a copy of the bitmap with Bitmap.createBitmap()
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        return BitmapFactory.decodeStream(is, null, options);
    }

    private Bitmap loadBitmapFromLocalFile() throws FileNotFoundException {
        return loadBitmapFromStream(new FileInputStream("/extsd/Pictures/TTS.png"));
    }

    private void testEncoderDecoderPerformance(Bitmap bitmap, Bitmap.CompressFormat compressFormat) {
        Benchmark benchmark = new Benchmark();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        benchmark.restart();
        bitmap.compress(compressFormat, 100, outputStream);
        benchmark.report("compress bitmap to: " + compressFormat + ", size: " + outputStream.size());

        ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
        benchmark.restart();
        loadBitmapFromStream(inputStream);
        benchmark.report("decode bitmap from: " + compressFormat);
    }

    public void testEncoderDecoderPerformance() throws FileNotFoundException {
        Benchmark benchmark = new Benchmark();

        Bitmap bitmap = loadBitmapFromLocalFile();
        benchmark.report("loadBitmapFromLocalFile");

        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.PNG);
        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.JPEG);
        testEncoderDecoderPerformance(bitmap, Bitmap.CompressFormat.WEBP);
    }

    public void test0JYBitmap() throws FileNotFoundException {

        final int WIDTH = 1320;
        final int HEIGHT = 1760;
        Bitmap bitmap = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < HEIGHT / 4; ++y) {
            for(int x = 0; x < WIDTH / 4; ++x) {
                int px = 2 * x;
                int py = 2 * y;
                bitmap.setPixel(px, py, Color.WHITE);
                bitmap.setPixel(px + 1, py, Color.BLACK);
                bitmap.setPixel(px, py + 1, Color.BLACK);
                bitmap.setPixel(px + 1,py + 1, Color.BLACK);
            }
        }

        for(int y = 0; y < HEIGHT / 4; ++y) {
            for(int x = 0; x < WIDTH / 4; ++x) {
                int px = 2 * x + WIDTH / 2;
                int py = 2 * y;
                bitmap.setPixel(px, py, Color.BLACK);
                bitmap.setPixel(px + 1, py, Color.WHITE);
                bitmap.setPixel(px, py + 1, Color.BLACK);
                bitmap.setPixel(px + 1,py + 1, Color.BLACK);
            }
        }

        for(int y = 0; y < HEIGHT / 4; ++y) {
            for(int x = 0; x < WIDTH / 4; ++x) {
                int px = 2 * x;
                int py = 2 * y + HEIGHT / 2;
                bitmap.setPixel(px, py, Color.BLACK);
                bitmap.setPixel(px + 1, py, Color.BLACK);
                bitmap.setPixel(px, py + 1, Color.WHITE);
                bitmap.setPixel(px + 1,py + 1, Color.BLACK);
            }
        }

        for(int y = 0; y < HEIGHT / 4; ++y) {
            for(int x = 0; x < WIDTH / 4; ++x) {
                int px = 2 * x + WIDTH / 2;
                int py = 2 * y + HEIGHT / 2;
                bitmap.setPixel(px, py, Color.BLACK);
                bitmap.setPixel(px + 1, py, Color.BLACK);
                bitmap.setPixel(px, py + 1, Color.BLACK);
                bitmap.setPixel(px + 1,py + 1, Color.WHITE);
            }
        }

        BitmapUtils.saveBitmap(bitmap, "/mnt/sdcard/grid.png");
    }

    static int white(int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);
        return ((((red * 299) + (green * 587) + (blue * 114)) / 1000) & 0xff);
    }

    /*
    * read from origin bitmap and convert to cfa.
    * WR
    * GB
     */
    public static void test00JYBitmap() throws FileNotFoundException {

        final int WIDTH = 1320;
        final int HEIGHT = 1760;
        Bitmap origin = BitmapUtils.loadBitmapFromFile("/mnt/sdcard/origin.png");
        Bitmap dst = Bitmap.createBitmap(WIDTH, HEIGHT, Bitmap.Config.ARGB_8888);
        for(int y = 0; y < origin.getHeight(); ++y) {
            for(int x = 0; x < origin.getWidth(); ++x) {
                int px = 2 * x;
                int py = 2 * y;
                int color = origin.getPixel(x, y);
                int white = white(color);
                int red = Color.red(color);
                int blue = Color.blue(color);
                int green = Color.green(color);
                dst.setPixel(px, py, Color.argb(0xff, white, white, white));
                dst.setPixel(px + 1, py, Color.argb(0xff, red, red, red));
                dst.setPixel(px, py + 1, Color.argb(0xff, green, green, green));
                dst.setPixel(px + 1, py + 1, Color.argb(0xff, blue, blue, blue));
            }
        }
        BitmapUtils.saveBitmap(dst, "/mnt/sdcard/cfa.png");
    }


}
