package com.scanner.returm.retumscanner.scanner.contracts;


import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;

public interface ScannerWithFileCallback {
    void onDetectionSuccess(AbstractBaseModel abstractBaseModel);
    void onDetectionFailure(Exception exception);
}
