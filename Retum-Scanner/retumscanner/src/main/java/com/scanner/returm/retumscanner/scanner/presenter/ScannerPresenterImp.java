package com.scanner.returm.retumscanner.scanner.presenter;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;


import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerPresenter;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerView;
import com.scanner.returm.retumscanner.scanner.view.ScannerActivity;
import com.scanner.returm.retumscanner.utils.FileUtil;
import com.scanner.returm.retumscanner.utils.Utility;

import java.io.File;
import java.util.Objects;

import static android.content.Context.WINDOW_SERVICE;

public class ScannerPresenterImp implements ScannerPresenter {


    private final ScannerView scannerView;
    private Camera camera;
    private static final int PERMISSION_CODE = 11;
    private String filePath;

    public ScannerPresenterImp(ScannerView scannerView) {
        this.scannerView = scannerView;
    }

    @Override
    public void setUpDisplayAndCamera(String detectObject) {
        if (detectObject == null) return;
        switch (detectObject) {
            case ScannerActivity.CNIC_FRONT:
                setDisplayAndCameraForCnic();
                break;
            case ScannerActivity.CNIC_BACK:
                setDisplayAndCameraForCnic();
                break;
            case ScannerActivity.FACE:
                setDisplayAndCameraForFace();
                break;
        }
    }

    @Override
    public void clickPicture(Activity activity) {
        camera.takePicture(null, null, (bytes, camera) -> {
            File file = FileUtil.prepareFileForOCR(bytes, activity);
            File rotatedFile = Utility.rotateImageFile(file, getRotationOfImageForFaceDetectionToSaveInFile(activity), activity.getApplicationContext());
            activity.runOnUiThread(() -> {
                setFilePathForFace(rotatedFile.getAbsolutePath());
                scannerView.onPictureTaken(rotatedFile.getAbsolutePath());
            });
        });
    }

    private void setFilePathForFace(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public boolean isCameraAndStorageAllowed(AppCompatActivity appCompatActivity) {
        if (ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(appCompatActivity, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
            return true;
        else
            requestCameraAndStoragePermission(appCompatActivity);
        return false;
    }

    @Override
    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    //do the things normally
                    scannerView.startDetection();
                } else {
                    //finish activity
                    scannerView.createExceptionAndFinish(new Exception("Permission Denied"));
                }
                break;
        }


    }

    @Override
    public void resumeCamera() {
        camera.startPreview();
    }

    @Override
    public void setIntentForFaceAndFinish() {
        AbstractBaseModel abstractBaseModel = getDummyAbstractBaseModel();
        abstractBaseModel.setFilePath(filePath);
        Intent intent = scannerView.setIntentForActivity(abstractBaseModel);
        scannerView.setResultOK(intent);
        scannerView.finishActivity();
    }

    private AbstractBaseModel getDummyAbstractBaseModel() {
        return new AbstractBaseModel() {
            @Override
            public AbstractBaseModel mergeModel(AbstractBaseModel b2) {
                return null;
            }

            @Override
            public int compareTo(AbstractBaseModel b2) {
                return 0;
            }

            @Override
            public int getScore() {
                return 0;
            }

            @Override
            public int getMaxScore() {
                return 0;
            }

            @Override
            public int getMinScore() {
                return 0;
            }
        };
    }

    @Override
    public void checkDefaultCameraOrientationForDeviceAndSetUpViews(Context context) {
        int rotation = getDefaultCameraOrientation(context);
        switch (rotation) {
            //default in most cases
            case Surface.ROTATION_90:
                break;
            //Present in some cases
            case Surface.ROTATION_270:
                scannerView.rotateTextViewHoldCnic(180);
                break;
        }
    }


    @Override
    public void hideSystemUI(boolean hasFocus, View decorView, android.app.ActionBar actionBar, String detectObject) {
        //Avoid full screen in face detection, due to extra stretching
        if (hasFocus && !ScannerActivity.FACE.equalsIgnoreCase(detectObject))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                decorView.setSystemUiVisibility(
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                                // Set the content to appear under the system bars so that the
                                // content doesn't resize when the system bars hide and show.
                                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                                // Hide the nav bar and status bar
                                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                                | View.SYSTEM_UI_FLAG_FULLSCREEN);
            } else {
                int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN;
                decorView.setSystemUiVisibility(uiOptions);
                // Remember that you should never show the action bar if the
                // status bar is hidden, so hide that too if necessary.
                actionBar.hide();
            }
    }

    private int getRotationOfImageForFaceDetectionToSaveInFile(Context context) {
        int rotation = getDefaultCameraOrientation(context);
        switch (rotation) {
            //default in most cases
            case Surface.ROTATION_90:
                return 270;
            //Present in some cases
            case Surface.ROTATION_270:
                return 90;
            default:
                return 270;
        }
    }

    private int getDefaultCameraOrientation(Context context) {
        try {
            Display display = ((WindowManager) Objects.requireNonNull(context.getSystemService(WINDOW_SERVICE))).getDefaultDisplay();
            return display.getRotation();
        } catch (Exception ignored) {

        }
        return -1;
    }


    private void requestCameraAndStoragePermission(AppCompatActivity appCompatActivity) {

        ActivityCompat.requestPermissions(appCompatActivity,
                new String[]{
                        Manifest.permission.CAMERA,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                },
                PERMISSION_CODE);
    }

    private void setDisplayAndCameraForFace() {
        scannerView.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        scannerView.setVisibilityOfCnicSquare(View.GONE);
        scannerView.setVisibilityOfTextViewHoldCnic(View.GONE);
        scannerView.setCameraAndStartPreview(getCameraForFace(), true);

    }

    private void setDisplayAndCameraForCnic() {
        scannerView.setScreenOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        scannerView.setVisibilityOfCnicSquare(View.VISIBLE);
        scannerView.setVisibilityOfTextViewHoldCnic(View.VISIBLE);
        scannerView.setCameraAndStartPreview(getCameraForCNIC(), false);
    }

    private Camera getCameraForFace() {
        camera = new com.scanner.returm.retumscanner.utils.camera.Camera.CameraBuilder()
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_FRONT)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
                .setDisplayOrientation(90)
                .build();
        return camera;
    }

    private Camera getCameraForCNIC() {
        camera = new com.scanner.returm.retumscanner.utils.camera.Camera.CameraBuilder()
                .setCameraFacing(Camera.CameraInfo.CAMERA_FACING_BACK)
                .setFocusMode(Camera.Parameters.FOCUS_MODE_AUTO)
                .setDisplayOrientation(0)
                .build();
        return camera;
    }
}
