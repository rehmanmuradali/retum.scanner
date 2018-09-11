package com.scanner.returm.retumscanner.scanner.view.surface.view.presenter;

import android.hardware.Camera;

import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.predictor.BasePredictor;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerCallback;
import com.scanner.returm.retumscanner.scanner.view.surface.view.contracts.CameraPreviewPresenter;
import com.scanner.returm.retumscanner.scanner.view.surface.view.contracts.CameraPreviewView;
import com.scanner.returm.retumscanner.utils.firebase.FirebaseVisionUtil;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageFaceRetrieveCallback;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageTextRetrieveCallback;

import java.util.List;


public class CameraPreviewPresenterImpl implements CameraPreviewPresenter, ImageTextRetrieveCallback, ImageFaceRetrieveCallback {
    private final CameraPreviewView cameraPreviewView;
    private byte[] tempImageByte;
    private byte[] finalImageBytes;
    private boolean areAllFieldsDetected = false;
    private final ScannerCallback scannerCallback;
    private final ImageFaceRetrieveCallback faceRetrieveCallback;
    private boolean isImageInProcess = false;
    private boolean isAutoFocusing = false;
    private boolean isFace;
    private int width;
    private int height;
    private int previewFormat;
    private Exception exception;
    private BasePredictor basePredictor;
    private AbstractBaseModel abstractBaseModel;


    public <T extends ScannerCallback & ImageFaceRetrieveCallback, M extends BasePredictor> CameraPreviewPresenterImpl(CameraPreviewView cameraPreviewView, T caller, M basePredictor, boolean isFace) {
        this.cameraPreviewView = cameraPreviewView;
        this.scannerCallback = caller;
        this.faceRetrieveCallback = caller;
        this.isFace = isFace;
        this.basePredictor = basePredictor;
    }

    @Override
    public void sendImageToFirebase(byte[] bytes, Camera camera, boolean isLive, boolean isFace) {
        this.tempImageByte = bytes;
        this.width = camera.getParameters().getPreviewSize().width;
        this.height = camera.getParameters().getPreviewSize().height;
        previewFormat = camera.getParameters().getPreviewFormat();

        if (!isFace)
            FirebaseVisionUtil.getInstance().detectTextFromByteBuffer(this, bytes, camera);
        else
            FirebaseVisionUtil.getInstance().detectFaceFromByteBuffer(this, bytes, camera);

    }

    @Override
    public void processByteArray(Camera mCamera, byte[] bytes) {
        if (!isImageInProcess) {
            if (!isAutoFocusing) {
                try {
                    mCamera.autoFocus((success, camera1) -> isAutoFocusing = false);
                    isAutoFocusing = true;
                } catch (RuntimeException e) {
                    cameraPreviewView.showToast("Could not auto-focus");

                }
            }
            setImageInProgress(true);
            try {
                sendImageToFirebase(bytes, mCamera, true, isFace);
            } catch (Exception e) {
                setImageInProgress(false);
                e.printStackTrace();
            }
        }
    }


    @Override
    public void onTextRetrieveFailure(Exception exception) {
        setImageInProgress(false);
        this.exception = exception;
    }

    @Override
    public void onTextRetrieveSuccess(List<String> list, List<FirebaseVisionText.Element> elementList) {
        AbstractBaseModel abstractBaseModel = basePredictor.process(list, elementList);
        this.abstractBaseModel = this.abstractBaseModel == null ? abstractBaseModel : this.abstractBaseModel;
        //noinspection unchecked
        if (abstractBaseModel.compareTo(this.abstractBaseModel) > 0) {
            this.finalImageBytes = tempImageByte;
            //noinspection unchecked
            this.abstractBaseModel = abstractBaseModel.mergeModel(this.abstractBaseModel);
        }

        if (this.abstractBaseModel.getScore() == this.abstractBaseModel.getMaxScore()) {
            resetSettingsForSuccessfulIdentification();
            areAllFieldsDetected = true;
        }
        setImageInProgress(false);
    }

    private void setImageInProgress(boolean imageInProgress) {
        this.isImageInProcess = imageInProgress;
    }


    private void resetSettingsForSuccessfulIdentification() {
        cameraPreviewView.resetSettings();
        scannerCallback.onDetectionSuccess(finalImageBytes,
                width,
                height,
                previewFormat,
                abstractBaseModel);
    }


    @Override
    public void onTimeOut() {
        if (this.abstractBaseModel != null && this.abstractBaseModel.getScore() >= abstractBaseModel.getMinScore()) {
            if (!areAllFieldsDetected)
                resetSettingsForSuccessfulIdentification();
        } else {
            cameraPreviewView.resetSettings();
            scannerCallback.onDetectionFailure(exception);

        }

    }

    @Override
    public void onFaceRetrieveFailure(Exception exception) {
        setImageInProgress(false);
        faceRetrieveCallback.onFaceRetrieveFailure(exception);
    }

    @Override
    public void onFaceRetrieveSuccess(List<Integer> faceListId) {
        setImageInProgress(false);
        if (faceListId.size() > 0) faceRetrieveCallback.onFaceRetrieveSuccess(faceListId);
        else faceRetrieveCallback.onFaceRetrieveFailure(new Exception("No face detected"));
    }
}
