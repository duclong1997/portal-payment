package com.ezpay.core.gateway.constant;

/**
 * @author OI
 */
public interface OnePayConstant {
    String HASHED = "vpc_SecureHash";
    String RESPONSE = "vpc_TxnResponseCode";
    String CURRENCY = "vpc_Currency";
    String LOCALE = "vpc_Locale";
    String RETURN_URL = "vpc_ReturnURL";
    String TRANSACTION_REF = "vpc_MerchTxnRef";
    String INFO = "vpc_OrderInfo";
    String ORDER_AMOUNT = "vpc_Amount";
    String PAY_IP = "vpc_TicketNo";
    String AGAIN_LINK = "AgainLink";
    String STREET = "vpc_SHIP_Street01";
    String PROVINCE = "vpc_SHIP_Provice";
    String CITY = "vpc_SHIP_City";
    String COUNTRY = "vpc_SHIP_Country";
    String CUS_PHONE = "vpc_Customer_Phone";
    String CUS_EMAIL = "vpc_Customer_Email";
    String CUS_ID = "vpc_Customer_Id";
    String ADDITION_DATA = "vpc_AdditionData";
    String TRANSACTION_NO = "vpc_TransactionNo";
    String VERSION = "vpc_Version";
    String MERCHANT = "vpc_Merchant";
    String COMMAND = "vpc_Command";
    String TITLE = "Title";
    String MESSAGE = "vpc_Message";
    String ACCESSCODE = "vpc_AccessCode";
    String PASSWORD = "vpc_Password";
    String USER = "vpc_User";
    String SECRET_KEY = "secretKey";

    //value
    String VERSION_VALUE = "2";
    String COMMAND_VALUE = "pay";
    String CURRENCY_VALUE = "VND";
    String COMMAND_VALUE_QUERY = "queryDR";
    String VERSION_VALUE_QUERY = "1";

    String LOCALE_VN = "vn";
    String LOCALE_EN = "en";
}
