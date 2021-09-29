package com.ezpay.core.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author OI
 */
public class DateUtils {
    public static int getDifferenceDays(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getDifferenceMilisecond(Date d1, Date d2) {
        long diff = d2.getTime() - d1.getTime();
        return (int) TimeUnit.MILLISECONDS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static int getDiffDay(Date d1, Date d2) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String firstDate = sdf.format(d1);
        String secondDate = sdf.format(d2);
        try {
            d1 = sdf.parse(firstDate);
            d2 = sdf.parse(secondDate);
        } catch (Exception e) {
            e.printStackTrace();
        }
        long diff = Math.abs(d2.getTime() - d1.getTime());
        return (int) TimeUnit.DAYS.convert(diff, TimeUnit.MILLISECONDS);
    }

    public static Date resetBeginOfDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date middleDay(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR, 12);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date clearMilisecond(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    public static Date incr1Day(Date from) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.DAY_OF_MONTH, 1);
        return c.getTime();
    }

    public static Date increaseHour(Date from, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.HOUR_OF_DAY, amount);
        return c.getTime();
    }

    public static Date increaseDay(Date from, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.DAY_OF_YEAR, amount);
        return c.getTime();
    }

    public static Date increaseMonth(Date from, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.MONTH, amount);
        return c.getTime();
    }

    public static Date increaseMilisecond(Date from, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.MILLISECOND, amount);
        return c.getTime();
    }

    public static Date increaseSecond(Date from, int amount) {
        Calendar c = Calendar.getInstance();
        c.setTime(from);
        c.add(Calendar.SECOND, amount);
        return c.getTime();
    }


    public static Date getPolicyDay(Date from, int amount1, int amount2) {
        Calendar c = Calendar.getInstance();
        int amount = amount2 - amount1;
        c.setTime(from);
        c.add(Calendar.DAY_OF_YEAR, amount);
        return c.getTime();
    }

    public static boolean equals(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return false;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.format(d1).equals(df.format(d2));
    }

    public static boolean equalsMilisecond(Date d1, Date d2) {
        if (d1 == null || d2 == null)
            return false;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return df.format(d1).equals(df.format(d2));
    }

    public static Calendar beginDay(Date d) {
        Calendar c = Calendar.getInstance();
        c.setTime(d);
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        return c;
    }

    public static String getWeekdays(Date date) {
        DateFormat df = new SimpleDateFormat("E");
        return date == null ? null : df.format(date);
    }

    public static String formatDateYYYYMMDD(Date date) {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return date == null ? null : df.format(date);
    }

    public static String formatDateYYYYMMDDHHMMSS(Date date) {
        DateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return date == null ? null : df.format(date);
    }

    public static Date parseDateSecond(String s) throws ParseException {
        return new Date(Long.parseLong(s));
    }

    public static Date parseDate(String s) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse(s);
        return d;
    }

    public static Date parseStringToDate(String s, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        Date d = null;
        try {
            d = df.parse(s);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static String parseDateToString(Date date, String pattern) {
        DateFormat df = new SimpleDateFormat(pattern);
        return date == null ? null : df.format(date);
    }

    public static String formatDateYYMMDDHHMM(String s) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date d = df.parse(s);
        df = new SimpleDateFormat("yyMMddHHmm");
        return df.format(d);
    }
}
