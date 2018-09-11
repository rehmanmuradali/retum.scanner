package com.scanner.returm.retumscanner.client.common.retum;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.predictor.BasePredictor;
import com.scanner.returm.retumscanner.scanner.view.ScannerActivity;

import java.util.Objects;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

/**
 * @author "Rehman Murad Ali"
 * @since 9/8/2018.<br/><br/>
 * Retum allows you to scan <b>Custom Cards</b>
 * <br/>
 * <p>
 * The implementation for Cnic Predictor is provided.
 * <br/><br/>
 * <b>Usage:<br/></b>
 * Retum.withActivity(activity)
 * .setPredictor(new CnicPredictor)
 * .scan(retumCallback)
 */
public class Retum {

    private AppCompatActivity appCompatActivity;
    private BasePredictor basePredictor;
    private static RetumCallback staticRetumCallback;

    /**
     * Use to set Activity
     *
     * @param activity Make Activity extends AppCompactActivity and use getActivity()
     * @return Retum object
     */
    public Retum withActivity(AppCompatActivity activity) {
        appCompatActivity = activity;
        return this;
    }

    /**
     * Use to set Predictor, which will eventually predict card's data.
     *
     * @param basePredictor you can pass custom predictor or pass CnicPredictor for detection and scanning of Cnic Cards
     * @return Retum Object
     */
    public Retum setPredictor(BasePredictor basePredictor) {
        this.basePredictor = basePredictor;
        return this;
    }

    /**
     * Use to start scanning object with provided predictor
     *
     * @param retumCallback Use to pass result back to client.
     */
    public void scan(RetumCallback retumCallback) {
        staticRetumCallback = retumCallback;
        if (isStateValid())
            startDetection();
        else retumCallback.onScanFailure("Validation Failed");
    }


    private void startDetection() {
        Bundle bundle = new Bundle();
        bundle.putSerializable(DummyFragment.BASE_PREDICTOR, basePredictor);
        DummyFragment dummyFragment = new DummyFragment(appCompatActivity.getSupportFragmentManager());
        dummyFragment.setArguments(bundle);

        FragmentTransaction fragmentTransaction = appCompatActivity.getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(dummyFragment, dummyFragment.getClass().getSimpleName());
        fragmentTransaction.commit();

    }

    private boolean isStateValid() {
        return appCompatActivity != null && basePredictor != null;
    }

    /**
     * Use to send data back to client after scan.
     */
    public interface RetumCallback {
        /**
         * The method will be called is Scanning is success.
         *
         * @param abstractBaseModel Client can cast the base Object to their custom Object of their predictor.
         */
        void onScanSuccess(AbstractBaseModel abstractBaseModel);

        /**
         * The method will be called if Scanning failed.
         *
         * @param message Message to show
         */
        void onScanFailure(String message);
    }

    @SuppressLint("ValidFragment")
    public static class DummyFragment extends Fragment {
        public static String BASE_PREDICTOR = "BASE_PREDICTOR";
        private FragmentManager fragmentManager;

        public DummyFragment(FragmentManager fragmentManager) {
            super();
            this.fragmentManager = fragmentManager;
        }


        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            startCnicScannerActivity((BasePredictor) Objects.requireNonNull(getArguments()).getSerializable(BASE_PREDICTOR));
        }

        private void startCnicScannerActivity(BasePredictor basePredictor) {
            Intent intent = new Intent(this.getContext(), ScannerActivity.class);
            intent.putExtra(ScannerActivity.DETECT_OBJECT, ScannerActivity.CNIC_FRONT);
            intent.putExtra(ScannerActivity.BASE_PREDICTOR, basePredictor);
            startActivityForResult(intent, 1);
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            if (resultCode == RESULT_OK) {
                AbstractBaseModel abstractBaseModel = (AbstractBaseModel) data.getSerializableExtra(ScannerActivity.RESULT_BASE_MODEL);
                staticRetumCallback.onScanSuccess(abstractBaseModel);
            } else if (resultCode == RESULT_CANCELED) {
                Exception exception = data == null ? null : (Exception) data.getSerializableExtra(ScannerActivity.EXCEPTION);
                if (exception == null) exception = new Exception("Card Not Detected");
                staticRetumCallback.onScanFailure(exception.getMessage());

            }
            fragmentManager.beginTransaction()
                    .remove(this).commit();
        }
    }

}
