package com.scanner.returm.retumscanner.scanner.contracts;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public interface ScannerPresenter {

    void setUpDisplayAndCamera(String detectObject);

    void clickPicture(Activity activity);

    boolean isCameraAndStorageAllowed(AppCompatActivity appCompatActivity);

    void handlePermissionResult(int requestCode, String permissions[], int[] grantResults);

    void resumeCamera();

    void setIntentForFaceAndFinish();

    void checkDefaultCameraOrientationForDeviceAndSetUpViews(Context context);

    void hideSystemUI(boolean hasFocus, View decorView, ActionBar actionBar, String detectObject);
}
