package com.scanner.returm.retumscanner.utils.firebase;

import android.graphics.Point;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.Surface;

import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.firebase.ml.vision.face.FirebaseVisionFace;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions.Builder;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceLandmark;
import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.google.firebase.ml.vision.text.FirebaseVisionText.Element;
import com.google.firebase.ml.vision.text.FirebaseVisionText.Line;
import com.google.firebase.ml.vision.text.FirebaseVisionText.TextBlock;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageFaceRetrieveCallback;
import com.scanner.returm.retumscanner.utils.firebase.callback.ImageTextRetrieveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class FirebaseVisionUtil {
    private static final String TAG = FirebaseVisionUtil.class.getSimpleName();
    private static FirebaseVisionUtil firebaseVisionUtil;
    private FirebaseVisionImage image;
    private ImageFaceRetrieveCallback imageFaceRetrieveCallback;
    private ImageTextRetrieveCallback imageTextRetrieveCallback;


    private FirebaseVisionUtil() {
    }


    public static FirebaseVisionUtil getInstance() {
        if (firebaseVisionUtil == null) {
            firebaseVisionUtil = new FirebaseVisionUtil();
        }
        return firebaseVisionUtil;
    }

    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }


    private void extractTextFromImage(FirebaseVisionText firebaseVisionText) {

        ArrayList<String> blockList = new ArrayList<>();
        List<Element> elements = new ArrayList<>();
        String stringBuilder = "Operation Finishes, Block size:  " +
                firebaseVisionText.getTextBlocks().size();
        Log.e(TAG, stringBuilder);
        int blockNumber = 1;
        for (TextBlock block : firebaseVisionText.getTextBlocks()) {
            Point[] cornerPoints = block.getCornerPoints();
            String str2 = TAG;
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Block Number# ");
            stringBuilder2.append(blockNumber);
            stringBuilder2.append("Block text :  ");
            stringBuilder2.append(block.getText());
            Log.e(str2, stringBuilder2.toString());
            str2 = TAG;
            stringBuilder2 = new StringBuilder();
            stringBuilder2.append("Block Position :  ");
            stringBuilder2.append(Arrays.toString(cornerPoints));
            Log.e(str2, stringBuilder2.toString());
            for (Line line : block.getLines()) {
                for (Element element : line.getElements()) {
                    blockList.add(element.getText());
                    elements.add(element);
                }
            }
            blockNumber++;
        }
        this.imageTextRetrieveCallback.onTextRetrieveSuccess(blockList, elements);
    }


    private void extractFacesFromImage(List<FirebaseVisionFace> faces) {
        Log.e("FaceSize", String.valueOf(faces.size()));
        ArrayList<Integer> faceIdList = new ArrayList<>();
        for (FirebaseVisionFace face : faces) {
            FirebaseVisionFaceLandmark leftEar = face.getLandmark(3);
            if (leftEar != null) {
                leftEar.getPosition();
            }
            if (face.getSmilingProbability() != -1.0f) {
                face.getSmilingProbability();
            }
            if (face.getRightEyeOpenProbability() != -1.0f) {
                face.getRightEyeOpenProbability();
            }
            if (-1 != face.getTrackingId()) {
                faceIdList.add(face.getTrackingId());
            }
        }
        this.imageFaceRetrieveCallback.onFaceRetrieveSuccess(faceIdList);
    }


    public void detectTextFromByteBuffer(ImageTextRetrieveCallback imageTextRetrieveCallback,
                                         byte[] bytes,
                                         android.hardware.Camera camera) {
        this.imageTextRetrieveCallback = imageTextRetrieveCallback;

        FirebaseVisionImageMetadata.Builder builder = new FirebaseVisionImageMetadata.Builder()
                .setWidth(camera.getParameters().getPreviewSize().width)
                .setHeight(camera.getParameters().getPreviewSize().height)
                .setFormat(camera.getParameters().getPreviewFormat());

        FirebaseVisionImageMetadata metadata = builder.build();
        this.image = FirebaseVisionImage.fromByteArray(bytes, metadata);
        FirebaseVision.getInstance().getOnDeviceTextRecognizer()
                .processImage(this.image)
                .addOnSuccessListener(this::extractTextFromImage)
                .addOnFailureListener(e -> {
                    String stringBuilder = "FirebaseVisionText Failed to detect:  " +
                            e.getMessage();
                    Log.e(TAG, stringBuilder);
                    imageTextRetrieveCallback.onTextRetrieveFailure(e);
                });
    }


    public void detectFaceFromByteBuffer(ImageFaceRetrieveCallback imageFaceRetrieveCallback,
                                         byte[] bytes,
                                         android.hardware.Camera camera) {
        Log.e("FirebaseVisionUtil", "Passing Image to Firebase");
        this.imageFaceRetrieveCallback = imageFaceRetrieveCallback;

        FirebaseVisionImageMetadata.Builder builder = new FirebaseVisionImageMetadata.Builder()
                .setWidth(camera.getParameters().getPreviewSize().width)
                .setHeight(camera.getParameters().getPreviewSize().height)
                .setFormat(camera.getParameters().getPreviewFormat())
                .setRotation(FirebaseVisionImageMetadata.ROTATION_270);
        FirebaseVisionImageMetadata metadata = builder.build();
        this.image = FirebaseVisionImage.fromByteArray(bytes, metadata);

        FirebaseVision.getInstance()
                .getVisionFaceDetector(
                        new Builder()
                                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
                                .setTrackingEnabled(true)
                                .build()
                ).detectInImage(this.image)
                .addOnSuccessListener(this::extractFacesFromImage)
                .addOnFailureListener(e -> {
                    Log.e("FirebaseVisionUtil", "Face Success Callback");
                    String stringBuilder = "FirebaseVisionText Failed to detect Faces:  " +
                            e.getMessage();
                    Log.e(TAG, stringBuilder);
                    imageFaceRetrieveCallback.onFaceRetrieveFailure(e);
                });
    }


    /*
    public void detectTextFromBitmap(ImageTextRetrieveCallback imageTextRetrieveCallback, Bitmap bitmap) {
         this.imageTextRetrieveCallback = imageTextRetrieveCallback;
         if (bitmap == null) {
             Log.e(TAG, "Bitmap is null");
             return;
         }
         this.image = FirebaseVisionImage.fromBitmap(bitmap);
         FirebaseVision.getInstance().getVisionTextDetector()
                 .detectInImage(this.image)
                 .addOnSuccessListener(this::extractTextFromImage)
                 .addOnFailureListener(e -> {
                     String stringBuilder = "FirebaseVisionText Failed to detect:  " +
                             e.getMessage();
                     Log.e(TAG, stringBuilder);
                     imageTextRetrieveCallback.onTextRetrieveFailure(e);
                 });
     }

     public void detectFaceFromBitmap(ImageFaceRetrieveCallback imageFaceRetrieveCallback, Bitmap bitmap) {
         this.imageFaceRetrieveCallback = imageFaceRetrieveCallback;
         if (bitmap == null) {
             Log.e(TAG, "Bitmap is null");
             return;
         }
         this.image = FirebaseVisionImage.fromBitmap(bitmap);
         FirebaseVision.getInstance().getVisionFaceDetector(new Builder().setModeType(2).setMinFaceSize(1.0E-6f).setTrackingEnabled(true).build()).detectInImage(this.image)
                 .addOnSuccessListener(this::extractFacesFromImage)
                 .addOnFailureListener(e -> {
                     String stringBuilder = "FirebaseVisionText Failed to detect Faces:  " +
                             e.getMessage();
                     Log.e(TAG, stringBuilder);
                     imageFaceRetrieveCallback.onFaceRetrieveFailure(e);
                 });
     }
 */
}

