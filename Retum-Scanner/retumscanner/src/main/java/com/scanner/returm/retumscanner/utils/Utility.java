package com.scanner.returm.retumscanner.utils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.support.annotation.NonNull;
import android.util.Log;


import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

public class Utility {
    /**
     * Saves the bitmap at the given path
     *
     * @param bitmap   Image bitmap
     * @param fileName file Name
     * @return file which contains Bitmap
     */
    private static File saveBitmap(Bitmap bitmap, String fileName, Context context) {
        if (bitmap != null) {
            try {
                FileOutputStream outputStream = null;
                try {
                    //String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM).getAbsolutePath();
                    String path = FileUtil.getPicturesRoot(context).getAbsolutePath();
                    outputStream = new FileOutputStream(path + fileName);
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    return new File(path + fileName);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (outputStream != null) outputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }


    private static double[] getScaledWidthAndHeight(double greatestSideSize, int width, int height) {
        double newWidth, newHeight;
        if (width <= greatestSideSize || height <= greatestSideSize) {
            newHeight = height;
            newWidth = width;
        } else {
            boolean isWidthGreater = width > height;
            double aspectRatio = isWidthGreater ? (width / height) : (height / width);
            if (isWidthGreater) {
                newWidth = greatestSideSize;
                int temp = (int) ((width - greatestSideSize) / aspectRatio);
                newHeight = height - temp;
            } else {
                newHeight = greatestSideSize;
                double temp = ((height - greatestSideSize) / aspectRatio);
                newWidth = width - temp;
            }
        }
        return new double[]{newWidth, newHeight};
    }


    /**
     * Load Bitmap to memory according memory available
     *
     * @param file             Containt image to load
     * @param greatestSideSize Adjust value according to memory available in device
     * @return Bitmap that is sufficient to load in memory
     */
    private static Bitmap loadBitmapWithRespectToMemory(File file, double greatestSideSize) {

        try {
            Options options = new Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(file.getAbsolutePath(), options);
            double[] scaledWidthAndHeight = getScaledWidthAndHeight(greatestSideSize, options.outWidth, options.outHeight);
            if (greatestSideSize > 0) {
                options.inSampleSize = calculateInSampleSize(options, (int) scaledWidthAndHeight[0], (int) scaledWidthAndHeight[1]);
            } else greatestSideSize = 1024D;
            options.inJustDecodeBounds = false;
            return BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        } catch (OutOfMemoryError e) {
            Log.e("OutOfMemory", "greatest size: " + greatestSideSize);
            if (greatestSideSize <= 500) return null;
            return loadBitmapWithRespectToMemory(file, greatestSideSize - 100);
        }
    }


    public static File rotateImageFile(File file, int degrees, Context context) {
        Matrix matrix = new Matrix();
        matrix.postRotate(degrees);
        Bitmap originalBitmap = loadBitmapWithRespectToMemory(file, -1);
        assert originalBitmap != null;
        Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
        if (/*scaleWidth != 1 || scaleHeight != 1 || rotate != 0*/ originalBitmap != croppedBitmap)
            originalBitmap.recycle();
        @SuppressLint("SimpleDateFormat") String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
        return saveBitmap(croppedBitmap, fileName, context);
    }


    public static File getResizedImageFileUsingSampleSizeTemp(File file, boolean shouldFlip, Context context) {
        try {
            int rotate = 0;
            if (shouldFlip) rotate = getFileRotation(file);
            Matrix matrix = new Matrix();
            matrix.postRotate(rotate);
            Bitmap originalBitmap = loadBitmapWithRespectToMemory(file, -1);
            assert originalBitmap != null;
            Bitmap croppedBitmap = Bitmap.createBitmap(originalBitmap, 0, 0, originalBitmap.getWidth(), originalBitmap.getHeight(), matrix, false);
            if (/*scaleWidth != 1 || scaleHeight != 1 || rotate != 0*/ originalBitmap != croppedBitmap)
                originalBitmap.recycle();
            @SuppressLint("SimpleDateFormat") String fileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + ".jpg";
            return saveBitmap(croppedBitmap, fileName, context);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private static int getFileRotation(File file) throws IOException {
        int rotate = 0;
        ExifInterface exif = new ExifInterface(file.getAbsolutePath());
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }
        return rotate;
    }


    private static int calculateInSampleSize(Options options, int reqWidth, int reqHeight) {
        int height = options.outHeight;
        int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int halfHeight = height / 2;
            int halfWidth = width / 2;
            while (halfHeight / inSampleSize > reqHeight && halfWidth / inSampleSize > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public static <T> ArrayList deleteDuplicatesFromList(List<T> blockData) {
        //noinspection unchecked
        return new ArrayList(new HashSet(blockData));
    }

    @SuppressLint("SimpleDateFormat")
    public static String getFormattedDate(long dateTime, @NonNull String format) {
        return new SimpleDateFormat(format).format(new Date(dateTime));
    }

    public static void showAlertBox(Context context, String message, int positiveStringId, DialogInterface.OnClickListener onClickListener) {
        new AlertDialog.Builder(context)
                .setMessage(message)
                .setPositiveButton(positiveStringId, onClickListener)
                .show();
    }
}
