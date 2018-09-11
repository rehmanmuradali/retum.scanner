package com.scanner.returm.retumscanner.scanner.contracts;


import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;

public interface ScannerCallback {
    void onDetectionSuccess(byte[] imageBytes, int width, int height, int previewFormat, AbstractBaseModel abstractBaseModel);
    void onDetectionFailure(Exception exception);

}
