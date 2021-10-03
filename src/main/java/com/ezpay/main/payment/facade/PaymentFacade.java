package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.entity.MerchantProject;
import com.ezpay.core.entity.Transaction;
import com.ezpay.core.facade.BaseFacade;
import com.ezpay.core.gateway.constant.*;
import com.ezpay.core.utils.CalendarUtil;
import com.ezpay.core.utils.DateUtils;
import com.ezpay.core.utils.ZoneUtil;
import com.ezpay.main.payment.exception.TransactionIsNotExistException;
import com.ezpay.main.payment.model.req.CreateRequest;
import com.ezpay.main.payment.model.req.UpdateQrcodeVnpayRequest;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.payment.utils.PaymentConstant;
import com.google.gson.Gson;
import org.springframework.util.StringUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.Normalizer;
import java.text.ParseException;
import java.util.*;
import java.util.regex.Pattern;

public class PaymentFacade extends BaseFacade {
    protected TransactionService transactionService;
    protected EsApiLogService esApiLogService;
    protected MerchantGatewaysettingService merchantGatewaysettingService;
    protected Gson gson;

    public PaymentFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        this.transactionService = transactionService;
        this.esApiLogService = esApiLogService;
        this.merchantGatewaysettingService = merchantGatewaysettingService;
        this.gson = gson;
    }

    protected Transaction getTran(String txnRef) throws TransactionIsNotExistException {
        Optional<Transaction> optTran = transactionService.getByTxnRef(txnRef);
        if (!optTran.isPresent()) {
            throw new TransactionIsNotExistException();
        }
        return optTran.get();
    }

    protected void saveTran(Transaction tran, String message, String responseCode, String responseMessage, String tranNo, String tranDate, Date now) {
        tran.setStatus(Transaction.STATUS_GATEWAY_RESPONDED);
        tran.setNotes(message);
        tran.setResponseCode(responseCode);
        tran.setResponseMessage(responseMessage);
        tran.setTransactionNo(tranNo);
        tran.setTransactionDate(tranDate);
        tran.setUpdatedDate(now);
        transactionService.save(tran);
    }

    protected List<MerchantGatewaysetting> getParamMegaPay(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef, Date now, String notiUrl) {

        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param currency (VND)
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.CURRENCY, MegaPayConstant.CURRENCY_VALUE));

        // add param amount  10,000 <= amount < 2,147,483,647
        // amount = goodsAmount + userFee
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.AMOUNT, req.getAmount()));

        // add param invoiceNo
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.INVOICE_NOTE, req.getOrderInfo()));

        // add param goodsNm
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.GOODS_NM, MegaPayConstant.GOODS_NM_VALUE));

        // add param payType (default = NO)
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.PAY_TYPE, MegaPayConstant.PAY_TYPE_VALUE));

        // add param callBackUrl
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.CALL_BACK_URL, req.getReturnUrl()));

        // todo add param note url khi viet xong api udpate meagepay
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.NOTI_URL, notiUrl));

        // todo add param reqDomain
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.REQ_DOMAIN, getDomain(req.getReturnUrl())));

        // add param vat
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.VAT, "0"));

        // add param fee
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.FEE, "0"));

        // add param noTax
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.NOTAX, "0"));

        // add param description
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.DESCRIPTON, StringUtils.hasText(req.getDescription().trim()) ? deAccent(req.getDescription().trim()) : "Payment by megapay"));

        // add param userIP
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.USER_IP, req.getIpAddress()));

        // add param userLanguage
        if (req.getLang().equalsIgnoreCase(PaymentConstant.VI)) {
            lstResult.add(setMerchantGatewaysetting(MegaPayConstant.USER_LANGUAGE, MegaPayConstant.VN));
        } else {
            lstResult.add(setMerchantGatewaysetting(MegaPayConstant.USER_LANGUAGE, MegaPayConstant.EN));
        }

        // add param timeStamp
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.TIMESTAMP, DateUtils.formatDateYYYYMMDDHHMMSS(now)));

        // add param merTrxId
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.MERTRX_ID, txnRef));

        // add param userFee
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.USER_FEE, "0"));

        // add param goodsAmount
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.GOODS_AMOUNT, req.getAmount()));

        // add param windowType  0 : Sử dụng máy tính, 1: Sử dụng điện thoại
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.WINDOW_TYPE, req.getWindowType() != null ? req
                .getWindowType() : MegaPayConstant.WINDOW_TYPE_VALUE));

        // add param windowColor
        lstResult.add(setMerchantGatewaysetting(MegaPayConstant.WINDOW_COLOR, req.getWindowColor() != null ? req.getWindowColor() : MegaPayConstant.WINDOW_COLOR_VALUE));
        return lstResult;

    }

    protected List<MerchantGatewaysetting> getParamOnepayDom(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef) {
        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param vpc_Version
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.VERSION, OnePayConstant.VERSION_VALUE));

        //add param vpc_Currency
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.CURRENCY, OnePayConstant.CURRENCY_VALUE));

        //add param vpc_Command
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.COMMAND, OnePayConstant.COMMAND_VALUE));

        //add param vpc_MerchTxnRef;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.TRANSACTION_REF, txnRef));

        //add param vpc_OrderInfo;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.INFO, req.getOrderInfo()));

        //add param vpc_Amount;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.ORDER_AMOUNT, req.getAmount() + PaymentConstant.AMOUNT_LAST_CHARACTER));

        //add param vpc_TicketNo;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.PAY_IP, req.getIpAddress()));

        //add param Title;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.TITLE, req.getDescription()));

        // add param vpc_locale
        if (PaymentConstant.VI.equals(req.getLang())) {
            lstResult.add(setMerchantGatewaysetting(OnePayConstant.LOCALE, OnePayConstant.LOCALE_VN));
        } else {
            lstResult.add(setMerchantGatewaysetting(OnePayConstant.LOCALE, OnePayConstant.LOCALE_EN));
        }

        //add param vpc_ReturnURL;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.RETURN_URL, req.getReturnUrl()));

        //add param AgainLink;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.AGAIN_LINK, req.getAgainLink()));

        return lstResult;
    }

    protected List<MerchantGatewaysetting> getParamOnepayInter(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef) {
        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param vpc_Version
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.VERSION, OnePayConstant.VERSION_VALUE));

        //add param vpc_Command
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.COMMAND, OnePayConstant.COMMAND_VALUE));

        //add param vpc_MerchTxnRef;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.TRANSACTION_REF, txnRef));

        //add param vpc_OrderInfo;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.INFO, req.getOrderInfo()));

        //add param vpc_Amount;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.ORDER_AMOUNT, req.getAmount() + PaymentConstant.AMOUNT_LAST_CHARACTER));

        //add param vpc_TicketNo;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.PAY_IP, req.getIpAddress()));

        //add param Title;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.TITLE, req.getDescription()));

        // add param vpc_locale
        if (PaymentConstant.VI.equals(req.getLang())) {
            lstResult.add(setMerchantGatewaysetting(OnePayConstant.LOCALE, OnePayConstant.LOCALE_VN));
        } else {
            lstResult.add(setMerchantGatewaysetting(OnePayConstant.LOCALE, OnePayConstant.LOCALE_EN));
        }

        //add param vpc_ReturnURL;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.RETURN_URL, req.getReturnUrl()));

        //add param AgainLink;
        lstResult.add(setMerchantGatewaysetting(OnePayConstant.AGAIN_LINK, req.getAgainLink()));

        return lstResult;
    }

    protected List<MerchantGatewaysetting> getParamVnpay(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef, Date now) {
        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param vnp_Version
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.VERSION, VNPayConstant.VERSION_VALUE));

        //add param vnp_Command
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.COMMAND, VNPayConstant.COMMAND_VALUE));

        //add param vnp_CurrCode
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.CURRCODE, VNPayConstant.CURRCODE_VALUE));

        //add param vnp_TxnRef;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.TRANSACTION_REF, txnRef));

        //add param vnp_OrderInfo;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.INFO, req.getOrderInfo()));

        //add param vnp_Amount;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.ORDER_AMOUNT, req.getAmount() + PaymentConstant.AMOUNT_LAST_CHARACTER));

        //add param vnp_IpAddr;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.IP_ADDR, req.getIpAddress()));

        //+07:00
        Calendar createDate = CalendarUtil.getCalenderByTimeZone(ZoneUtil.ZONE_HO_CHI_MINH);
        //add param vnp_CreateDate
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.CREATE_DATE, CalendarUtil.formatCalendaryyyyMMddHHmmss(createDate)));

        // add param vnp_Locale
        if (req.getLang().equals(PaymentConstant.VI)) {
            lstResult.add(setMerchantGatewaysetting(VNPayConstant.LOCALE, VNPayConstant.LOCALE_VN));
        } else {
            lstResult.add(setMerchantGatewaysetting(VNPayConstant.LOCALE, VNPayConstant.LOCALE_EN));
        }

        //add param vnp_OrderType;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.ORDER_TYPE, VNPayConstant.ORDER_TYPE_VALUE));

        //add param vnp_ReturnURL;
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.RETURN_URL, req.getReturnUrl()));

        //add param vnp_ExpireDate
        createDate.add(Calendar.MINUTE, 15);
        lstResult.add(setMerchantGatewaysetting(VNPayConstant.VNP_EXPIREDATE, CalendarUtil.formatCalendaryyyyMMddHHmmss(createDate)));

        return lstResult;
    }

    protected List<MerchantGatewaysetting> getParamViettelPay(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef) {
        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param billcode
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.ORDER_ID, txnRef));

        //add param command
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.COMMAND, ViettelPayConstant.COMMAND_VALUE));

        //add param locale
        if (req.getLang().equals(PaymentConstant.VI)) {
            lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.LOCALE, ViettelPayConstant.LOCALE_VI));
        } else {
            lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.LOCALE, ViettelPayConstant.LOCALE_EN));
        }

        //add param order_id;
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.BILLCODE, req.getOrderInfo()));

        //add param trans_amount;
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.TRANS_AMOUNT, req.getAmount()));

        //add param version;
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.VERSION, ViettelPayConstant.VERSION_VALUE));

        //add param return_url;
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.RETURN_URL, req.getReturnUrl()));

        //add param cancel_url;
        lstResult.add(setMerchantGatewaysetting(ViettelPayConstant.CANCEL_URL, req.getCancelUrl()));

        return lstResult;
    }

    protected MerchantGatewaysetting setMerchantGatewaysetting(String parameter, String value) {
        MerchantGatewaysetting param = new MerchantGatewaysetting();
        param.setParameter(parameter);
        param.setValue(value);
        param.setType(MerchantGatewaysetting.FIXED_PARAM);
        return param;
    }

    protected MerchantGatewaysetting setMerchantGatewaysetting(String parameter, String value, int type) {
        MerchantGatewaysetting param = new MerchantGatewaysetting();
        param.setParameter(parameter);
        param.setValue(value);
        param.setType(type);
        return param;
    }

    protected String getValueParam(List<MerchantGatewaysetting> params, String key) {
        for (MerchantGatewaysetting param : params) {
            if (key.equals(param.getParameter().trim())) {
                return param.getValue();
            }
        }
        return null;
    }

    protected Transaction createTran(CreateRequest req, MerchantGateway mg, MerchantProject mp, Date now, String txnRef) {
        Transaction tran = new Transaction();
        tran.setAmount(Double.parseDouble(req.getAmount()));
        tran.setCreatedDate(now);
        tran.setUpdatedDate(now);
        tran.setGateway(mg.getGateway());
        tran.setMerchantGateway(mg);
        tran.setMerchantProject(mp);
        tran.setOrderInfo(req.getOrderInfo());
        tran.setTxnRef(txnRef);
        tran.setStatus(Transaction.STATUS_CREATE_NEW);
        tran.setDescription(req.getDescription());
        tran.setExpDate(now);
        tran.setIpAddress(req.getIpAddress());
        return tran;
    }

    protected List<MerchantGatewaysetting> getParamQrcodeVnpay(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef) throws ParseException {
        List<MerchantGatewaysetting> lstResult = new ArrayList<>();
        //add param configured
        lstResult.addAll(params);

        //add param serviceCode
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.SERVICE_CODE, QRCodeVNPayConstant.SERVICE_CODE_VALUE));

        //add param countryCode
        if (req.getLang().equals(PaymentConstant.VI)) {
            lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.COUNTRY_CODE, QRCodeVNPayConstant.COUNTRY_CODE_VN));
        } else {
            lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.COUNTRY_CODE, QRCodeVNPayConstant.COUNTRY_CODE_US));
        }
        //add param ccy
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.CCY, QRCodeVNPayConstant.CCY_VALUE));

        //add param masterMerCode;
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.MASTER_MER_CODE, QRCodeVNPayConstant.MASTER_MER_CODE_VALUE));

        //add param productId
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.PAY_TYPE, QRCodeVNPayConstant.PAY_TYPE_VALUE_ONLINE));

        //add param merchantType;
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.MERCHANT_TYPE, null));

