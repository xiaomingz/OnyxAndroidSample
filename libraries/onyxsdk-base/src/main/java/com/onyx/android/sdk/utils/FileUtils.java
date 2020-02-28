package com.onyx.android.sdk.utils;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.util.Log;

import com.onyx.android.sdk.data.SortBy;
import com.onyx.android.sdk.data.SortOrder;
import com.onyx.android.sdk.device.Device;
import com.onyx.android.sdk.device.EnvironmentUtil;

import java.io.BufferedReader;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by zhuzeng on 10/16/15.
 */
public class FileUtils {
    private static final String TAG = FileUtils.class.getSimpleName();
    private static final String ONE_NOTE_FILE_NAME_KEY = "com.microsoft.office.onenote.filename";
    private static final String EXTERNAL_STORAGE_AUTHORITY = "com.android.externalstorage.documents";
    private static List<String> fileNameKeyList = Arrays.asList(ONE_NOTE_FILE_NAME_KEY);
    private static List<String> externalStorageMountPoints = Arrays.asList("/mnt/media_rw", "/storage");

    public static boolean fileExist(final String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists();
    }

    public static boolean fileExist(File file) {
        return file != null && file.exists();
    }

    public static boolean isEmpty(final String path) {
        return !fileExist(path) || FileUtils.getFileSize(path) <= 0;
    }

