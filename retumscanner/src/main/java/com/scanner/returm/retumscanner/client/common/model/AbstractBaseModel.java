package com.scanner.returm.retumscanner.client.common.model;

/**
 * The abstractBaseModel will hold the predictions of data which is done by CustomPredictor.
 * <p>
 * To create Custom Model, extends your Model class with AbstractBaseModel.
 * <p>
 * The result of prediction will be return in RetumCallback
 * <p>
 * Usage :
 * <p>
 * CnicModel extends AbstractBaseModel &lt; CnicModel &gt;.
 * </p>
 *
 * @param <T> Use as a return type function like mergeModel, compareTo etc.
 *            <p>
 *            Place the class here, which you have extended from AbstractBaseModel
 */
public abstract class AbstractBaseModel<T extends AbstractBaseModel> implements java.io.Serializable {
    private String filePath;

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public abstract T mergeModel(T b2);

    public abstract int compareTo(T b2);

    public abstract int getScore();

    public abstract int getMaxScore();

    public abstract int getMinScore();
}