//        //add param tipAndFee;
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.TIP_AND_FEE, null));
//
//        //add param consumerID;
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.CONSUMER_ID, null));
//
//        //add param purpose;
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.PURPOSE, null));
//
//        //add param desc
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.DESC, null));
//
        //add param billNumber
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.BILL_NUMBER, txnRef));

        //add param productId
//        lstResult.add(setMerchantGatewaysetting(QrCodeVNPayConstant.PRODUCT_ID, ""));

        //add param txnId;
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.TXN_ID, txnRef));

        //add param consumerID;
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.AMOUNT, req.getAmount()));

        //add param expDate;
        lstResult.add(setMerchantGatewaysetting(QRCodeVNPayConstant.EXP_DATE, DateUtils.formatDateYYMMDDHHMM(req.getExpDate())));

        return lstResult;
    }

    protected String createQrcodeViettelPay(CreateRequest req, List<MerchantGatewaysetting> params, String txnRef) {
        StringBuffer sb = new StringBuffer();
        sb.append("{");

        sb.append(createParam(QRCodeViettelPayConstant.MERCHANT_CODE, getValueParam(params, ViettelPayConstant.MERCHANT_CODE)));
        sb.append(createParam(QRCodeViettelPayConstant.VERSION, QRCodeViettelPayConstant.VERSION_VALUE));
        sb.append(createParam(QRCodeViettelPayConstant.TYPE, QRCodeViettelPayConstant.TYPE_VALUE));
        sb.append(createParam(QRCodeViettelPayConstant.ORDER_ID, txnRef));
        sb.append(createParam(QRCodeViettelPayConstant.BILLCODE, req.getOrderInfo()));
        sb.append(createParam(QRCodeViettelPayConstant.AMOUNT, req.getAmount()));

        sb.append("\"");
        sb.append(QRCodeViettelPayConstant.PRIORITY);
        sb.append("\":\"");
        sb.append(QRCodeViettelPayConstant.PRIORITY_VALUE);
        sb.append("\"");

        sb.append("}");
        return sb.toString();
    }

    protected String createParam(String key, String value) {
        StringBuffer sb = new StringBuffer();
        sb.append("\"");
        sb.append(key);
        sb.append("\":\"");
        sb.append(value);
        sb.append("\",");
        return sb.toString();
    }

    protected Map<String, String> getFields(UpdateQrcodeVnpayRequest req) {
        Map<String, String> result = new HashMap<String, String>();
        result.put(QRCodeVNPayConstant.CODE, req.getCode());
        result.put(QRCodeVNPayConstant.MSG_TYPE, req.getMsgType());
        result.put(QRCodeVNPayConstant.TXN_ID, req.getTxnId());
        result.put(QRCodeVNPayConstant.QR_TRACE, req.getQrTrace());
        result.put(QRCodeVNPayConstant.BANK_CODE, req.getBankCode());
        result.put(QRCodeVNPayConstant.MOBILE, req.getMobile());
        result.put(QRCodeVNPayConstant.ACCOUNT_NO, req.getAccountNo());
        result.put(QRCodeVNPayConstant.AMOUNT, req.getAmount());
        result.put(QRCodeVNPayConstant.PAY_DATE, req.getPayDate());
        result.put(QRCodeVNPayConstant.MERCHANT_CODE, req.getMerchantCode());
        result.put(QRCodeVNPayConstant.CHECKSUM, req.getChecksum());
        return result;
    }

    protected static String deAccent(String str) {
        String nfdNormalizedString = Normalizer.normalize(str, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    protected static String getDomain(String url) {
        try {
            URL aURL = new URL(url);
            return aURL.getHost();
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return url;
        }
    }
}
