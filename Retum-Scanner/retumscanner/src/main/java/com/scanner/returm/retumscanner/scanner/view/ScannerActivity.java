package com.scanner.returm.retumscanner.scanner.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.scanner.returm.retumscanner.R;
import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.predictor.BasePredictor;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerPresenter;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerView;
import com.scanner.returm.retumscanner.scanner.contracts.ScannerWithFileCallback;
import com.scanner.returm.retumscanner.scanner.presenter.ScannerPresenterImp;
import com.scanner.returm.retumscanner.scanner.view.surface.view.view.CameraPreview;
import com.scanner.returm.retumscanner.utils.camera.Camera;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageFaceRetrieveCallback;

import java.util.List;

/**
 *
 */
public class ScannerActivity extends AppCompatActivity implements ScannerWithFileCallback, ImageFaceRetrieveCallback, ScannerView, View.OnClickListener {

    public static final String DETECT_OBJECT = "DETECT_OBJECT";
    public static final String CNIC_FRONT = "CNIC_FRONT";
    public static final String CNIC_BACK = "CNIC_BACK";
    public static final String FACE = "FACE";
    public static final String EXCEPTION = "EXCEPTION";
    public static final String BASE_PREDICTOR = "BASE_PREDICTOR";
    public static final String RESULT_BASE_MODEL = "RESULT_BASE_MODEL";

