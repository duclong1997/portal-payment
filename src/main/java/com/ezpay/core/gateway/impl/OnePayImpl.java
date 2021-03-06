package com.ezpay.core.gateway.impl;

import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.OnePayConstant;
import com.ezpay.core.entity.MerchantGatewaysetting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.net.URLEncoder;
import java.util.*;

/**
 * @author OI
 */
public class OnePayImpl implements Payment, OnePayConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(OnePayImpl.class);

    private String secretKey = "6D0870CDE5F24F34F3915FB0045120DB";

    // static final String SECURE_SECRET = "your-secure-hash-secret";

    private static final char[] HEX_TABLE = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B',
            'C', 'D', 'E', 'F'};
    private static byte[] decodeHexArray = new byte[103];

    static {
        int i = 0;
        for (byte b : new byte[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'}) {
            decodeHexArray[b] = (byte) i++;
        }
        decodeHexArray['a'] = decodeHexArray['A'];
        decodeHexArray['b'] = decodeHexArray['B'];
        decodeHexArray['c'] = decodeHexArray['C'];
        decodeHexArray['d'] = decodeHexArray['D'];
        decodeHexArray['e'] = decodeHexArray['E'];
        decodeHexArray['f'] = decodeHexArray['F'];
    }

    private String hashAllFields(Map<String, String> fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuffer buf = new StringBuffer();
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0) && fieldName.indexOf("vpc_") == 0) {
                buf.append(fieldName + "=" + fieldValue);
                if (itr.hasNext()) {
                    buf.append('&');
                }
            }
        }
        byte[] mac = null;
        try {
            byte[] b = decodeHexa(secretKey.getBytes());
            SecretKey key = new SecretKeySpec(b, "HMACSHA256");
            Mac m = Mac.getInstance("HMACSHA256");
            m.init(key);
            m.update(buf.toString().getBytes("UTF-8"));
            mac = m.doFinal();
        } catch (Exception e) {
            e.printStackTrace();
        }
        String hashValue = hex(mac);
        LOGGER.info("secureHash: " + hashValue);
        return hashValue;
    }

    private byte[] decodeHexa(byte[] data) throws Exception {
        if (data == null) {
            return null;
        }
        if (data.length % 2 != 0) {
            throw new Exception("Invalid data length:" + data.length);
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        byte b1, b2;
        int i = 0;
        while (i < data.length) {
            b1 = decodeHexArray[data[i++]];
            b2 = decodeHexArray[data[i++]];
            out.write((b1 << 4) | b2);
        }
        out.flush();
        out.close();
        return out.toByteArray();
    }

    private String hex(byte[] input) {
        StringBuffer sb = new StringBuffer(input.length * 2);
        for (int i = 0; i < input.length; i++) {
            sb.append(HEX_TABLE[(input[i] >> 4) & 0xf]);
            sb.append(HEX_TABLE[input[i] & 0xf]);
        }
        return sb.toString();
    }

    private String appendQueryFields(Map<String, String> fields) {
        try {
            List fieldNames = new ArrayList(fields.keySet());
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    buf.append(URLEncoder.encode(fieldName, "UTF-8"));
                    buf.append('=');
                    buf.append(URLEncoder.encode(fieldValue, "UTF-8"));
                }
                if (itr.hasNext()) {
                    buf.append('&');
                }
            }
            return buf.toString();
        } catch (Exception e) {
            return "";
        }
    }

    private String payParams(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getParameter()) && StringUtils.hasText(p.getValue())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                }
            }
        }
        LOGGER.info("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash = hashAllFields(fields);
            fields.put(HASHED, secureHash);
        }
        StringBuffer buf = new StringBuffer();
        buf.append('?');
        buf.append(appendQueryFields(fields));
        return buf.toString();
    }
//-------------------------------------------------------------------------------------------------------


    @Override
    public boolean checkFields(Map<String, String> fields, String key) {
        if (fields.get(HASHED) == null){
            return false;
        }
        String hashed = fields.remove(HASHED);
        if (fields.get(RESPONSE) != null) {
            if (StringUtils.hasText(key)) {
                secretKey = key;
            }
            String secureHash = hashAllFields(fields);
            if (hashed.equalsIgnoreCase(secureHash)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getPaymentLink(List<MerchantGatewaysetting> params, String payUrl) {
        String queryString = payParams(params);
        LOGGER.info("payUrl: " + payUrl + queryString);
        return payUrl + queryString;
    }

    @Override
    public String getResponseDescription(String vResponseCode) {
        Map<String, String> map_result = new HashMap<String, String>() {
            {
                put("0", "Giao d???ch th??nh c??ng");
                put("1", "Ng??n h??ng t??? ch???i giao d???ch");
                put("3", "M?? ????n v??? kh??ng t???n t???i");
                put("4", "Kh??ng ????ng access code");
                put("5", "S??? ti???n kh??ng h???p l???");
                put("6", "M?? ti???n t??? kh??ng t???n t???i");
                put("7", "L???i kh??ng x??c ?????nh");
                put("8", "S??? th??? kh??ng ????ng");
                put("9", "T??n ch??? th??? kh??ng ????ng");
                put("10", "Th??? h???t h???n/Th??? b??? kh??a");
                put("11", "Th??? ch??a ????ng k?? s??? d???ng d???ch v???");
                put("12", "Ng??y ph??t h??nh/H???t h???n kh??ng ????ng");
                put("13", "V?????t qu?? h???n m???c thanh to??n");
                put("21", "S??? ti???n kh??ng ????? ????? thanh to??n");
                put("99", "Kh??ch h??ng h???y thanh to??n");
                put("100","Kh??ch h??ng ch??a thanh to??n");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "L???i kh??ng x??c ?????nh";
    }

    @Override
    public String hashKey(Map<String, String> fields, String key) {
        if (StringUtils.hasText(key)) {
            secretKey = key;
        }
        return hashAllFields(fields);
    }

    @Override
    public Map<String, String> getFieldsValues(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getParameter()) && StringUtils.hasText(p.getValue())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                }
            }
        }
        LOGGER.info("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash = hashAllFields(fields);
            fields.put(HASHED, secureHash);
        }
        return fields;
    }
}
