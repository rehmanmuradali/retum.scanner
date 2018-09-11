package com.scanner.returm.retumscanner.utils;

import android.annotation.SuppressLint;
import android.support.annotation.NonNull;


import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class DateUtil {

    @SuppressLint("SimpleDateFormat")
    private static Date getFormattedDate(@NonNull String dateString, @NonNull String format) {
        try {
            return new SimpleDateFormat(format).parse(dateString);
        } catch (Exception e) {
            return null;
        }
    }

    public static Date getDateFromText(@NotNull String dateString) {
        List<String> possibleDateFormat = new ArrayList<>();
        possibleDateFormat.add("dd/MM/yyyy");
        possibleDateFormat.add("dd.MM.yyyy");
        possibleDateFormat.add("dd-MM-yyyy");
        for (String formatString : possibleDateFormat) {
            Date date = getFormattedDate(dateString, formatString);
            if (date != null) {
                return date;
            }
        }
        return null;
    }

    public static List<Date> getSortedDatesFromListOfString(@NotNull List<String> blockData) {
        //noinspection unchecked
        blockData = Utility.deleteDuplicatesFromList(blockData);
        List<Date> dateList = new ArrayList<>();
        for (String data : blockData) {
            Date date = getDateFromText(data);
            if (date != null) {
                dateList.add(date);
            }
        }
        Collections.sort(dateList);
        return dateList;
    }

    public static List<Date> validateDateList(List<Date> dateList, int MAX_AGE_OF_USER, int MAX_VALID_DATE) {
        ArrayList<Date> tempDateList = new ArrayList<>();
        for (Date date : dateList) {
            Calendar minimumCalendar = Calendar.getInstance();
            Calendar maximumCalendar = Calendar.getInstance();
            minimumCalendar.add(Calendar.YEAR, -MAX_AGE_OF_USER);
            maximumCalendar.add(Calendar.YEAR, MAX_VALID_DATE);
            if (date.compareTo(minimumCalendar.getTime()) >= 0 && date.compareTo(maximumCalendar.getTime()) <= 0) {
                tempDateList.add(date);
            }
        }
        return tempDateList;
    }

    public static Calendar toCalendar(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        return cal;
    }
}
