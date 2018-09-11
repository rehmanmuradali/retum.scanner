package com.scanner.returm.retumscanner.scanner.view.surface.view.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;

import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.predictor.BasePredictor;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerCallback;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerWithFileCallback;
import com.scanner.returm.retumscanner.scanner.view.surface.view.contracts.CameraPreviewPresenter;
import com.scanner.returm.retumscanner.scanner.view.surface.view.contracts.CameraPreviewView;
import com.scanner.returm.retumscanner.scanner.view.surface.view.presenter.CameraPreviewPresenterImpl;
import com.scanner.returm.retumscanner.utils.FileUtil;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageFaceRetrieveCallback;
import com.scanner.returm.retumscanner.utils.timer.util.Timer;

import java.io.File;
import java.util.List;

/**
 * A basic Camera preview class
 */
@SuppressLint("ViewConstructor")
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback, CameraPreviewView, ScannerCallback, ImageFaceRetrieveCallback/*, Runnable*/ {
    private static final String TAG = CameraPreview.class.getSimpleName();
    public static final int MAX_DETECTION_TIME_MILLISECONDS = 10000;
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Activity activity;
    private ScannerWithFileCallback scannerWithFileCallback;
    private ImageFaceRetrieveCallback faceRetrieveCallback;
    private Timer timer;

    private final CameraPreviewPresenter cameraPreviewPresenter;


    public void setCamera(Camera mCamera) {
        this.mCamera = mCamera;
    }

    public CameraPreview(Context context, Camera camera, Activity activity, BasePredictor basePredictor, boolean isFace) {
        super(context);
        cameraPreviewPresenter = new CameraPreviewPresenterImpl(this, this, basePredictor, isFace);
        setUpMemberVariables(camera, activity);
        setUpCountDownTimer(isFace);
    }


    private void setUpMemberVariables(Camera camera, Activity activity) {
        mCamera = camera;
        this.activity = activity;
        this.scannerWithFileCallback = (ScannerWithFileCallback) activity;
        this.faceRetrieveCallback = (ImageFaceRetrieveCallback) activity;
        mHolder = getHolder();
        mHolder.addCallback(this);

        //noinspection deprecation
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    private void setUpCountDownTimer(boolean isFace) {
        timer = new Timer(MAX_DETECTION_TIME_MILLISECONDS, cameraPreviewPresenter);
        if (isFace) return;
        timer.start();
    }


    public void surfaceCreated(SurfaceHolder holder) {
        try {
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);

        } catch (Exception e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }


    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        Log.e(TAG, "onSurfaceChange");
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }


        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }

        //setRotationOfCamera(w, h);
        //Log.e(TAG,"Display Orientation " + displayOrientation );
        //mCamera.setDisplayOrientation(this.displayOrientation);

        // set preview size and make any resize, rotate or
        // reformatting changes here

        // start preview with new settings
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
            mCamera.setPreviewCallback(this);

        } catch (Exception e) {
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    @Override
    public void onPreviewFrame(byte[] bytes, Camera camera) {
        cameraPreviewPresenter.processByteArray(camera, bytes);
    }


    @Override
    public void resetSettings() {
        if (mCamera != null) {
            mCamera.stopPreview();
            mCamera.setPreviewCallback(null);
        }
        timer.stop();
        mHolder.removeCallback(this);
    }

    @Override
    public void showToast(String message) {
        Toast.makeText(activity, message, Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onDetectionSuccess(byte[] imageBytes, int width, int height, int previewFormat, AbstractBaseModel abstractBaseModel) {
        File file = FileUtil.getResizedFileFromByteArray(imageBytes,
                width,
                height,
                previewFormat,
                activity
        );
        abstractBaseModel.setFilePath(file.getAbsolutePath());
        scannerWithFileCallback.onDetectionSuccess(abstractBaseModel);
    }


    @Override
    public void onDetectionFailure(Exception exception) {
        scannerWithFileCallback.onDetectionFailure(exception);
    }

    @Override
    public void onFaceRetrieveFailure(Exception exception) {
        faceRetrieveCallback.onFaceRetrieveFailure(exception);
    }

    @Override
    public void onFaceRetrieveSuccess(List<Integer> list) {
        faceRetrieveCallback.onFaceRetrieveSuccess(list);
    }

   /* private File prepareFile() {
        File file = FileUtil.writeByteArrayToFile(finalImageBytes, width, height, mCamera.getParameters().getPreviewFormat(), activity);
        File resizedImageFile = Utility.getResizedImageFileUsingSampleSizeTemp(file, true);
        return resizedImageFile;
    }*/

   /* private void storeValueToFirebaseDatabase(File file, CnicDataModel cnicDataModel, int keyOnDeviceModel) {


        Log.e(TAG, "Starting to Store in Database");
        Log.e(TAG, "Starting to Store in Database");
        cnicDataModel.setCaptureMethod("Live-Camera");
        FirebaseDatabaseHelper firebaseDatabaseHelper = new FirebaseDatabaseHelper();
        if (file == null) Log.e(TAG, "Resized Image File is NUll");
        firebaseDatabaseHelper.uploadFile(file, cnicDataModel, keyOnDeviceModel);
    }
*/
}
