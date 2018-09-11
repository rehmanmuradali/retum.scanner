package com.scanner.returm.retumscanner.scanner.contracts;

import android.content.Intent;
import android.hardware.Camera;

import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;


public interface ScannerView {

    void setScreenOrientation(int orientation);

    void setVisibilityOfCnicSquare(int visibility);

    void setCameraAndStartPreview(Camera camera, boolean isFace);

    void setVisibilityOfTextViewHoldCnic(int visibility);

    void onPictureTaken(String filePath);

    void setVisibilityOfImageViewCapture(int visibility);

    void setVisibilityOfImageViewRetake(int visibility);

    void finishActivity();

    void startDetection();

    Intent setIntentForActivity(AbstractBaseModel abstractBaseModel);

    void setResultOK(Intent intent);

    void rotateTextViewHoldCnic(int degrees);

    void createExceptionAndFinish(Exception exception);
}
