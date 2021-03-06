package com.ezpay.core.gateway.impl;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.VNPayConstant;
import com.ezpay.main.process.utils.ProcessHash;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.*;

/**
 * @author OI
 */
public class VNPayImpl implements Payment, VNPayConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(VNPayImpl.class);

    private String secretKey = "6D0870CDE5F24F34F3915FB0045120DB";

    private String md5(String message) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hash = md.digest(message.getBytes("UTF-8"));
            // converting byte array to Hexadecimal String
            StringBuilder sb = new StringBuilder(2 * hash.length);
            for (byte b : hash) {
                sb.append(String.format("%02x", b & 0xff));
            }
            return sb.toString();
        } catch (UnsupportedEncodingException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return "";
        } catch (NoSuchAlgorithmException ex) {
            LOGGER.error(ex.getMessage(), ex);
            return "";
        }
    }

    // Util for VNPAY
    private String hashAllFields(Map fields) throws UnsupportedEncodingException {
        // create a list and sort it
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        // create a buffer for the SHA-256 input and add the secure secret first
        StringBuilder sb = new StringBuilder();
        sb.append(secretKey);
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if ((fieldValue != null) && (fieldValue.length() > 0)) {
                sb.append(fieldName);
                sb.append("=");
                sb.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        LOGGER.info("hashAllFields json: " + sb.toString());
        String checkSum = ProcessHash.hmacSHA512(secretKey, sb.toString());
        LOGGER.info("checkSum: " + checkSum);
        return checkSum;
    }

    private String payParams(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getValue()) && StringUtils.hasText(p.getParameter())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                }
            }
        }
        LOGGER.info("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash;
            try {
                secureHash = hashAllFields(fields);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("error: {}" + e.getMessage());
                secureHash = "";
            }
            fields.put(HASHED, secureHash);
        }
        StringBuffer buf = new StringBuffer();
        buf.append('?');
        buf.append(appendQueryFields(fields));
        return buf.toString();
    }

    private String appendQueryFields(Map<String, String> fields) {
        try {
            List fieldNames = new ArrayList(fields.keySet());
            Collections.sort(fieldNames);
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) fields.get(fieldName);
                if ((fieldValue != null) && (fieldValue.length() > 0)) {
                    buf.append(URLEncoder.encode(fieldName, StandardCharsets.US_ASCII.toString()));
                    buf.append('=');
                    buf.append(URLEncoder.encode(fieldValue, StandardCharsets.US_ASCII.toString()));
                    if (itr.hasNext()) {
                        buf.append('&');
                    }
                }
            }
            return buf.toString();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return "";
        }
    }

    // -------------------------------------------------------------------------------------
    @Override
    public boolean checkFields(Map<String, String> fields, String key) {
        if (fields.get(HASHED) == null) {
            return false;
        }
        String hashed = fields.remove(HASHED);

        if (fields.get(RESPONSE) != null) {
            if (key != null && key.length() > 0) {
                secretKey = key;
            }
            String secureHash = "";
            try {
                secureHash = hashAllFields(fields);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("error: {}", e.getMessage());
                secureHash = "";
            }
            return hashed.equalsIgnoreCase(secureHash);
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
                put("00", "Giao d???ch th??nh c??ng");
                put("01", "Giao d???ch ???? t???n t???i");
                put("02", "Merchant kh??ng h???p l??? (ki???m tra l???i vnp_TmnCode)");
                put("03", "D??? li???u g???i sang kh??ng ????ng ?????nh d???ng");
                put("04", "Kh???i t???o GD kh??ng th??nh c??ng do Website ??ang b??? t???m kh??a");
                put("05", "Giao d???ch kh??ng th??nh c??ng do: Qu?? kh??ch nh???p sai m???t kh???u qu?? s??? l???n quy ?????nh. Xin qu?? kh??ch vui l??ng th???c hi???n l???i giao d???ch");
                put("13", "Giao d???ch kh??ng th??nh c??ng do Qu?? kh??ch nh???p sai m???t kh???u x??c th???c giao d???ch (OTP). Xin qu?? kh??ch vui l??ng th???c hi???n l???i giao d???ch.");
                put("07", "Giao d???ch b??? nghi ng??? l?? giao d???ch gian l???n");
                put("09", "Giao d???ch kh??ng th??nh c??ng do: Th???/T??i kho???n c???a kh??ch h??ng ch??a ????ng k?? d???ch v??? InternetBanking t???i ng??n h??ng.");
                put("10", "Giao d???ch kh??ng th??nh c??ng do: Kh??ch h??ng x??c th???c th??ng tin th???/t??i kho???n kh??ng ????ng qu?? 3 l???n");
                put("11", "Giao d???ch kh??ng th??nh c??ng do: ???? h???t h???n ch??? thanh to??n. Xin qu?? kh??ch vui l??ng th???c hi???n l???i giao d???ch.");
                put("12", "Giao d???ch kh??ng th??nh c??ng do: Th???/T??i kho???n c???a kh??ch h??ng b??? kh??a.");
                put("51", "Giao d???ch kh??ng th??nh c??ng do: T??i kho???n c???a qu?? kh??ch kh??ng ????? s??? d?? ????? th???c hi???n giao d???ch.");
                put("65", "Giao d???ch kh??ng th??nh c??ng do: T??i kho???n c???a Qu?? kh??ch ???? v?????t qu?? h???n m???c giao d???ch trong ng??y.");
                put("08", "Giao d???ch kh??ng th??nh c??ng do: H??? th???ng Ng??n h??ng ??ang b???o tr??. Xin qu?? kh??ch t???m th???i kh??ng th???c hi???n giao d???ch b???ng th???/t??i kho???n c???a Ng??n h??ng n??y.");
                put("99", "C??c l???i kh??c (l???i c??n l???i, kh??ng c?? trong danh s??ch m?? l???i ???? li???t k??)");
                put("24", "Kh??ch h??ng click h???y giao d???ch");
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
        if (key != null && key.length() > 0) {
            secretKey = key;
        }
        try {
            return hashAllFields(fields);
        } catch (UnsupportedEncodingException e) {
            return "";
        }
    }

    @Override
    public Map<String, String> getFieldsValues(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getValue()) && StringUtils.hasText(p.getParameter())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                }
            }
        }
        LOGGER.info("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash;
            try {
                secureHash = hashAllFields(fields);
            } catch (UnsupportedEncodingException e) {
                LOGGER.error("error: {}", e.getMessage());
                secureHash = "";
            }
            fields.put(VNPayConstant.HASHED, secureHash);
        }
        return fields;
    }
}
