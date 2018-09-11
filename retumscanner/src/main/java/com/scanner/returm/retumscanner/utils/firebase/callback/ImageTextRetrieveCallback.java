package com.scanner.returm.retumscanner.utils.firebase.callback;

import com.google.firebase.ml.vision.text.FirebaseVisionText;

import java.util.List;

public interface ImageTextRetrieveCallback {
    void onTextRetrieveFailure(Exception exception);

    void onTextRetrieveSuccess(List<String> list, List<FirebaseVisionText.Element> elementList);
}
