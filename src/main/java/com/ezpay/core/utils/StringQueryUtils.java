package com.ezpay.core.utils;

import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class StringQueryUtils {
    public static Map<String, String> getFields(String queryString) throws UnsupportedEncodingException {
        if (StringUtils.hasText(queryString)) {
            String strFied = "";
            if (queryString.indexOf("?") != -1) {
                strFied = queryString.split("\\?")[1];
            } else {
                strFied = queryString;
            }
            String[] strFieds = strFied.split("&");
            Map<String, String> fields = new HashMap<String, String>();
            for (String item : strFieds) {
                String[] fied = item.split("=");
                if (fied.length == 2) {
                    fields.put(URLDecoder.decode(fied[0], StandardCharsets.US_ASCII.toString()), URLDecoder.decode(fied[1], StandardCharsets.US_ASCII.toString()));
                }
            }
            return fields;
        }
        return Collections.emptyMap();
    }
}
