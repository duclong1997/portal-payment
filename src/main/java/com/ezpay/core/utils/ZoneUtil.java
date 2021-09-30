package com.ezpay.core.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimeZone;

public class ZoneUtil {
    public static final String ZONE_HO_CHI_MINH = "Asia/Ho_Chi_Minh";
    public static final String ZONE_NEW_YORK = "America/New_York";

    public static List<String> getZoneAvailableIds() {
        return new ArrayList<>(Arrays.asList(TimeZone.getAvailableIDs()));
    }
}
