package com.ezpay.core.gateway.impl;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.ViettelPayConstant;
import org.apache.commons.lang.StringEscapeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.*;

public class ViettelPayImpl implements Payment, ViettelPayConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(ViettelPayImpl.class);

    private String secretKey = "96abc96secret";
    private String access_code = "access_code";

    private static final String HMAC_SHA1_ALGORITHM = "HmacSHA1";

    private String calculateRFC2104HMAC(String data, String key)
            throws java.security.SignatureException {
        String result;
        try {
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA1_ALGORITHM);
            Mac mac = Mac.getInstance(HMAC_SHA1_ALGORITHM);
            mac.init(signingKey);
            byte[] rawHmac = mac.doFinal(data.getBytes());
            result = new String(Base64.getEncoder().encode(rawHmac));
        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }
        return result;
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
                    buf.append(URLEncoder.encode(fieldName, StandardCharsets.UTF_8.name()));
                    buf.append('=');
                    buf.append(URLEncoder.encode(fieldValue, StandardCharsets.UTF_8.name()));
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

    private String hashAllFields(Map<String, String> fields) {
        List fieldNames = new ArrayList(fields.keySet());
        Collections.sort(fieldNames);
        StringBuffer buf = new StringBuffer();
        buf.append(access_code);
        Iterator itr = fieldNames.iterator();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) fields.get(fieldName);
            if (StringUtils.hasText(fieldValue) && !fieldName.equals(RETURN_URL) && !fieldName.equals(CANCEL_URL)) {
                buf.append(fieldValue);
            }
        }

        try {
            LOGGER.info("data check sum: " + buf.toString());
            String checkSum = calculateRFC2104HMAC(buf.toString(), secretKey);
            LOGGER.info("checkSum: " + checkSum);
            return checkSum;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return null;
        }
    }

    private String payParams(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getParameter()) && StringUtils.hasText(p.getValue())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM && !p.getParameter().equals(ViettelPayConstant.LOCALE)) {
                    fields.put(p.getParameter(), p.getValue());
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                } else if (p.getType() == MerchantGatewaysetting.OTHER_PARAM) {
                    access_code = p.getValue();
                }
            }
        }
        System.out.println("secretKey: " + secretKey);
        if (StringUtils.hasText(secretKey) && StringUtils.hasText(access_code)) {
            String secureHash = hashAllFields(fields);
            fields.put(CHECK_SUM, secureHash);
        }
        StringBuffer buf = new StringBuffer();
        buf.append('?');
        buf.append(appendQueryFields(fields));
        return buf.toString();
    }

    //==================================================================================================================
    @Override
    public boolean checkFields(Map<String, String> fields, String key) {
        Map<String, String> params = new HashMap<String, String>(fields);
        String hashed = params.remove(CHECK_SUM);
        hashed = StringEscapeUtils.unescapeJava(hashed);
        hashed =hashed.replaceAll(" ","+");
        access_code = params.remove(ACCESS_CODE);

        if (fields.get(ERROR_CODE) != null) {
            if (key != null && key.length() > 0) {
                secretKey = key;
            }
            String secureHash = hashAllFields(params);
            if (hashed.equals(secureHash)) {
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
                put("00", "Giao dịch thành công");
                put("22", "KH nhập sai OTP tại CTT");
                put("V01", "Sai check_sum");
                put("V02", "KH nhập sai OTP tại CTT");
                put("V03", "OTP hết hạn");
                put("21", "KH nhập sai mật khẩu(mã PIN)");
                put("685", "KH nhập sai mật khẩu(mã PIN)");
                put("16", "KH không đủ số dư để thanh toán");
                put("W04", "Kết nối timeout(bao gồm trường hợp user không thao tác trên web CTT thì sau 3phút sẽ bị redirect về return_url)");
                put("V04", "Có lỗi khi truy vấn hệ thống tại VIETTEL");
                put("V05", "Không xác nhận được giao dịch(Gọi sang API xác nhận giao dịch của đối tác thất bại)");
                put("V06", "Khách hàng hủy thanh toán");
                put("S_MAINTAIN", "CTT bảo trì");
                put("99", "Lỗi không xác định");
                put("M01", " Mã đối tác chưa được đăng ký (liên hệ kỹ thuật Viettel để kiểm tra)");
                put("M02", " Chưa thiết lập tài khoản nhận tiền cho đối tác(liên hệ kỹ thuật Viettel)");
                put("M03", " Hình thức thanh toán không phù hợp (liên hệ kỹ thuật Viettel)");
                put("M04", " Ảnh QR bị lỗi hoặc không đọc được giá trị cần thiết từ ảnh");
                put("813", " Lỗi kết nối tới CTT");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "Lỗi không xác định";
    }

    @Override
    public String hashKey(Map<String, String> fields, String key) {
        Map<String, String> params = new HashMap<String, String>(fields);
        if (key != null && key.length() > 0) {
            secretKey = key;
        }
        access_code = params.get(ViettelPayConstant.ACCESS_CODE);
        if (StringUtils.hasText(access_code)) {
            String accessCode = params.remove(ViettelPayConstant.ACCESS_CODE);
            String checkSum = params.remove(ViettelPayConstant.CHECK_SUM);
        }
        return hashAllFields(params);
    }
}
