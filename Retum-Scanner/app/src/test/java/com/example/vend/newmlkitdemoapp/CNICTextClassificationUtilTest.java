package com.example.vend.newmlkitdemoapp;


import com.example.vend.newmlkitdemoapp.utils.cnic.CNICTextClassificationUtil;
import com.example.vend.newmlkitdemoapp.utils.DateUtil;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;

public class CNICTextClassificationUtilTest {

    @Test
    public void processDataForDatesTest_PassingValidInputs() {
        List<String> dateList = new ArrayList<>();
        dateList.add("23.02.1996");
        dateList.add("20.03.2014");
        dateList.add("20.03.2021");

        CNICTextClassificationUtil cnicTextClassificationUtil = new CNICTextClassificationUtil(dateList);
        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("23.02.1996"),
                DateUtil.getDateFromText("20.03.2014")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfBirth(), "23 02 1996");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfIssue(), "20 03 2014");


        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("23.02.1996"),
                DateUtil.getDateFromText("20.03.2021")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfBirth(), "23 02 1996");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfExpiry(), "20 03 2021");


        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("20.03.2014"),
                DateUtil.getDateFromText("20.03.2021")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfIssue(), "20 03 2014");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfExpiry(), "20 03 2021");


        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("15.10.2009"),
                DateUtil.getDateFromText("30.09.2019")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfIssue(), "15 10 2009");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfExpiry(), "30 09 2019");


        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("15.10.1996"),
                DateUtil.getDateFromText("30.09.2014")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfBirth(), "15 10 1996");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfIssue(), "30 09 2014");


        cnicTextClassificationUtil.validateDateForIssueAndExpiry(
                DateUtil.getDateFromText("23.02.1996"),
                DateUtil.getDateFromText("20.03.2021")

        );

        assertEquals(cnicTextClassificationUtil.getCnicDateOfBirth(), "23 02 1996");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfExpiry(), "20 03 2021");


    }


    @Test
    public void processDataForDatesTest_PassingValidInputsWithLifetime() {
        List<String> dateList = new ArrayList<>();
        dateList.add("02.02.1957");
        dateList.add("17.09.2013");
        dateList.add("Lifetime");

        CNICTextClassificationUtil cnicTextClassificationUtil = new CNICTextClassificationUtil(dateList);


        assertEquals(cnicTextClassificationUtil.getCnicDateOfBirth(), "02 02 1957");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfIssue(), "17 09 2013");
        assertEquals(cnicTextClassificationUtil.getCnicDateOfExpiry(), "Lifetime");

    }


    @Test
    public void getCnicFromList_PassingValidValues() {
        List<String> dateList = new ArrayList<>();
        dateList.add("02.02.1957");
        dateList.add("17.09.2013");
        dateList.add("42101-79044351");
        CNICTextClassificationUtil cnicTextClassificationUtil = new CNICTextClassificationUtil(dateList);
        assertEquals(cnicTextClassificationUtil.getCnicNumber(), "4210179044351");
    }


}
