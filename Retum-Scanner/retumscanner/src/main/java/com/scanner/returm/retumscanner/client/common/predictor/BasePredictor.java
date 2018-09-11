package com.scanner.returm.retumscanner.client.common.predictor;


import com.google.firebase.ml.vision.text.FirebaseVisionText;
import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;

import java.io.Serializable;
import java.util.List;

/**
 * The basePredictor class will be provided with List of Strings and Firebase Elements.
 * The element[0] corresponds to dataList[0] data.
 * To create custom Predictor implements BasePredictor &lt; CustomModel &gt; and provide your CustomModel.
 * Custom model should extends AbstractBaseModel
 *
 * @param <T>
 */
public interface BasePredictor<T extends AbstractBaseModel> extends Serializable {


    T process(List<String> dataList, List<FirebaseVisionText.Element> elementList);

}
