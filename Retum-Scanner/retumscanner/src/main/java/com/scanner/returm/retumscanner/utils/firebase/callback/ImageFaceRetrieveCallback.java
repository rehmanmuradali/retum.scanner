package com.scanner.returm.retumscanner.utils.firebase.callback;

import java.util.List;

public interface ImageFaceRetrieveCallback {
    void onFaceRetrieveFailure(Exception exception);

    void onFaceRetrieveSuccess(List<Integer> list);
}
