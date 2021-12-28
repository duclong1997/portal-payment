package com.ezpay.core.gateway.constant;

public interface MegaPayConstant {
    String MERID = "merId";
    String CURRENCY = "currency";
    String AMOUNT = "amount";
    String INVOICE_NOTE = "invoiceNo";
    String GOODS_NM = "goodsNm";
    // IC : Thẻ tín dụng (Visa/master/JCB...)
    // DC : Thẻ ATM nội địa
    // VA : Tài khoản chuyên dụng.
    // NO: vào trang thanh toán của MegaPay
    String PAY_TYPE = "payType";
    String BUYER_FIRST_NM = "buyerFirstNm";
    String BUYER_LAST_NM = "buyerLastNm";
    String BUYER_PHONE_ = "buyerPhone";
    String BUYER_EMAIL = "buyerEmail";
    String BUYER_ADDR = "buyerAddr";
    String BUYER_CITY = "buyerCity";
    String BUYER_STATE = "buyerState";
    String BUYER_POST_CD = "buyerPostCd";
    String BUYER_COUNTRY = "buyerCountry";
    String RECEIVER_LAST_NM = "receiverLastNm";
    String RECEIVER_FIRST_NM = "receiverFirstNm";
    String RECEIVER_PHONE = "receiverPhone";
    String RECEIVER_ADDR = "receiverAddr";
    String RECEIVER_CITY = "receiverCity";
    String RECEIVER_STATE = "receiverState";
    String RECEIVER_POST_CD = "receiverPostCd";
    String RECEIVER_COUNTRY = "receiverCountry";
    String CALL_BACK_URL = "callBackUrl"; // return url
    String NOTI_URL = "notiUrl";  // update for merchant
    String REQ_DOMAIN = "reqDomain";
    String VAT = "vat";
    String FEE = "fee";
    String NOTAX = "notax"; // mã số thuế của merchant
    String DESCRIPTON = "description";
    String MERCHANT_TOKEN = "merchantToken";
    String REQ_SERVER_IP = "reqServerIP";
    String REQ_CLIENT_VER = "reqClientVer";
    String USER_IP = "userIP";
    String USER_SESSION_ID = "userSessionID";
    String USER_AGENT = "userAgent";
    String USER_LANGUAGE = "userLanguage";  // VN, EN
    String TIMESTAMP = "timeStamp"; // yyyyMMddHHmmss
    String PAY_OPTION = "payOption";
    String PAY_TOKEN = "payToken";
    String USER_ID = "userId";
    String INST_MNT_TYPE = "instmntType";
    String INST_MNT_MON = "instmntMon";
    String MERTRX_ID = "merTrxId"; // merId + unique number
    String USER_FEE = "userFee";
    String GOODS_AMOUNT = "goodsAmount";
    String WINDOW_COLOR = "windowColor"; // default #ef5459
    String WINDOW_TYPE = "windowType"; // 0 : Sử dụng máy tính, 1: Sử dụng điện thoại
    String VA_START_DT = "vaStartDt"; // YYYYMMDDHHMMSS
    String VA_END_DT = "vaEndDt"; // YYYYMMDDHHMMSS
    String VA_CONDITION = "vaCondition";
    String MER_TEMP01 = "mer_temp01";
    String MER_TEMP02 = "mer_temp02";
    String BANK_CODE = "bankCode";
    String RESULT_CD = "resultCd";
    String RESULT_MSG = "resultMsg";
    String TRX_ID = "trxId";
    String BANK_ID = "bankId";
    String STATUS = "status";
    String CARD_NO = "cardNo";
    String TRXDT = "trxDt";
    String TRXTM = "trxTm";
    String ENCODE_KEY = "encodeKey";

    String CURRENCY_VALUE = "VND";
    String GOODS_NM_VALUE = "EPAY";
    String PAY_TYPE_VALUE = "NO";
    String VN = "VN";
    String EN = "EN";
    String REQ_DOMAIN_VALUE = "http://localhost/megapay";
    String WINDOW_COLOR_VALUE = "#ef5459";
    String WINDOW_TYPE_VALUE = "0";
    String RESULT_CD_SUCCESS = "00_000";
    String STATUS_PAYMENT = "0"; //Thanh toán
    String STATUS_VOID = "1"; // Void
    String STATUS_REFUND = "2"; // Refund
    String TRANSACTION_NOT_FOUND = "OR_140";

}
