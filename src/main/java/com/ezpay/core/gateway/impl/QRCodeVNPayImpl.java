package com.ezpay.core.gateway.impl;

import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.gateway.QRCode;
import com.ezpay.core.gateway.constant.QRCodeVNPayConstant;
import com.ezpay.core.gateway.model.QrcodeResponse;
import com.ezpay.core.gateway.model.res.QrcodeVnpayResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author OI
 */
public class QRCodeVNPayImpl implements QRCode, QRCodeVNPayConstant {
    private final static Logger LOGGER = LoggerFactory.getLogger(QRCodeVNPayImpl.class);

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
    private String hashRequestFields(Map fields) {
        StringBuilder sb = new StringBuilder();
        sb.append(fields.get(APP_ID));
        sb.append("|");
        sb.append(fields.get(MERCHANT_NAME));
        sb.append("|");
        sb.append(fields.get(SERVICE_CODE));
        sb.append("|");
        sb.append(fields.get(COUNTRY_CODE));
        sb.append("|");
        sb.append(fields.get(MASTER_MER_CODE));
        sb.append("|");
        sb.append(fields.get(MERCHANT_TYPE));
        sb.append("|");
        sb.append(fields.get(MERCHANT_CODE));
        sb.append("|");
        sb.append(fields.get(TERMINAL_ID));
        sb.append("|");
        sb.append(fields.get(PAY_TYPE));
        sb.append("|");
        sb.append(fields.get(PRODUCT_ID));
        sb.append("|");
        sb.append(fields.get(TXN_ID));
        sb.append("|");
        sb.append(fields.get(AMOUNT));
        sb.append("|");
        sb.append(fields.get(TIP_AND_FEE));
        sb.append("|");
        sb.append(fields.get(CCY));
        sb.append("|");
        sb.append(fields.get(EXP_DATE));
        sb.append("|");
        sb.append(secretKey);
        LOGGER.info("hashRequestFields checkSum: " + sb.toString());
        return md5(sb.toString());
    }

    // Util for VNPAY
    private String hashResponseFields(QrcodeVnpayResponse qvs, String url) {
        StringBuilder sb = new StringBuilder();
        sb.append(qvs.getCode());
        sb.append("|");
        sb.append(qvs.getMessage());
        sb.append("|");
        sb.append(qvs.getData());
        sb.append("|");
        sb.append(qvs.getUrl());
        sb.append("|");
        sb.append(secretKey);
        LOGGER.info("hashRequestFields checkSum: " + sb.toString());
        String checkSum = md5(sb.toString());
        LOGGER.info("checkSum: " + sb.toString());
        return checkSum;
    }


    private String payParams(List<MerchantGatewaysetting> params) {
        Map<String, String> fields = new HashMap<String, String>();
        StringBuffer buf = new StringBuffer("{");
        for (MerchantGatewaysetting p : params) {
            if (StringUtils.hasText(p.getValue()) && StringUtils.hasText(p.getParameter())) {
                if (p.getType() == MerchantGatewaysetting.FIXED_PARAM) {
                    fields.put(p.getParameter(), p.getValue());
                    buf.append("\"");
                    buf.append(p.getParameter());
                    buf.append("\":\"");
                    buf.append(p.getValue());
                    buf.append("\",");
                } else if (p.getType() == MerchantGatewaysetting.KEY_PARAM) {
                    secretKey = p.getValue();
                }
            }
        }
        LOGGER.info("secretKey: " + secretKey);
        if (secretKey != null && secretKey.length() > 0) {
            String secureHash = hashRequestFields(fields);
            buf.append("\"");
            buf.append(CHECKSUM);
            buf.append("\":\"");
            buf.append(secureHash);
            buf.append("\"");
        }
        buf.append("}");
        return buf.toString();
    }

    private String hashPaymentFields(Map<String, String> fields) {
        StringBuilder sb = new StringBuilder();
        sb.append(fields.get(CODE));
        sb.append("|");
        sb.append(fields.get(MSG_TYPE));
        sb.append("|");
        sb.append(fields.get(TXN_ID));
        sb.append("|");
        sb.append(fields.get(QR_TRACE));
        sb.append("|");
        sb.append(fields.get(BANK_CODE));
        sb.append("|");
        sb.append(fields.get(MOBILE));
        sb.append("|");
        sb.append(fields.get(ACCOUNT_NO));
        sb.append("|");
        sb.append(fields.get(AMOUNT));
        sb.append("|");
        sb.append(fields.get(PAY_DATE));
        sb.append("|");
        sb.append(fields.get(MERCHANT_CODE));
        sb.append("|");
        sb.append(secretKey);
        LOGGER.info("hashRequestFields checkSum: " + sb.toString());
        return md5(sb.toString());
    }

