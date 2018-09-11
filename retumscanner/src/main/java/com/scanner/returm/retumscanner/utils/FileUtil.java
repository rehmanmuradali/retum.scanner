package com.scanner.returm.retumscanner.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FileUtil {
    public static File getPicturesRoot(Context context) {
        File root = new File(context.getFilesDir(), "/Pictures/");
        if (!root.exists()) {
            //noinspection ResultOfMethodCallIgnored
            root.mkdirs();
        }
        return root;
    }

    private static File createImageFile(Activity activity) throws IOException {
        if (activity == null) return null;
        @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = activity.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return File.createTempFile(imageFileName, ".jpg", storageDir);
    }

    public static File prepareFileForOCR(byte[] image, Activity activity) {
        File file = null;
        try {
            file = createImageFile(activity);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(image);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    private static File writeByteArrayToFile(byte[] bytes, int width, int height, int imageFormat, Activity activity) {
        YuvImage yuvImage = new YuvImage(bytes, imageFormat, width, height, null);
        File file = null;
        try {
            file = createImageFile(activity);
            FileOutputStream fos = new FileOutputStream(file);
            yuvImage.compressToJpeg(new Rect(0, 0, width, height), 100, fos);
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }


    public static File getResizedFileFromByteArray(byte[] bytes, int width, int height, int previewFormat, Activity activity) {
        File file = FileUtil.writeByteArrayToFile(bytes, width, height, previewFormat, activity);
        return Utility.getResizedImageFileUsingSampleSizeTemp(file, true, activity.getApplicationContext());
    }
}
