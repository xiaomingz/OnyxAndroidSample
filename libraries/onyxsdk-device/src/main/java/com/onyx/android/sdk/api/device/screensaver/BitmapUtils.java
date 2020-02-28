package com.onyx.android.sdk.api.device.screensaver;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.opengl.GLES10;
import android.os.Build;
import android.support.annotation.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by zhuzeng on 10/16/15.
 */
class BitmapUtils {
    private static final String TAG = BitmapUtils.class.getSimpleName();

    static public Bitmap loadBitmapFromFile(final String path) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        if (!FileUtils.fileExist(path)) {
            return null;
        }
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    static public boolean saveBitmap(Bitmap bitmap, final String path) {
        return saveBitmap(bitmap, path, false);
    }

    static public boolean saveBitmap(Bitmap bitmap, final String path, boolean overrideFilePermission) {
        try {
            FileOutputStream out = new FileOutputStream(path);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            out.close();
            if (overrideFilePermission) {
                File bitmapFile = new File(path);
                bitmapFile.setReadable(true,false);
                bitmapFile.setWritable(true,false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;
    }

    public static Bitmap createScaledBitmap(final Bitmap origin, int newWidth, int newHeight) {
        Bitmap scaledBitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);

        float ratioX = newWidth / (float) origin.getWidth();
        float ratioY = newHeight / (float) origin.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHeight / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);
        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(origin, middleX - origin.getWidth() / 2, middleY - origin.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));
        return scaledBitmap;
    }

    public static void clear(final Bitmap bitmap) {
        if (bitmap != null) {
            bitmap.eraseColor(Color.WHITE);
        }
    }

    public static void recycle(final Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
    }

    static public boolean isValid(final Bitmap bitmap) {
        return bitmap != null && !bitmap.isRecycled() && bitmap.getWidth() > 0 && bitmap.getHeight() > 0;
    }

    public static Bitmap rotateBmp(Bitmap bmp, int degrees) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        return Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
    }

    public static Bitmap convertToBlackWhite(Bitmap bmp) {
        int width = bmp.getWidth();
        int height = bmp.getHeight();
        int[] pixels = new int[width * height];
        bmp.getPixels(pixels, 0, width, 0, 0, width, height);
        int alpha = 0xFF << 24;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int grey = pixels[width * i + j];
                int red = ((grey & 0x00FF0000) >> 16);
                int green = ((grey & 0x0000FF00) >> 8);
                int blue = (grey & 0x000000FF);
                grey = (int) (red * 0.3 + green * 0.59 + blue * 0.11);
                grey = alpha | (grey << 16) | (grey << 8) | grey;
                pixels[width * i + j] = grey;
            }
        }
        Bitmap newBmp = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        newBmp.setPixels(pixels, 0, width, 0, 0, width, height);
        return ThumbnailUtils.extractThumbnail(newBmp, width, height);
    }

    public static boolean savePngToFile(Bitmap bitmap, String dirName, String pngFileName, boolean isNeedOverrideDirPermission) {
        if (bitmap == null)
            return false;
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(pngFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (isNeedOverrideDirPermission) {
                String folderCommand = "chmod 777 " + dir.getAbsolutePath();
                String fileCommand = "chmod 777 " + file.getAbsolutePath();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(folderCommand);
                runtime.exec(fileCommand);
            }
            FileOutputStream fileos = new FileOutputStream(pngFileName);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileos);
            fileos.flush();
            fileos.close();
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
        }
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, String dirName, String bmpFileName, boolean isNeedOverrideDirPermission) {
        if (bitmap == null) {
            return false;
        }
        // bmp size
        int nBmpWidth = bitmap.getWidth();
        int nBmpHeight = bitmap.getHeight();
        int bufferSize = nBmpHeight * (nBmpWidth * 3 + nBmpWidth % 4);
        try {
            File dir = new File(dirName);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(bmpFileName);
            if (!file.exists()) {
                file.createNewFile();
            }
            if (isNeedOverrideDirPermission) {
                String folderCommand = "chmod 777 " + dir.getAbsolutePath();
                String fileCommand = "chmod 777 " + file.getAbsolutePath();
                Runtime runtime = Runtime.getRuntime();
                runtime.exec(folderCommand);
                runtime.exec(fileCommand);
            }
            FileOutputStream fileos = new FileOutputStream(bmpFileName);
            // bmp file head
            int bfType = 0x4d42;
            long bfSize = 14 + 40 + bufferSize;
            int bfReserved1 = 0;
            int bfReserved2 = 0;
            long bfOffBits = 14 + 40;
            // save head
            writeWord(fileos, bfType);
            writeDword(fileos, bfSize);
            writeWord(fileos, bfReserved1);
            writeWord(fileos, bfReserved2);
            writeDword(fileos, bfOffBits);
            // bmp message head
            long biSize = 40L;
            int biPlanes = 1;
            int biBitCount = 24;
            long biCompression = 0L;
            long biSizeImage = 0L;
            long biXpelsPerMeter = 0L;
            long biYPelsPerMeter = 0L;
            long biClrUsed = 0L;
            long biClrImportant = 0L;
            // save message head
            writeDword(fileos, biSize);
            writeLong(fileos, (long) nBmpWidth);
            writeLong(fileos, (long) nBmpHeight);
            writeWord(fileos, biPlanes);
            writeWord(fileos, biBitCount);
            writeDword(fileos, biCompression);
            writeDword(fileos, biSizeImage);
            writeLong(fileos, biXpelsPerMeter);
            writeLong(fileos, biYPelsPerMeter);
            writeDword(fileos, biClrUsed);
            writeDword(fileos, biClrImportant);
            // scan pixels
            byte bmpData[] = new byte[bufferSize];
            int wWidth = (nBmpWidth * 3 + nBmpWidth % 4);
            for (int nCol = 0, nRealCol = nBmpHeight - 1; nCol < nBmpHeight; ++nCol, --nRealCol)
                for (int wRow = 0, wByteIdex = 0; wRow < nBmpWidth; wRow++, wByteIdex += 3) {
                    int clr = bitmap.getPixel(wRow, nCol);
                    bmpData[nRealCol * wWidth + wByteIdex] = (byte) Color.blue(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 1] = (byte) Color.green(clr);
                    bmpData[nRealCol * wWidth + wByteIdex + 2] = (byte) Color.red(clr);
                }
            fileos.write(bmpData);
            fileos.flush();
            fileos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            bitmap.recycle();
        }
    }

    public static void writeWord(FileOutputStream stream, int value) throws IOException {
        byte[] b = new byte[2];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        stream.write(b);
    }

    public static void writeDword(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    public static void writeLong(FileOutputStream stream, long value) throws IOException {
        byte[] b = new byte[4];
        b[0] = (byte) (value & 0xff);
        b[1] = (byte) (value >> 8 & 0xff);
        b[2] = (byte) (value >> 16 & 0xff);
        b[3] = (byte) (value >> 24 & 0xff);
        stream.write(b);
    }

    public static Bitmap fillColorAsBackground(Bitmap targetBitmap, int color) {
        Bitmap result = Bitmap.createBitmap(targetBitmap.getWidth(), targetBitmap.getHeight(), Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(result);
        canvas.drawColor(color);
        canvas.drawBitmap(targetBitmap, 0, 0, null);
        canvas.save();
        return result;
    }
}