    private String hashFields(Map<String, String> fields) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> field : fields.entrySet()) {
            sb.append(field.getValue());
            sb.append("|");
        }
        if (sb.length() > 0 ) {
            sb.deleteCharAt(sb.length() - 1);
        }
        LOGGER.info("hashFields checksum: " + sb.toString());
        // ma hoa md5 for vnpay qrcode
        return md5(sb.toString());
    }

    //-----------------------------------------------------------------------------------------------------------------

    @Override
    public boolean checkQRCode(QrcodeResponse fields, String key, String url) {

        if (fields != null) {
            QrcodeVnpayResponse qvs = (QrcodeVnpayResponse) fields;
            if (key != null && key.length() > 0) {
                secretKey = key;
            }
            String secureHash = hashResponseFields(qvs, url);
            if (qvs.getChecksum().equalsIgnoreCase(secureHash)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getQRCode(List<MerchantGatewaysetting> params) {
        String strParams = payParams(params);
        LOGGER.info("params qrcode vnpay request: " + strParams);
        return strParams;
    }

    @Override
    public String getResponseQRCodeDescription(String vResponseCode) {
        Map<String, String> map_result = new HashMap<String, String>() {
            {
                put("00", "Success.");
                put("01", "Data input is not in format");
                put("04", "Insert data QrCode failed");
                put("05", "Ip is denied");
                put("06", "False checkSum");
                put("07", "Merchant is not exist");
                put("09", "Service code is invalid");
                put("10", "AppId is invalid");
                put("11", "Merchant is not active");
                put("12", "Master merchant code is null or empty");
                put("15", "ConsumerID is null or empty");
                put("16", "Purpose is null or empty");
                put("21", "Terminal is invalid");
                put("24", "Terminal is inactive");
                put("99", "Internal errors");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "Lỗi không xác định";
    }

    @Override
    public String getResponseQueryQRCodeDescription(String vResponseCode) {
        Map<String, String> map_result = new HashMap<String, String>() {
            {
                put("00", "Giao dịch thành công");
                put("01", "Không tìm thấy giao dịch");
                put("02", "PayDate không đúng định dạng.");
                put("03", "TxnId không được null hoặc empty.");
                put("04", "Giao dich thất bại.");
                put("05", "Giao dich nghi vấn.");
                put("14", "IP bị khóa.");
                put("11", "Dữ liệu đầu vào không đúng định dạng.");
                put("99", "Internal errors");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "Lỗi không xác định";
    }

    @Override
    public boolean checkPayment(Map<String, String> fields, String key) {
        if (StringUtils.hasText(fields.get(CODE))) {
            if (key != null && key.length() > 0) {
                secretKey = key;
            }
            String secureHash = hashPaymentFields(fields);
            LOGGER.info("MD5 checksum: " + secureHash);
            LOGGER.info("request checksum: " + fields.get(CHECKSUM).toLowerCase());
            if (fields.get(CHECKSUM).equalsIgnoreCase(secureHash)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getResponseDescription(String vResponseCode) {
        Map<String, String> map_result = new HashMap<String, String>() {
            {
                put("00", "Đặt hàng thành công");
                put("01", "Thiếu hàng trong đơn hàng");
                put("02", "Hết toàn bộ hàng trong đơn hàng");
                put("03", "Đơn hàng đã được thanh toán");
                put("04", "Lỗi tạo đơn hàng");
                put("05", "Đơn hàng đang được xử lí");
                put("06", "Sai thông tin xác thực");
                put("07", "Số tiền không chính xác");
                put("08", "Giao dịch timout");
                put("09", "QR hết hạn thanh toán");
                put("10", "IP không được truy cập");
            }
        };
        String result = map_result.get(vResponseCode);
        if (result != null) {
            return result;
        }
        return "Lỗi không xác định";
    }

    @Override
    public String hashKeyQueryTransactionRequest(Map<String, String> fields, String secretKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(fields.get(PAY_DATE));
        sb.append("|");
        sb.append(fields.get(TXN_ID));
        sb.append("|");
        sb.append(fields.get(MERCHANT_CODE));
        sb.append("|");
        sb.append(fields.get(TERMINAL_IDD));
        sb.append("|");
        sb.append(secretKey);
        LOGGER.info("checksum: " + sb.toString());
        return md5(sb.toString());
    }

    @Override
    public String hashKeyQueryTransactionResponse(Map<String, String> fields, String secretKey) {
        StringBuilder sb = new StringBuilder();
        sb.append(fields.get(MASTER_MERCHANT_CODE));
        sb.append("|");
        sb.append(fields.get(MERCHANT_CODE));
        sb.append("|");
        sb.append(fields.get(TERMINAL_IDD));
        sb.append("|");
        sb.append(fields.get(TXN_ID));
        sb.append("|");
        sb.append(fields.get(PAY_DATE));
        sb.append("|");
        sb.append(fields.get(BANK_CODE));
        sb.append("|");
        sb.append(fields.get(QR_TRACE));
        sb.append("|");
        sb.append(fields.get(DEBIT_AMOUNT));
        sb.append("|");
        sb.append(fields.get(REAL_AMOUNT));
        sb.append("|");
        sb.append(secretKey);
        LOGGER.info("checksum: " + sb.toString());
        return md5(sb.toString());
    }
}