    FrameLayout frameLayoutCameraPreview;
    ImageView imageViewCNICSquare;
    TextView textViewHoldYourCNIC;
    ImageView imageViewCapture;
    ImageView imageViewRetake;
    ImageView imageViewCaptureDone;
    private CameraPreview cameraPreview;
    private ScannerPresenter scannerPresenter;
    private String detectObject;
    private BasePredictor basePredictor;


    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
        setOnClickListener();
    }

    private void setOnClickListener() {
        frameLayoutCameraPreview = findViewById(R.id.frameLayoutCameraPreview);
        textViewHoldYourCNIC = findViewById(R.id.textViewHoldYourCNIC);
        imageViewCapture = findViewById(R.id.imageViewCapture);
        imageViewRetake = findViewById(R.id.imageViewRetake);
        imageViewCaptureDone = findViewById(R.id.imageViewCaptureDone);
        imageViewCNICSquare = findViewById(R.id.imageViewCNICSquareCamera);
        imageViewCapture.setOnClickListener(this);
        imageViewRetake.setOnClickListener(this);
        imageViewCaptureDone.setOnClickListener(this);
    }

    private void setUpIntentData(Intent intentData) {
        this.detectObject = intentData != null ? getIntent().getStringExtra(ScannerActivity.DETECT_OBJECT) : null;
        this.basePredictor = intentData != null ? (BasePredictor) getIntent().getSerializableExtra(ScannerActivity.BASE_PREDICTOR) : null;
    }


    @Override
    protected void onStart() {
        super.onStart();
        grantCameraAndStoragePermission();
    }


    @Override
    protected void onResume() {
        super.onResume();
        setupViewsForCameraScannerScreen();
    }

    private void grantCameraAndStoragePermission() {
        if (scannerPresenter.isCameraAndStorageAllowed(this))
            startDetection();
    }


    private void setUpViews() {
        setContentView(R.layout.activity_real_time_camera);
        setUpIntentData(getIntent());
        scannerPresenter = new ScannerPresenterImp(this);
        scannerPresenter.checkDefaultCameraOrientationForDeviceAndSetUpViews(this);

    }


    @Override
    public void setCameraAndStartPreview(android.hardware.Camera mCamera, boolean isFace) {

        cameraPreview = new CameraPreview(this, mCamera, this, this.basePredictor, isFace);
        frameLayoutCameraPreview.addView(cameraPreview);
    }

    @Override
    public void setVisibilityOfTextViewHoldCnic(int visibility) {
        textViewHoldYourCNIC.setVisibility(visibility);
    }

    @Override
    public void onPictureTaken(String filePath) {
        setVisibilityOfImageViewRetake(View.VISIBLE);
        setVisibilityOfImageViewCaptureDone(View.VISIBLE);
        setVisibilityOfImageViewCapture(View.GONE);
        setEnableModeForImageViewCapture(true);
        imageViewCapture.setEnabled(true);
    }

    private void setEnableModeForImageViewCapture(boolean b) {
        imageViewCapture.setEnabled(b);
    }

    private void setVisibilityOfImageViewCaptureDone(int visible) {
        imageViewCaptureDone.setVisibility(visible);
    }


    @Override
    public void setVisibilityOfImageViewCapture(int visibility) {
        imageViewCapture.setVisibility(visibility);
    }

    @Override
    public void setVisibilityOfImageViewRetake(int visibility) {
        imageViewRetake.setVisibility(visibility);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);
        Camera.autoFocus();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLivePreview();
    }

    private void stopLivePreview() {
        if (cameraPreview != null) {
            cameraPreview.resetSettings();
            cameraPreview.setCamera(null);
        }
        Camera.closeCamera();
    }

    @Override
    public Intent setIntentForActivity(AbstractBaseModel abstractBaseModel) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_BASE_MODEL, abstractBaseModel);
        return intent;
    }

    @Override
    public void setResultOK(Intent intent) {
        setResult(RESULT_OK, intent);
    }

    @Override
    public void rotateTextViewHoldCnic(int degrees) {
        textViewHoldYourCNIC.setRotation(degrees);
    }


    @Override
    public void finishActivity() {
        finish();
    }

    @Override
    public void startDetection() {
        scannerPresenter.setUpDisplayAndCamera(detectObject);
    }


    @Override
    public void onDetectionSuccess(AbstractBaseModel abstractBaseModel) {
        Intent intent = setIntentForActivity(abstractBaseModel);
        setResult(RESULT_OK, intent);
        finishActivity();
    }

    @Override
    public void onDetectionFailure(Exception exception) {
        createExceptionAndFinish(exception);
    }

    @Override
    public void createExceptionAndFinish(Exception exception) {
        Intent intent = new Intent();
        intent.putExtra(EXCEPTION, exception);
        setResult(RESULT_CANCELED, intent);
        finishActivity();
    }


    @Override
    public void setScreenOrientation(int orientation) {
        setRequestedOrientation(orientation);
    }

    @Override
    public void setVisibilityOfCnicSquare(int visibility) {
        imageViewCNICSquare.setVisibility(visibility);
    }


    @Override
    public void onBackPressed() {
        cameraPreview.resetSettings();
        setResult(RESULT_CANCELED);
        finishActivity();
    }

    @Override
    public void onFaceRetrieveFailure(Exception exception) {
        setVisibilityOfImageViewCapture(View.GONE);
    }

    @Override
    public void onFaceRetrieveSuccess(List<Integer> list) {
        setVisibilityOfImageViewCapture(View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imageViewRetake)
            setUpViewsForRetakeAndStartDetection();
        else if (id == R.id.imageViewCapture)
            clickPicture();
        else if (id == R.id.imageViewCaptureDone)
            scannerPresenter.setIntentForFaceAndFinish();


    }

    private void setUpViewsForRetakeAndStartDetection() {
        scannerPresenter.resumeCamera();
        setupViewsForCameraScannerScreen();
    }

    private void setupViewsForCameraScannerScreen() {
        setVisibilityOfImageViewCapture(View.GONE);
        setVisibilityOfImageViewRetake(View.GONE);
        setVisibilityOfImageViewCaptureDone(View.GONE);
    }

    private void clickPicture() {
        setEnableModeForImageViewCapture(false);
        scannerPresenter.clickPicture(this);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        scannerPresenter.handlePermissionResult(requestCode, permissions, grantResults);
    }


    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        scannerPresenter.hideSystemUI(hasFocus, getWindow().getDecorView(), getActionBar(), detectObject);
    }
}
