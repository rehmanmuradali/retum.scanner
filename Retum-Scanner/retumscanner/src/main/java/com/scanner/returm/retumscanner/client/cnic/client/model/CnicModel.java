package com.scanner.returm.retumscanner.client.cnic.client.model;


import com.scanner.returm.retumscanner.client.common.model.AbstractBaseModel;

public class CnicModel extends AbstractBaseModel<CnicModel> {

    private static final int MAX_DETECTION_SCORE = 4;
    private static final int MIN_DETECTION_SCORE = 1;

    private String cnicIssueDate;
    private String cnicExpiryDate;
    private String dateOfBirth;
    private String cnicNumber;

    @Override
    public CnicModel mergeModel(CnicModel b2) {
        cnicNumber = cnicNumber == null ? b2.getCnicNumber() : cnicNumber;
        return this;
    }

    public void setCnicIssueDate(String cnicIssueDate) {
        this.cnicIssueDate = cnicIssueDate;
    }

    public void setCnicExpiryDate(String cnicExpiryDate) {
        this.cnicExpiryDate = cnicExpiryDate;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public void setCnicNumber(String cnicNumber) {
        this.cnicNumber = cnicNumber;
    }

    public String getCnicIssueDate() {
        return cnicIssueDate;
    }

    public String getCnicExpiryDate() {
        return cnicExpiryDate;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    @Override
    public int compareTo(CnicModel b2) {
        return Integer.compare(getScore(), b2.getScore());
    }

    @Override
    public int getScore() {
        int score = 0;
        if (cnicExpiryDate != null) score++;
        if (cnicIssueDate != null) score++;
        if (dateOfBirth != null) score++;
        if (cnicNumber != null) score++;
        return score;
    }

    @Override
    public int getMaxScore() {
        return MAX_DETECTION_SCORE;
    }

    @Override
    public int getMinScore() {
        return MIN_DETECTION_SCORE;
    }


}