    public static boolean fileCanRead(final String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return false;
        }
        File file = new File(path);
        return file.exists() && file.canRead();
    }

    public static boolean isDirectory(String path) {
        File f = new File(path);
        return f.isDirectory();
    }

    public static boolean mkdirs(final String path) {
        File file = new File(path);
        if (file.exists()) {
            return file.isDirectory();
        }
        return file.mkdirs();
    }

    public static void purgeDirectory(final File dir) {
        File[] files = dir.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : dir.listFiles()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    public static String getFileExtension(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            return "";
        }
        int dotPosition = fileName.lastIndexOf('.');
        if (dotPosition >= 0) {
            return fileName.substring(dotPosition + 1).toLowerCase(Locale.getDefault());
        }

        return "";
    }

    public static String getFileExtension(File file) {
        return getFileExtension(file.getName());
    }

    public static boolean isPngExtension(File file) {
        return getFileExtension(file).toLowerCase().equals("png");
    }

    public static boolean isJpgExtension(File file) {
        String extension = getFileExtension(file).toLowerCase();
        return extension.equals("jpg") || extension.equals("jpeg");
    }

    public static void collectFiles(final String parentPath, final Set<String> extensionFilters, boolean recursive, final Collection<String> fileList) {
        collectFiles(parentPath, extensionFilters, recursive, fileList, false);
    }

    public static void collectFiles(final String parentPath, final Set<String> extensionFilters, boolean recursive, final Collection<String> fileList , boolean checkNoMedia) {
        File parent = new File(parentPath);
        File[] files = parent.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        if (checkNoMedia && containsNomedia(files)) {
            return;
        }
        for (File file : files) {
            if (file.isHidden()) {
                continue;
            }
            final String absolutePath = file.getAbsolutePath();
            if (file.isFile()) {
                final String extension = getFileExtension(absolutePath);
                if (extensionFilters == null || extensionFilters.contains(extension)) {
                    fileList.add(absolutePath);
                }
            } else if (file.isDirectory() && recursive) {
                collectFiles(absolutePath, extensionFilters, recursive, fileList, checkNoMedia);
            }
        }
    }

    public static boolean containsNomedia(final File[] files) {
        if (files == null) {
            return false;
        }
        for (File file : files) {
            if (file.getName().equalsIgnoreCase(".nomedia")) {
                return true;
            }
        }
        return false;
    }

    public static void collectDirs(final String parentPath, boolean recursive, final Collection<String> dirList) {
        File parent = new File(parentPath);
        File[] files = parent.listFiles();
        if (files == null || files.length <= 0) {
            return;
        }
        for (File file : files) {
            if (file.isHidden() || file.isFile()) {
                continue;
            }
            dirList.add(file.getAbsolutePath());
            if (recursive) {
                collectDirs(file.getAbsolutePath(), recursive, dirList);
            }
        }
    }

    /**
     * get file list of tree of path by depth first,contains the hide file or folder
     *
     * @param rootFile
     * @param flattenedFileList
     */
    public static void collectFileTree(final File rootFile, final List<File> flattenedFileList, AtomicBoolean abortHolder) {
        if (!rootFile.exists()) {
            return;
        }
        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                return -lhs.getName().compareToIgnoreCase(rhs.getName());
            }
        };

        Stack<File> stack = new Stack<>();
        stack.push(rootFile);
        while (!stack.isEmpty()) {
            if (abortHolder.get()) {
                return;
            }

            File parent = stack.pop();
            flattenedFileList.add(parent);
            if (parent.isDirectory()) {
                File[] files = parent.listFiles();
                if (files == null) {
                    continue;
                }
                Arrays.sort(files, comparator);
                for (File f : files) {
                    if (!f.getName().equals(".") && !f.getName().equals("..")) {
                        stack.push(f);
                    }
                }
            }
        }
    }

    public static String getParent(final String path) {
        File file = new File(path);
        return file.getParent();
    }

    public static String getFileName(final String path) {
        File file = new File(path);
        return file.getName();
    }

    public static String getBaseName(File file) {
        if (file == null) {
            return "";
        }
        if (file.isDirectory()) {
            return file.getName();
        }
        return getFileBaseName(file.getName());
    }

    public static String getBaseName(final String path) {
        return getBaseName(new File(path));
    }

    public static String getFileBaseName(String fileName) {
        if (StringUtils.isNullOrEmpty(fileName)) {
            return null;
        }
        int idx = fileName.lastIndexOf('.');
        if (idx < 0) {
            return fileName;
        }
        return fileName.substring(0, idx);
    }

    public static void closeQuietly(Cursor cursor) {
        try {
            if (cursor != null)
                cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void closeQuietly(Closeable closeable) {
        try {
            if (closeable != null)
                closeable.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String canonicalPath(final String ref, final String path) {
        String result = path;
        int index = ref.lastIndexOf('/');
        if (index > 0 && path.indexOf('/') < 0) {
            result = ref.substring(0, index + 1) + path;
        }
        return result;
    }

    public static long getLastChangeTime(File file) {
        return file.lastModified();
    }

    public static boolean isImageFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".bmp") || fileName.endsWith(".jpg") || fileName.endsWith(".jpeg") ||
                fileName.endsWith(".png") || fileName.endsWith(".gif");
    }

    public static boolean isPdfFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".pdf");
    }

    public static boolean isTxtFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".txt");
    }

    public static boolean isZipFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".zip") || fileName.endsWith(".cbz");
    }

    public static boolean isRarFile(String fileName) {
        fileName = fileName.toLowerCase(Locale.getDefault());
        return fileName.endsWith(".rar") || fileName.endsWith(".cbr");
    }

    public static String readContentOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            String ls = System.getProperty("line.separator");

            StringBuilder sb = new StringBuilder();
            boolean firstLine = true;
            String line;
            while ((line = breader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                } else {
                    sb.append(ls);
                }
                sb.append(line);
            }
            return sb.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(breader);
            closeQuietly(reader);
            closeQuietly(in);
        }
        return null;
    }

    public static boolean saveContentToFile(String content, File fileForSave) {
        boolean succeed = true;
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave);
            out.write(content.getBytes("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

    public static boolean appendContentToFile(String content, File fileForSave) {
        boolean succeed;
        try {
            succeed = appendContentToFile(content.getBytes("utf-8"), fileForSave);
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        }
        return succeed;
    }

    public static boolean truncateFile(String filePath, long size) {
        File file = new File(filePath);
        if (!file.exists()) {
            return false;
        }
        FileChannel channel = null;
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            channel.truncate(size);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(channel);
        }
    }

    public static boolean appendContentToFile(String filePath, byte[] appendData, int size) {
        File file = new File(filePath);
        ensureFileExists(filePath);
        FileChannel channel = null;
        try {
            ByteBuffer byteBuffer = ByteBuffer.wrap(appendData, 0, size);
            channel = new RandomAccessFile(file, "rw").getChannel();
            channel.write(byteBuffer, getFileSize(filePath));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(channel);
        }
    }

    public static boolean appendContentToFile(byte[] appendData, File fileForSave) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave, true);
            out.write(appendData);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            closeQuietly(out);
        }
    }

    public static byte[] readFileData(String filePath, long offset, int length) {
        MappedByteBuffer memoryMap;
        File file = new File(filePath);
        byte[] data = new byte[length];
        FileChannel channel = null;
        try {
            channel = new RandomAccessFile(file, "rw").getChannel();
            memoryMap = channel.map(FileChannel.MapMode.READ_WRITE, offset, length);
            for (int i = 0; i < length; i++) {
                data[i] =  memoryMap.get();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            closeQuietly(channel);
        }
        return data;
    }

    public static boolean saveContentToFile(final byte[] data, final File fileForSave) {
        boolean succeed = true;
        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileForSave);
            output.write(data);
        } catch (Exception e) {
            succeed = false;
        } finally {
            closeQuietly(output);
        }
        return succeed;
    }

    public static boolean saveBitmapToFile(Bitmap bitmap, File fileForSave, Bitmap.CompressFormat format, int quality) {
        boolean succeed = true;
        File parentFile = fileForSave.getParentFile();
        if (null != parentFile && !parentFile.exists()) {
            parentFile.mkdirs();
        }
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(fileForSave);
            bitmap.compress(format, quality, out);
        } catch (Exception e) {
            e.printStackTrace();
            succeed = false;
        } finally {
            closeQuietly(out);
        }
        return succeed;
    }

    @Nullable
    public static String getRealFilePathFromUri(Context context, Uri uri) {
        try {
            String filePath = null;
            if (uri != null) {
                if ("content".equals(uri.getScheme())) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        filePath = getRealFilePathFromUriAboveAPI23(context, uri);
                    } else {
                        filePath = getRealFilePathFromUriByContentResolver(context,
                                uri, android.provider.MediaStore.Images.ImageColumns.DATA);
                    }
                } else {
                    filePath = uri.getPath();
                }
            }
            return filePath;
        } catch (Throwable tr) {
            Debug.e(tr);
            return null;
        }
    }

    public static String getDownloadFilePathFromName(String displayName) {
        File file = new File(EnvironmentUtil.getExternalStorageDownloadDirectory().getPath(), displayName);
        return file.getPath();
    }

    public static boolean isContentSchemeFromUri(Uri uri) {
        return StringUtils.isEquals(uri.getScheme(), "content");
    }

    public static String getFileNameFromIntent(Context context, Intent intent) {
        String fileName = getFileNameFromStringExtra(intent);
        if (StringUtils.isNullOrEmpty(fileName)) {
            fileName = getDisplayNameFromUri(context, intent.getData());
        }

        if (StringUtils.isNullOrEmpty(fileName)) {
            fileName = System.currentTimeMillis() + "-tmp.txt";
        }
        return fileName;
    }

    private static String getFileNameFromStringExtra(Intent intent) {
        for (String key : fileNameKeyList) {
            if (intent.hasExtra(key)) {
                return intent.getStringExtra(key);
            }
        }
        return "";
    }

    public static String getDisplayNameFromUri(Context context, Uri uri) {
        return getRealFilePathFromUriByContentResolver(context, uri, "_display_name");
    }

    private static String getRealFilePathFromUriAboveAPI23(Context context, Uri uri) {
        String filePath;
        if (uri.getAuthority().contains("fileprovider")) {
            filePath = getRealFilePathFromFileProvider(context, uri);
        } else {
            filePath = getPathFromUri(context, uri);
            Debug.e("getPathFromUri file path = " + filePath);
        }
        return filePath;
    }

    private static String getPathFromUri(Context context, Uri uri) {
        String pathFromUriByContentResolver = getRealFilePathFromUriByContentResolver(context, uri,
                MediaStore.Images.ImageColumns.DATA);
        if (StringUtils.isNotBlank(pathFromUriByContentResolver)) {
            return pathFromUriByContentResolver;
        }
        return getDocumentPathFromExternalStorage(context, uri);
    }

    private static String getRealFilePathFromFileProvider(Context context, Uri uri) {
        String encodePath = null;
        try {
            encodePath = URLDecoder.decode(uri.getEncodedPath(), "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String[] split = encodePath.split(File.separator);
        String relativePath = encodePath.substring(("/" + split[1]).length());
        switch (split[1]) {
            case "external":
            case "bluetooth":
                return Device.currentDevice().getExternalStorageDirectory().getAbsolutePath() + relativePath;
            case "root":
                return relativePath;
            default:
                return "";
        }
    }

    public static String getDocumentPathFromExternalStorage(final Context context, final Uri uri) {
        if (uri == null) {
            return null;
        }
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            return null;
        }

        if (!DocumentsContract.isDocumentUri(context, uri)) {
            return null;
        }

        final String authority = uri.getAuthority();
        if (EXTERNAL_STORAGE_AUTHORITY.equals(authority)) {
            final String docId = DocumentsContract.getDocumentId(uri);
            final String[] divide = docId.split(":");
            if (divide.length < 2) {
                return null;
            }
            final String type = divide[0];
            final String fileName = divide[1];
            if ("primary".equals(type)) {
                return Environment.getExternalStorageDirectory().getAbsolutePath().concat("/").concat(fileName);
            } else {
                String path;
                for (String mountPoint : externalStorageMountPoints) {
                    path = mountPoint.concat("/").concat(type).concat("/").concat(fileName);
                    if (fileExist(path)) {
                        return path;
                    }
                }
                return null;
            }
        }
        return null;
    }


    public static String getRealFilePathFromUriByContentResolver(Context context, Uri uri, String projectionName) {
        String filePath = "";
        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(uri, new String[] {projectionName}, null, null, null);
            if (cursor == null) {
                return filePath;
            }
            cursor.moveToFirst();
            filePath = cursor.getString(0);
        } catch (Exception e) {
            Debug.e(e);
        } finally {
            FileUtils.closeQuietly(cursor);
        }
        return filePath;
    }

    public static String computeMD5Safely(String path) {
        String md5 = null;
        try {
            md5 = FileUtils.computeMD5(new File(path));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return md5;
    }

    public static String computeMD5(File file) throws IOException, NoSuchAlgorithmException {
        if (!file.exists()) {
            throw new FileNotFoundException();
        }

        if (!file.isFile()) {
            throw new IllegalArgumentException();
        }

        byte[] digest_buffer = getDigestBuffer(file);
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(digest_buffer);
        byte[] out = md.digest();

        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f' };

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }

        return String.valueOf(str);
    }

    public static byte[] getDigestBuffer(File file) throws IOException {
        final int digestBlockLength = 512;
        byte[] digestBuffer = null;
        RandomAccessFile rf = null;

        try {
            rf = new RandomAccessFile(file, "r");

            long fileSize = rf.length();

            // TODO: what about an empty file?
            if (fileSize <= (digestBlockLength * 3)) {
                digestBuffer = new byte[(int)fileSize];
                rf.read(digestBuffer);
            } else {
                // 3 digest blocks, head, mid, end
                digestBuffer = new byte[3 * digestBlockLength];
                rf.seek(0);
                rf.read(digestBuffer, 0, digestBlockLength);
                rf.seek((fileSize / 2) - (digestBlockLength / 2));
                rf.read(digestBuffer, digestBlockLength, digestBlockLength);
                rf.seek(fileSize - digestBlockLength);
                rf.read(digestBuffer, 2 * digestBlockLength, digestBlockLength);
            }
        }
        finally {
            if (rf != null) {
                rf.close();
            }
        }
        return digestBuffer;
    }

    public static String computeMD5(String content) {
        if (StringUtils.isNullOrEmpty(content)) {
            return null;
        }
        return computeMD5(content.getBytes(Charset.defaultCharset()));
    }

    public static String computeMD5(byte[] buffer) {
        if (buffer == null) {
            return null;
        }
        String result = null;
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(buffer, 0, buffer.length);
            result = hexToString(md.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static String computeFullMD5Checksum(File file) throws IOException, NoSuchAlgorithmException {
        InputStream fis = null;
        try {
            fis = new FileInputStream(file);
            byte[] buffer = new byte[64 * 1024];
            MessageDigest md = MessageDigest.getInstance("MD5");
            int numRead;
            do {
                numRead = fis.read(buffer);
                if (numRead > 0) {
                    md.update(buffer, 0, numRead);
                }
            } while (numRead != -1);

            return hexToString(md.digest());
        } finally {
            FileUtils.closeQuietly(fis);
        }
    }

    public static String hexToString(byte[] out) {
        final char hex_digits[] = {
                '0', '1', '2', '3',
                '4', '5', '6', '7',
                '8', '9', 'a', 'b',
                'c', 'd', 'e', 'f'};

        char str[] = new char[out.length * 2];
        for (int i = 0; i < out.length; i++) {
            int j = i << 1;
            str[j] = hex_digits[(out[i] >> 4) & 0x0F];
            str[j + 1] = hex_digits[out[i] & 0x0F];
        }
        return String.valueOf(str);
    }

    public static boolean deleteFile(final String path) {
        return deleteFile(new File(path));
    }

    public static boolean deleteFile(File file) {
        if (file.exists()) {
            file.delete();
            return true;
        }
        return false;
    }

    public static boolean deleteFile(final File file, boolean recursive) {
        if (file.isFile()) {
            return deleteFile(file);
        } else {
            File[] childFile = file.listFiles();
            if (childFile == null || childFile.length <= 0) {
                return deleteFile(file);
            }
            for (File f : childFile) {
                if (recursive) {
                    deleteFile(f, true);
                } else {
                    deleteFile(f);
                }
            }
            return deleteFile(file);
        }
    }

    public static boolean ensureFileExists(String path) {
        File file = new File(path);
        if (file.exists()) {
            return true;
        } else {
            // we will not attempt to create the first directory in the path
            // (for example, do not create /sdcard if the SD card is not mounted)
            int secondSlash = path.indexOf('/', 1);
            if (secondSlash < 1) return false;
            String directoryPath = path.substring(0, secondSlash);
            File directory = new File(directoryPath);
            if (!directory.exists()) {
                return false;
            }

            File parent_folder = file.getParentFile();
            if (!parent_folder.exists() && !parent_folder.mkdirs()) {
                Log.e(TAG, "create folder failed: " + parent_folder.getAbsolutePath());
                return false;
            }
            try {
                return file.createNewFile();
            } catch (IOException ioe) {
                Log.e(TAG, "File creation failed", ioe);
            }
            return false;
        }
    }

    public static void findFileByKey(List<File> fileList, String searchKey) {
        findFileByKey(fileList, EnvironmentUtil.getExternalStorageDirectory(), searchKey);
        findFileByKey(fileList, EnvironmentUtil.getRemovableSDCardDirectory(), searchKey);
    }

    public static void findFileByKey(List<File> fileList, File targetDir, String searchKey) {
        if (!targetDir.canRead()) {
            return;
        }
        for (File temp : targetDir.listFiles()) {
            if (temp.isHidden()) {
                continue;
            }
            if (temp.isDirectory()) {
                if (temp.getName().contains(searchKey)) {
                    fileList.add(temp);
                }
                findFileByKey(fileList, temp, searchKey);
            }
            if (temp.isFile()) {
                if (temp.getName().contains(searchKey)) {
                    fileList.add(temp);
                }
            }
        }
    }

    @Nullable
    public static String fixNotAllowFileName(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return null;
        }
        int dotIndex = fileName.lastIndexOf(".");

        if (dotIndex == -1) {
            return null;
        }

        String replaceString = fileName;
        String regularExpression = "([.*/^()?|<>\\]\\[\r\n])";

        replaceString = replaceString.replaceAll(regularExpression, " ");
        replaceString = replaceString.replace(replaceString.substring(dotIndex), fileName.substring(dotIndex));
        replaceString = replaceString.replace(":", "：");
        int index = 0;
        while (replaceString.contains("\"")) {
            if (index == 0) {
                replaceString = replaceString.replaceFirst("\"", "“");
                index = 1;
            } else {
                replaceString = replaceString.replaceFirst("\"", "”");
                index = 0;
            }
        }
        return replaceString;
    }

    public static String filterFileName(String fileName) {
        return fileName.replaceAll("([.*/^()?|<>\\]\\[])", " ");
    }

    public static String readContentOfFile(String path) {
        BufferedReader reader = null;
        InputStream is = null;
        try {
            File file = new File(path);
            reader = new BufferedReader(new InputStreamReader(is = new FileInputStream(file)));
            StringBuilder total = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                total.append(line);
            }
            return total.toString();
        } catch (Exception e) {
        } finally {
            closeQuietly(reader);
            closeQuietly(is);
        }
        return null;
    }

    public static String getFileNameFromUrl(String url) {
        if (!StringUtils.isUrl(url)) {
            return null;
        }
        int idx = url.lastIndexOf('/');
        if (idx < 0) {
            return null;
        }
        return url.substring(idx + 1, url.length());
    }

    public static String getFileSize(long size) {
        DecimalFormat df = new DecimalFormat("###.##");
        float f = ((float) size / (float) (1024 * 1024));
        if (f < 1.0) {
            float f2 = ((float) size / (float) (1024));
            return df.format(new Float(f2).doubleValue()) + "KB";
        } else {
            return df.format(new Float(f).doubleValue()) + "M";
        }
    }

    public static void transferFile(final String currentFilePath, final String newFilePath) throws Exception {
        File currentFile = new File(currentFilePath);
        File newFile = new File(newFilePath);

        FileChannel src;
        FileChannel dst;
        src = new FileInputStream(currentFile).getChannel();
        dst = new FileOutputStream(newFile).getChannel();
        dst.transferFrom(src, 0, src.size());

        src.close();
        dst.close();
    }

    public static boolean compareFileMd5(final String file1, final String file2) throws IOException, NoSuchAlgorithmException {
        String file1Md5 = FileUtils.computeFullMD5Checksum(new File(file1));
        String file2Md5 = FileUtils.computeFullMD5Checksum(new File(file2));
        return file1Md5.equals(file2Md5);
    }

    public static boolean copyFile(File sourceFile, File targetFile) {
        FileChannel source = null;
        FileChannel destination = null;
        try {
            if (!targetFile.exists()) {
                if (!targetFile.createNewFile()) {
                    return false;
                }
            }
            source = new FileInputStream(sourceFile).getChannel();
            destination = new FileOutputStream(targetFile).getChannel();
            source.transferTo(0, source.size(), destination);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            FileUtils.closeQuietly(source);
            FileUtils.closeQuietly(destination);
        }
    }

    public static void sortListByName(final List<File> fileList, final SortOrder sortOrder) {
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                if (i == 0) {
                    return ComparatorUtils.stringComparator(lhs.getName(), rhs.getName(), sortOrder);
                }
                return i;
            }
        });
    }

    public static void sortListByCreationTime(final List<File> fileList, final SortOrder sortOrder) {
        //Todo:Java 6 and belows seems could only get file's last modified time,could not get creation time.
        //reference site:http://stackoverflow.com/questions/6885269/getting-date-time-of-creation-of-a-file
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                if (i == 0) {
                    return ComparatorUtils.longComparator(lhs.lastModified(), rhs.lastModified(), sortOrder);
                }
                return i;
            }
        });
    }

    public static void sortListBySize(final List<File> fileList, final SortOrder sortOrder) {
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                if (i == 0) {
                    return ComparatorUtils.longComparator(lhs.length(), rhs.length(), sortOrder);
                }
                return i;
            }
        });
    }

    public static void sortListByFileType(final List<File> fileList, final SortOrder sortOrder) {
        Collections.sort(fileList, new Comparator<File>() {
            @Override
            public int compare(File lhs, File rhs) {
                int i = ComparatorUtils.booleanComparator(lhs.isDirectory(), rhs.isDirectory(), SortOrder.Desc);
                if (i == 0) {
                    return ComparatorUtils.stringComparator(FileUtils.getFileExtension(lhs),
                            FileUtils.getFileExtension(rhs), sortOrder);
                }
                return i;
            }
        });
    }

    /**
     * Because file.renameTo Cannot use while crossing 2 different sd.For example Internal Flash to Removable SD.
     * So use the method to judge if source and target files were on same sd.
     * This method may only use in storage,so we assume file is only on internal flash or removable sd.
     * @param a
     * @param b
     * @return is on same SDCard or not.
     */
    public static boolean onSameSDCard(File a, File b) {
        if (a.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
            if (b.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
                return true;
            }
        } else {
            if (!b.getAbsolutePath().contains(EnvironmentUtil.getExternalStorageDirectory().getAbsolutePath())) {
                return true;
            }
        }
        return false;
    }

    public static void updateMtpDb(Context context, File file) {
        MediaScannerConnection.scanFile(context,
                new String[]{file.getAbsolutePath()}, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    @Override
                    public void onScanCompleted(String path, Uri uri) {
                        Debug.i(FileUtils.class, "file " + path
                                + " was scanned successfully: " + uri);
                    }
                });
    }

    public static String readableFileSize(long size) {
        if (size <= 0) return "0";
        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    public static long getFileSize(String filePath) {
        return getFileSize(new File(filePath));
    }

    public static long getFileSize(File file) {
        if (file.isDirectory()) {
            long size = 0;
            try {
                File[] fileList = file.listFiles();
                for (File temp : fileList) {
                    if (temp.isDirectory()) {
                        size = size + getFileSize(temp);

                    } else {
                        size = size + temp.length();

                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return size;
        } else if (file.exists() && file.isFile()) {
            return file.length();
        }
        return -1;
    }

    public static void removeUnSupportFormatFiles(Collection<String> originList, Collection<String> extensionFilterList) {
        if (CollectionUtils.isNullOrEmpty(originList) || CollectionUtils.isNullOrEmpty(extensionFilterList)) {
            return;
        }
        Iterator<String> iterator = originList.iterator();
        while (iterator.hasNext()) {
            String path = iterator.next();
            if (!extensionFilterList.contains(getFileExtension(StringUtils.safelyGetStr(path)))) {
                iterator.remove();
            }
        }
    }

    public static List<File> loadStorageFileList(File targetDir, final boolean skipHiddenFile, final List<String> filterDirList) {
        File[] files = targetDir.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String filename) {
                File file = new File(dir, filename);
                if (file.isDirectory()) {
                    if (filterDirList != null && filterDirList.size() > 0){
                        for (String dirName : filterDirList) {
                            if (filename.equalsIgnoreCase(dirName)) {
                                return false;
                            }
                        }
                    }
                }
                if (skipHiddenFile && file.isHidden()) {
                    return false;
                }
                return true;
            }
        });
        if (files == null || files.length <= 0) {
            return new ArrayList<>();
        }
        return new ArrayList<>(Arrays.asList(files));
    }

    public static void sortFileList(final List<File> fileList, SortBy sortBy, final SortOrder sortOrder) {
        if (CollectionUtils.isNullOrEmpty(fileList)) {
            return;
        }
        Benchmark benchmark = new Benchmark();
        switch (sortBy) {
            case Name:
                sortListByName(fileList, sortOrder);
                break;
            case CreationTime:
                sortListByCreationTime(fileList, sortOrder);
                break;
            case FileType:
                sortListByFileType(fileList, sortOrder);
                break;
            case Size:
                sortListBySize(fileList, sortOrder);
                break;
        }
        Log.w(TAG, "Sort duration:" + benchmark.duration() + "ms");
    }

   public static boolean isStorageRoot(File targetDirectory) {
       return targetDirectory == null || isStorageRoot(targetDirectory.getAbsolutePath());
   }

    public static boolean isStorageRoot(String targetDirectory) {
        return StringUtils.isNullOrEmpty(targetDirectory) ||
                StringUtils.safelyEquals(EnvironmentUtil.getStorageRootDirectory().getAbsolutePath(), targetDirectory);
    }

    public static List<String> readStringListOfFile(File fileForRead) {
        FileInputStream in = null;
        InputStreamReader reader = null;
        BufferedReader breader = null;
        List<String> list = new ArrayList<String>();
        try {
            in = new FileInputStream(fileForRead);
            reader = new InputStreamReader(in, "utf-8");
            breader = new BufferedReader(reader);

            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = breader.readLine()) != null) {
                list.add(line);
            }
            return list;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeQuietly(breader);
            closeQuietly(reader);
            closeQuietly(in);
        }
        return null;
    }

    public static String getFileNameFromPath(String path) {
        if (StringUtils.isNullOrEmpty(path)) {
            return "";
        }
        int idx = path.lastIndexOf('/');
        if (idx < 0) {
            return "";
        }
        return path.substring(idx + 1, path.length());
    }

    public static String formatFileAbsolutePath(String path, String replacedPath) {
        if (StringUtils.isNullOrEmpty(path)) {
            return replacedPath;
        }
        return formatFileAbsolutePath(new File(path), replacedPath);
    }

    public static String formatFileAbsolutePath(File file, String replacedPath) {
        if (file == null) {
            return replacedPath;
        }
        File rootDirectory = getRootDirectory(file);
        replacedPath = replaceRootPath(file, rootDirectory, replacedPath);
        return replacedPath;
    }

    public static File getRootDirectory(File file) {
        File rootDirectory = EnvironmentUtil.getStorageRootDirectory();
        if (file.getAbsolutePath().contains(rootDirectory.getAbsolutePath())) {
            File removableSDCardDirectory = EnvironmentUtil.getRemovableSDCardDirectory();
            if (removableSDCardDirectory != null && file.getAbsolutePath().contains(removableSDCardDirectory.getAbsolutePath())) {
                rootDirectory = removableSDCardDirectory;
            }
        } else {
            rootDirectory = EnvironmentUtil.getExternalStorageDirectory();
        }
        return rootDirectory;
    }

    public static String replaceRootPath(File targetDirectory, File rootDirectory, String replacedPath) {
        return targetDirectory.getAbsolutePath().replace(rootDirectory.getAbsolutePath(), replacedPath);
    }

    public static boolean isInSpeciallyDirectory(List<String> list, String filePath) {
        if (StringUtils.isNullOrEmpty(filePath) || CollectionUtils.isNullOrEmpty(list)) {
            return false;
        }
        for (String item : list) {
            if (StringUtils.isBlank(item)) {
                continue;
            }
            if (filePath.equals(item)) {
                return true;
            }
            if (item.endsWith(File.separator)) {
                if (filePath.startsWith(item)) {
                   return true;
                }
            } else {
                if (filePath.startsWith(item + File.separator)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static String addSeparatorToDirectory(String directoryPath) {
        if (StringUtils.isNullOrEmpty(directoryPath)) {
            return directoryPath;
        }
        if (directoryPath.endsWith(File.separator)) {
            return directoryPath;
        }
        return directoryPath + File.separator;
    }
}
