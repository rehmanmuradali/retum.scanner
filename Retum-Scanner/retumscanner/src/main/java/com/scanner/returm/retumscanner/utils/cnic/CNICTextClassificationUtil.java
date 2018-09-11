package com.scanner.returm.retumscanner.utils.cnic;


import com.scanner.returm.retumscanner.utils.DateUtil;
import com.scanner.returm.retumscanner.utils.Utility;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.regex.Pattern;

/**
 * deprecated.
 * Use to Classification of CNIC. Test Cases are present.
 */
public class CNICTextClassificationUtil {
    private static final int INT_ONE = 1;
    private static final int INT_THREE = 3;
    private static final int INT_TWO = 2;
    private static final int MAX_AGE_OF_USER = 120;
    private static final int MAX_AGE_TO_ISSUE_NIC = 23;
    private static final int MIN_DIFF_OF_DOB_AND_ISSUE_DATE = 17;
    private static final int MIN_USER_AGE_FOR_NIC = 18;
    private Date cnicDateOfBirth;
    private Date cnicDateOfExpiry;
    private Date cnicDateOfIssue;
    private List<String> listData;
    private boolean isLifeTimePresent = false;
    private static final String DATE_FORMAT = "dd MM yyyy";
    private String cnicNumber;

    public CNICTextClassificationUtil(List<String> listData) {
        setListData(listData);
    }

    private void setListData(List<String> listData) {
        this.listData = listData;
        searchLifeTimeFromString();
        processDataForDates();
        getCnicNumberFromList();
    }

    private void searchLifeTimeFromString() {
        for (String data : this.listData) {
            if (data.equalsIgnoreCase("Lifetime")) {
                this.isLifeTimePresent = true;
                return;
            }
        }
    }

    public String getCnicDateOfIssue() {
        return this.cnicDateOfIssue == null ? null : Utility.getFormattedDate(cnicDateOfIssue.getTime(), DATE_FORMAT);
    }

    public String getCnicDateOfExpiry() {
        if (isLifeTimePresent)
            return "Lifetime";
        return this.cnicDateOfExpiry == null ? null : Utility.getFormattedDate(cnicDateOfExpiry.getTime(), DATE_FORMAT);
    }

    public String getCnicDateOfBirth() {
        return this.cnicDateOfBirth == null ? null : Utility.getFormattedDate(cnicDateOfBirth.getTime(), DATE_FORMAT);
    }

    private void processDataForDates() {
        List<Date> dateList = DateUtil.getSortedDatesFromListOfString(this.listData);
        dateList = DateUtil.validateDateList(dateList, MAX_AGE_OF_USER, 15);
        if (dateList.size() >= INT_THREE) {
            this.cnicDateOfBirth = dateList.get(0);
            this.cnicDateOfIssue = dateList.get(1);
            this.cnicDateOfExpiry = dateList.get(2);
        } else if (dateList.size() == INT_TWO) {
            validateDateForIssueAndExpiry(dateList.get(0), dateList.get(1));
        } else if (dateList.size() == INT_ONE) {
            this.cnicDateOfBirth = dateList.get(0);
        }
    }


    public void validateDateForIssueAndExpiry(Date date1, Date date2) {
        Calendar c1 = DateUtil.toCalendar(date1);
        Calendar c2 = DateUtil.toCalendar(date2);

        int d1 = c1.get(Calendar.DAY_OF_MONTH);
        int d2 = c2.get(Calendar.DAY_OF_MONTH);
        int m1 = c1.get(Calendar.MONTH);
        int m2 = c2.get(Calendar.MONTH);
        int y1 = c1.get(Calendar.YEAR);
        int y2 = c2.get(Calendar.YEAR);

        int previousMonthOfDate1 = m1 - 1;
        //month zeroth based
        Calendar c3 = new GregorianCalendar(c2.get(Calendar.YEAR), previousMonthOfDate1, 1);
        int maxDaysOfPreviousMonth = c3.getActualMaximum(Calendar.DAY_OF_MONTH);


        if ((d1 == d2 && m1 == m2 && Math.abs(y1 - y2) < MIN_USER_AGE_FOR_NIC) ||
                (m2 == previousMonthOfDate1 && d2 == maxDaysOfPreviousMonth && Math.abs(y1 - y2) < MIN_DIFF_OF_DOB_AND_ISSUE_DATE)) {
            this.cnicDateOfIssue = c1.getTime();
            this.cnicDateOfExpiry = c2.getTime();
        } else {
            this.cnicDateOfBirth = c1.getTime();
            predictDateOfIssueOrDateOfExpiry(c1, c2);
        }

/*
        this.cnicDateOfIssue = (Date) this.dateList.get(0);
        this.cnicDateOfExpiry = (Date) this.dateList.get(1);*/
    }

    private void predictDateOfIssueOrDateOfExpiry(Calendar dateOfBirth, Calendar c2) {
        if (isLifeTimePresent)
            this.cnicDateOfIssue = c2.getTime();
        else if (Math.abs(dateOfBirth.get(Calendar.YEAR) - c2.get(Calendar.YEAR)) < MAX_AGE_TO_ISSUE_NIC)
            this.cnicDateOfIssue = c2.getTime();
        else this.cnicDateOfExpiry = c2.getTime();
    }

    private static boolean matchIdentityNumber(String data) {
        if (data == null) {
            return false;
        }
        data = data.replaceAll("[-+.^:,]", "");
        return data.matches(Pattern.compile("\\d{13}").pattern());
    }

    public String getCnicNumber() {
        return cnicNumber;
    }

    private void getCnicNumberFromList() {
        for (String data : this.listData) {
            if (matchIdentityNumber(data)) {
                this.cnicNumber = data.replaceAll("[-+.^:,]", "");
                return;
            }
        }
    }
}
