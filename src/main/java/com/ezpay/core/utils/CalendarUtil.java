package com.ezpay.core.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;

public class CalendarUtil {
    public static Calendar getCalenderByTimeZone(String zone) {
        return Calendar.getInstance(TimeZone.getTimeZone(zone));
    }

    public static String formatCalendaryyyyMMddHHmmss(Calendar calendar) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyyMMddHHmmss");
        return formatter.format(calendar.getTime());
    }
}
