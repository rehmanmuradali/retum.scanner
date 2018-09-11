package com.example.vend.newmlkitdemoapp.splash.view;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;

import com.example.vend.newmlkitdemoapp.R;
import com.scanner.returm.retumscanner.client.cnic.client.model.CnicModel;
import com.scanner.returm.retumscanner.client.cnic.client.preditor.CnicPredictor;
import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;
import com.scanner.returm.retumscanner.client.common.retum.Retum;
import com.scanner.returm.retumscanner.utils.Utility;

public class MainActivity extends AppCompatActivity implements Retum.RetumCallback {


    Button buttonMlKitRealTimeDetection;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonMlKitRealTimeDetection = findViewById(R.id.buttonMlKitRealTimeDetection);

        this.buttonMlKitRealTimeDetection.setOnClickListener(view1 ->

                new Retum().withActivity(this)
                        .setPredictor(new CnicPredictor())
                        .scan(this)

        );
    }


    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }

    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
    }

    @Override
    public void onScanSuccess(AbstractBaseModel abstractBaseModel) {
        CnicModel cnicModel = (CnicModel) abstractBaseModel;
        String filePath = cnicModel.getFilePath();
        String message = "cnic number: " + cnicModel.getCnicNumber() +
                "\ndate of issue: " + cnicModel.getCnicIssueDate() +
                "\ndate of expiry: " + cnicModel.getCnicExpiryDate() +
                "\ndate of birth: " + cnicModel.getDateOfBirth();

        Utility.showAlertBox(this,message,R.string.okay,null);


        Log.e("FilePath: ", filePath);
    }

    @Override
    public void onScanFailure(String message) {
        Log.e("", message);
        Utility.showAlertBox(this,message,R.string.okay,null);
    }
}
