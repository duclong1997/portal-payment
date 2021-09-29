package com.ezpay.core.gateway.constant;

/**
 * @author OI
 */
public interface VNPayConstant {
    String HASHED = "vnp_SecureHash";
    String HASHTYPE = "vnp_SecureHashType";
    String RESPONSE = "vnp_ResponseCode";
    String VERSION = "vnp_Version";
    String COMMAND = "vnp_Command";
    String LOCALE = "vnp_Locale";
    String ORDER_AMOUNT = "vnp_Amount";
    String CURRCODE = "vnp_CurrCode";
    String INFO = "vnp_OrderInfo";
    String RETURN_URL = "vnp_ReturnUrl";
    String TRANSACTION_REF = "vnp_TxnRef";
    String TRANSACTION_NO = "vnp_TransactionNo";
    String CREATE_DATE = "vnp_CreateDate";
    String IP_ADDR = "vnp_IpAddr";
    String PAY_DATE = "vnp_PayDate";
    String MESSAGE = "vpc_Message";
    String TMN_CODE = "vnp_TmnCode";
    String TRANSDATE = "vnp_TransDate";
    String SECRET_KEY = "secretKey";
    String ORDER_TYPE = "vnp_OrderType";

    //value
    String HASHTYPE_VALUE = "SHA256";
    String VERSION_VALUE = "2.0.0";
    String COMMAND_VALUE = "pay";
    String CURRCODE_VALUE = "VND";
    String COMMAND_QERY_VALUE = "querydr";
    String ORDER_TYPE_VALUE = "billpayment";

    String LOCALE_VN = "vn";
    String LOCALE_EN = "en";
}
