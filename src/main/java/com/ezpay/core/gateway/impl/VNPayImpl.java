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
    private String hashAllFields(Map fields) {
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
                sb.append(fieldValue);
            }
            if (itr.hasNext()) {
                sb.append("&");
            }
        }
        LOGGER.info("hashAllFields json: " + sb.toString());
        String checkSum = ProcessHash.encryptSha256(sb.toString());
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
        System.out.println("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash = hashAllFields(fields);
            fields.put(HASHED, secureHash);
            fields.put(HASHTYPE, HASHTYPE_VALUE);
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
        if (fields.get(HASHED) == null){
            return false;
        }
        String hashed = fields.remove(HASHED);
        String hashtype = fields.remove(HASHTYPE);

        if (fields.get(RESPONSE) != null) {
            if (key != null && key.length() > 0) {
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
                put("00", "Giao dịch thành công");
                put("01", "Giao dịch đã tồn tại");
                put("02", "Merchant không hợp lệ (kiểm tra lại vnp_TmnCode)");
                put("03", "Dữ liệu gửi sang không đúng định dạng");
                put("04", "Khởi tạo GD không thành công do Website đang bị tạm khóa");
                put("05", "Giao dịch không thành công do: Quý khách nhập sai mật khẩu quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch");
                put("13", "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.");
                put("07", "Giao dịch bị nghi ngờ là giao dịch gian lận");
                put("09", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.");
                put("10", "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần");
                put("11", "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.");
                put("12", "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.");
                put("51", "Giao dịch không thành công do: Tài khoản của quý khách không đủ số dư để thực hiện giao dịch.");
                put("65", "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.");
                put("08", "Giao dịch không thành công do: Hệ thống Ngân hàng đang bảo trì. Xin quý khách tạm thời không thực hiện giao dịch bằng thẻ/tài khoản của Ngân hàng này.");
                put("99", "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)");
                put("24", "Khách hàng click hủy giao dịch");
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
        if (key != null && key.length() > 0) {
            secretKey = key;
        }
        return hashAllFields(fields);
    }
}
