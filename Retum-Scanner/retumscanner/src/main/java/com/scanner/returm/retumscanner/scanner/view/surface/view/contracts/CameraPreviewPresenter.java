package com.scanner.returm.retumscanner.scanner.view.surface.view.contracts;

import android.hardware.Camera;

import com.scanner.returm.retumscanner.utils.timer.callback.TimeOutCallback;

public interface CameraPreviewPresenter extends TimeOutCallback {

    void sendImageToFirebase(byte[] bytes, Camera camera, boolean isLive, boolean isFace);

    void processByteArray(Camera camera, byte[] bytes);
}
