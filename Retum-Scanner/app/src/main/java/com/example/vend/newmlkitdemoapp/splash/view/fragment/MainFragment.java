package com.example.vend.newmlkitdemoapp.splash.view.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.vend.newmlkitdemoapp.R;
import com.scanner.returm.retumscanner.client.cnic.client.model.CnicModel;
import com.scanner.returm.retumscanner.client.cnic.client.preditor.CnicPredictor;
import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.retum.Retum;
import com.scanner.returm.retumscanner.utils.Utility;

public class MainFragment extends Fragment implements Retum.RetumCallback {


    Button buttonMlKitRealTimeDetection;


    @Nullable
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        buttonMlKitRealTimeDetection = view.findViewById(R.id.buttonMlKitRealTimeDetection);

        this.buttonMlKitRealTimeDetection.setOnClickListener(view1 ->

                new Retum().withActivity((AppCompatActivity) getActivity())
                        .setPredictor(new CnicPredictor())
                        .scan(this)

        );


        return view;
    }


    @Override
    public void onScanSuccess(AbstractBaseModel abstractBaseModel) {
        CnicModel cnicModel = (CnicModel) abstractBaseModel;
        String filePath = cnicModel.getFilePath();
        String message = "cnic number: " + cnicModel.getCnicNumber() +
                "\ndate of issue: " + cnicModel.getCnicIssueDate() +
                "\ndate of expiry: " + cnicModel.getCnicExpiryDate() +
                "\ndate of birth: " + cnicModel.getDateOfBirth();

        Utility.showAlertBox(getActivity(),message,R.string.okay,null);


        Log.e("FilePath: ", filePath);
    }

    @Override
    public void onScanFailure(String message) {
        Log.e("", message);
        Utility.showAlertBox(getActivity(),message,R.string.okay,null);
    }


}
