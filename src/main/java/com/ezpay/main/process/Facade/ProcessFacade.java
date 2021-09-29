package com.ezpay.main.process.Facade;

import com.ezpay.core.entity.*;
import com.ezpay.core.facade.BaseFacade;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.QRCode;
import com.ezpay.core.gateway.constant.*;
import com.ezpay.core.model.Res;
import com.ezpay.core.utils.DateUtils;
import com.ezpay.core.utils.StringQueryUtils;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.process.entity.model.MegapayDataModel;
import com.ezpay.main.process.entity.res.MegapayRes;
import com.ezpay.main.process.model.req.QueryQRCodeVnpayRequest;
import com.ezpay.main.process.model.req.UpdateTransactionRequest;
import com.ezpay.main.process.model.res.TransactionViettelpayResponse;
import com.ezpay.main.process.utils.ProcessConstant;
import com.ezpay.main.process.utils.ProcessHash;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class ProcessFacade extends BaseFacade {
    @Value("${app.process.max-count-updates}")
    private int maxCountUpdates;
    @Value("${app.process.max-count-query}")
    private int maxCountQuery;

    @Value("${app.process.fixedRate.update.transaction}")
    private int intervalUpdateTransaction;
    @Value("${app.process.fixedRate.query.transaction}")
    private int intervalQueryTransaction;

    //url cong thanh toan
    @Value("${payment.query.onepay-dom}")
    private String onepayDomUrl;
    @Value("${payment.query.onepay-inter}")
    private String onepayInterUrl;
    @Value("${payment.query.vnpay}")
    private String vnpayUrl;
    @Value("${payment.query.viettelpay}")
    private String viettelPayUrl;
    @Value("${payment.query.vnpay-qrcode}")
    private String qrcodeVnpayUrl;
    @Value("${payment.query.megapay}")
    private String megapayRul;

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private Gson gson;
    @Autowired
    private EsApiLogService esApiLogService;

    @Autowired
    @Qualifier("onePay")
    private Payment onePay;
    @Autowired
    @Qualifier("vnPay")
    private Payment vnPay;
    @Autowired
    @Qualifier("viettelPay")
    private Payment viettelPay;

    @Autowired
    @Qualifier("qrCodeVNPay")
    private QRCode qrcodeVnpay;

    @Autowired
    @Qualifier("megaPay")
    private Payment megaPay;

    @Transactional
    public void updateTransaction() {
        try {
            LOGGER.info("Start: " + System.currentTimeMillis());
            Date now = new Date();

            // update transaction not updated
//            List<Transaction> lstTranRes = transactionService.getByStatus();
//            if (lstTranRes.isEmpty()) {
//                LOGGER.info("List transaction paid is empty");
//            } else {
//                LOGGER.info("Transaction paid size: " + lstTranRes.size());
//                for (Transaction tran : lstTranRes) {
//                    LOGGER.info("Transaction order info is : " + tran.getOrderInfo());
//                    if (DateUtils.getDifferenceMilisecond(tran.getUpdatedDate(), now) < intervalUpdateTransaction) break;
//                    tran.setCountUpdates(tran.getCountUpdates() + ProcessConstant.INCR1_COUNT);
//                    if (callIpn(tran)) {
//                        LOGGER.info("Call ipn 1 true");
//                        tran.setStatus(Transaction.STATUS_PROJECT_UPDATED);
//                    } else {
//                        LOGGER.info("Call ipn 1 false");
//                        tran.setStatus(Transaction.STATUS_PROJECT_UPDATE_FAIL);
//                    }
//                    LOGGER.info("Transaction save 1 " + tran.getOrderInfo());
//                    transactionService.save(tran);
//                }
//            }

            // update transaction not updated
            List<Transaction> lstTranRes = transactionService.getTransactionNotUpdatedToProject(maxCountUpdates + ProcessConstant.INCR1_COUNT);
            if (lstTranRes.isEmpty()) {
                LOGGER.info("List transaction update is empty");
            } else {
                LOGGER.info("Transaction update size is: " + lstTranRes.size());
                for (Transaction tran : lstTranRes) {
                    LOGGER.info("Transaction order info is: " + tran.getOrderInfo());
                    if (DateUtils.getDifferenceMilisecond(tran.getUpdatedDate(), now) < intervalUpdateTransaction) {
                        LOGGER.info("Transaction time unsatisfied");
                        continue;
                    }
                    if (tran.getCountUpdates() <= maxCountUpdates) {
                        tran.setCountUpdates(tran.getCountUpdates() + ProcessConstant.INCR1_COUNT);
                        if (callIpn(tran)) {
                            LOGGER.info("Call ipn true");
                            tran.setStatus(Transaction.STATUS_PROJECT_UPDATED);
                        } else {
                            LOGGER.info("Call ipn false");
                            tran.setStatus(Transaction.STATUS_PROJECT_UPDATE_FAIL);
                        }
                        LOGGER.info("Transaction save 1: " + tran.getOrderInfo());
                        transactionService.save(tran);
                    } else {
                        LOGGER.info("Transaction update over");
                        tran.setCountUpdates(tran.getCountUpdates() + ProcessConstant.INCR1_COUNT);
                        sendMailUpdate(tran);
                        LOGGER.info("Transaction save 2: " + tran.getOrderInfo());
                        transactionService.save(tran);
                    }
                }
            }
            LOGGER.info("End: " + System.currentTimeMillis());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    @Transactional
    public void queryTransaction() {
        try {
            LOGGER.info("Start: " + System.currentTimeMillis());
            Date now = new Date();
//            DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date from = DateUtils.increaseHour(now, -ProcessConstant.EXP_DATE_QUERY);
//            Date to = DateUtils.increaseSecond(now, -ProcessConstant.START_DATE_QUERY);
            List<Transaction> lstTranRes = transactionService.getTransactionNotUpdated(maxCountQuery + ProcessConstant.INCR1_COUNT);
            if (lstTranRes.isEmpty()) {
                LOGGER.info("List transaction query is empty");
            } else {
                LOGGER.info("Transaction query size is: " + lstTranRes.size());
                for (Transaction tran : lstTranRes) {
                    LOGGER.info("Transaction order info is: " + tran.getOrderInfo());
                    if (DateUtils.getDifferenceMilisecond(tran.getCreatedDate(), now) < intervalQueryTransaction) {
                        LOGGER.info("Transaction time unsatisfied");
                        continue;
                    }
                    if (tran.getCountQuery() <= maxCountQuery) {
                        Gateway gateway = tran.getGateway();
                        LOGGER.info("Gateway code is: " + gateway.getCode());
                        MerchantGateway mg = tran.getMerchantGateway();
                        switch (gateway.getCode()) {
                            case GatewayConstant.ONEPAY_DOM_CODE:
                                LOGGER.info("Update onepay dom: " + tran.getOrderInfo());
                                requestTransactionOnePay(onepayDomUrl, tran, mg, now);
                                break;
                            case GatewayConstant.ONEPAY_INTER_CODE:
                                LOGGER.info("Update onepay inter: " + tran.getOrderInfo());
                                requestTransactionOnePay(onepayInterUrl, tran, mg, now);
                                break;
                            case GatewayConstant.QR_VNPAY_CODE:
                                LOGGER.info("Update vnpay qrcode: " + tran.getOrderInfo());
                                requestTransactionQrcodeVnpay(qrcodeVnpayUrl, tran, mg, now);
                                break;
                            case GatewayConstant.VNPAY_CODE:
                                LOGGER.info("Update vnpay: " + tran.getOrderInfo());
                                requestTransactionVnpay(vnpayUrl, tran, mg, now);
                                break;
                            case GatewayConstant.VIETTEL_CODE:
                            case GatewayConstant.QR_VIETTEL_CODE:
                                LOGGER.info("Update viettelpay: " + tran.getOrderInfo());
                                requestTransactionViettelPay(viettelPayUrl, tran, mg, now);
                                break;
                            case GatewayConstant.MEGAPAY_CODE:
                                LOGGER.info("Update megapay: " + tran.getOrderInfo());
                                requestTransactionMegapay(megapayRul, tran, mg, now);
                                break;
                            default:
                                LOGGER.info("Gateway is not exist: " + gateway.getCode());
                                break;
                        }
                        // update trans
                    } else {
                        LOGGER.info("Transaction query over");
                        tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
                        sendMailQuery(tran);
                        LOGGER.info("Transaction save 3: ");
                        transactionService.save(tran);
                    }
                }
            }
            LOGGER.info("End: " + System.currentTimeMillis());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    //==================================================================================================================

    private void requestTransactionViettelPay(String url, Transaction tran, MerchantGateway mg, Date now) throws UnsupportedEncodingException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add(ViettelPayConstant.CMD, ViettelPayConstant.CMD_VALUE);
        params.add(ViettelPayConstant.MERCHANT_CODE, getParamByKey(mg.getParams(), ViettelPayConstant.MERCHANT_CODE));
        params.add(ViettelPayConstant.ORDER_ID, tran.getTxnRef());
        params.add(ViettelPayConstant.VERSION, ViettelPayConstant.VERSION_VALUE);

        Map<String, String> requestFields = convertMultiValueMapToMap(params);
        requestFields.put(ViettelPayConstant.ACCESS_CODE, getParamByKey(mg.getParams(), ViettelPayConstant.ACCESS_CODE));

        String checkSumRequestFields = viettelPay.hashKey(requestFields, getParamByKey(mg.getParams(), ViettelPayConstant.SECRET_KEY));
        params.add(ViettelPayConstant.CHECK_SUM, checkSumRequestFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        TransactionViettelpayResponse tranVR = gson.fromJson(response.getBody(), TransactionViettelpayResponse.class);

        Map<String, String> responseFields = new HashMap<String, String>() {
            {
                put(ViettelPayConstant.ACCESS_CODE, getParamByKey(mg.getParams(), ViettelPayConstant.ACCESS_CODE));
                put(ViettelPayConstant.MERCHANT_CODE, StringUtils.hasText(tranVR.getMerchant_code()) ? URLDecoder.decode(tranVR.getMerchant_code(), StandardCharsets.UTF_8.name()) : "");
                put(ViettelPayConstant.ORDER_ID, StringUtils.hasText(tranVR.getOrder_id()) ? URLDecoder.decode(tranVR.getOrder_id(), StandardCharsets.UTF_8.name()) : "");
                put(ViettelPayConstant.PAYMENT_STATUS, StringUtils.hasText(tranVR.getPayment_status()) ? URLDecoder.decode(tranVR.getPayment_status(), StandardCharsets.UTF_8.name()) : "");
                put(ViettelPayConstant.VERSION, StringUtils.hasText(tranVR.getVersion()) ? URLDecoder.decode(tranVR.getVersion(), StandardCharsets.UTF_8.name()) : "");
            }
        };
        String checkSumResponseFields = viettelPay.hashKey(responseFields, getParamByKey(mg.getParams(), ViettelPayConstant.SECRET_KEY));
        if (checkSumResponseFields.equalsIgnoreCase(StringUtils.hasText(tranVR.getCheck_sum()) ? URLDecoder.decode(tranVR.getCheck_sum(), StandardCharsets.UTF_8.name()) : "")) {
            saveTran(tran,
                    tranVR.getPayment_status(),
                    tranVR.getError_code(),
                    viettelPay.getResponseDescription(tranVR.getError_code()),
                    tranVR.getVt_transaction_id(),
                    DateUtils.formatDateYYYYMMDDHHMMSS(now),
                    now);
        } else {
            tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
            transactionService.save(tran);
        }

        createLog(tran, gson.toJson(params), response.getBody(), url);
    }

    private void requestTransactionVnpay(String url, Transaction tran, MerchantGateway mg, Date now) throws UnsupportedEncodingException, UnknownHostException {
        List<MerchantGatewaysetting> params = new ArrayList<>();
        params.add(setMerchantGatewaysetting(VNPayConstant.VERSION, VNPayConstant.VERSION_VALUE, MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.COMMAND, VNPayConstant.COMMAND_QERY_VALUE, MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.TMN_CODE, getParamByKey(mg.getParams(), VNPayConstant.TMN_CODE), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.TRANSACTION_REF, tran.getTxnRef(), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.INFO, tran.getOrderInfo(), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.TRANSDATE, DateUtils.formatDateYYYYMMDDHHMMSS(tran.getCreatedDate()), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.IP_ADDR, InetAddress.getLocalHost().getHostAddress(), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.CREATE_DATE, DateUtils.formatDateYYYYMMDDHHMMSS(now), MerchantGatewaysetting.FIXED_PARAM));
        params.add(setMerchantGatewaysetting(VNPayConstant.SECRET_KEY, getParamByKey(mg.getParams(), VNPayConstant.SECRET_KEY), MerchantGatewaysetting.KEY_PARAM));
        String link = vnPay.getPaymentLink(params, url);
        ResponseEntity<String> response = restTemplate.getForEntity(link, String.class);

        Map<String, String> responseFields = StringQueryUtils.getFields(response.getBody());
        if (vnPay.checkFields(responseFields, getParamByKey(mg.getParams(), VNPayConstant.SECRET_KEY))) {
            // check amount
            String amount = responseFields.get(VNPayConstant.ORDER_AMOUNT) == null ? "000" : responseFields.get(VNPayConstant.ORDER_AMOUNT);
            if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount.substring(0, amount.length() - 2)))) {
                responseFields.put(VNPayConstant.RESPONSE, "-1");
                responseFields.put(VNPayConstant.MESSAGE, "Số tiền không đúng");
            }
            saveTran(tran,
                    responseFields.get(VNPayConstant.MESSAGE),
                    responseFields.get(VNPayConstant.RESPONSE),
                    vnPay.getResponseDescription(responseFields.get(VNPayConstant.RESPONSE)),
                    responseFields.get(VNPayConstant.TRANSACTION_NO),
                    DateUtils.formatDateYYYYMMDDHHMMSS(now),
                    now);
        } else {
            tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
            transactionService.save(tran);
        }

        createLog(tran, gson.toJson(params), response.getBody(), url);
    }

    private void requestTransactionQrcodeVnpay(String url, Transaction tran, MerchantGateway mg, Date now) {
        QueryQRCodeVnpayRequest queryRequest = new QueryQRCodeVnpayRequest();
        queryRequest.setTxnId(tran.getTxnRef());
        queryRequest.setTerminalID(getParamByKey(mg.getParams(), QRCodeVNPayConstant.TERMINAL_ID));
        queryRequest.setMerchantCode(getParamByKey(mg.getParams(), QRCodeVNPayConstant.MERCHANT_CODE));
        queryRequest.setPayDate(DateUtils.parseDateToString(tran.getCreatedDate(), "dd/MM/yyyy"));

        LOGGER.info("Vnpay qrcode request query: " + gson.toJson(queryRequest));

        Map<String, String> requestFields = convertObjectToMap(queryRequest);
        String checkSumRequestFields = qrcodeVnpay.hashKeyQueryTransactionRequest(requestFields, getParamByKey(mg.getParams(), QRCodeVNPayConstant.SECRET_KEY_QUERY));
        queryRequest.setCheckSum(checkSumRequestFields);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<QueryQRCodeVnpayRequest> request = new HttpEntity<QueryQRCodeVnpayRequest>(queryRequest, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        LOGGER.info("Vnpay qrcode response query: " + gson.toJson(response.getBody()));

        Map<String, String> responseFields = convertJsonToMap(response.getBody());
        String checkSumResponseFields = qrcodeVnpay.hashKeyQueryTransactionResponse(responseFields, getParamByKey(mg.getParams(), QRCodeVNPayConstant.SECRET_KEY_QUERY));

        if (checkSumResponseFields.equalsIgnoreCase(responseFields.get(QRCodeVNPayConstant.CHECKSSUM))) {
            //check amount
            String amount = responseFields.get(QRCodeVNPayConstant.DEBIT_AMOUNT);
            if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount))) {
                responseFields.put(QRCodeVNPayConstant.CODE, "-1");
                responseFields.put(QRCodeVNPayConstant.MESSAGE, "Số tiền không đúng");
            }
            saveTran(tran,
                    responseFields.get(QRCodeVNPayConstant.MESSAGE),
                    responseFields.get(QRCodeVNPayConstant.CODE),
                    qrcodeVnpay.getResponseQueryQRCodeDescription(responseFields.get(QRCodeVNPayConstant.CODE)),
                    "",
                    DateUtils.formatDateYYYYMMDDHHMMSS(now),
                    now);
        } else {
            tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
            transactionService.save(tran);
        }

        createLog(tran, gson.toJson(queryRequest), response.getBody(), url);
    }

    private void requestTransactionMegapay(String url, Transaction tran, MerchantGateway mg, Date now) throws UnsupportedEncodingException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();

        String merId = getParamByKey(mg.getParams(), MegaPayConstant.MERID);
        String encodeKey = getParamByKey(mg.getParams(), MegaPayConstant.ENCODE_KEY);
        String time = DateUtils.formatDateYYYYMMDDHHMMSS(now);
        String merchantToken = time + tran.getTxnRef() + merId + encodeKey;

        params.add(MegaPayConstant.MERID, merId);
        params.add(MegaPayConstant.MERTRX_ID, tran.getTxnRef());
        params.add(MegaPayConstant.TIMESTAMP, time);
        params.add(MegaPayConstant.MERCHANT_TOKEN, ProcessHash.encryptSha256(merchantToken));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);

        MegapayRes megapayRes = gson.fromJson(response.getBody(), MegapayRes.class);
        if (megapayRes.getResultCd().equalsIgnoreCase(MegaPayConstant.RESULT_CD_SUCCESS)) {
            MegapayDataModel data = megapayRes.getData();

            String tranNo = data.getTrxId() == null ? "" : data.getTrxId();
            String responseCode = data.getResultCd();
            String message = data.getResultMsg();

            Map<String, String> responseFields = new HashMap<>();
            responseFields.put(MegaPayConstant.RESULT_CD, data.getResultCd());
            responseFields.put(MegaPayConstant.TIMESTAMP, data.getTimeStamp());
            responseFields.put(MegaPayConstant.MERTRX_ID, data.getMerTrxId());
            responseFields.put(MegaPayConstant.TRX_ID, data.getTrxId());
            responseFields.put(MegaPayConstant.MERID, data.getMerId());
            responseFields.put(MegaPayConstant.AMOUNT, data.getAmount());
            responseFields.put(MegaPayConstant.MERCHANT_TOKEN, data.getMerchantToken());
            // check merchant token
            if (megaPay.checkFields(responseFields, getParamByKey(mg.getParams(), MegaPayConstant.ENCODE_KEY))) {
                //check amount
                if (StringUtils.hasText(data.getAmount())) {
                    String amount = data.getAmount();
                    if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount))) {
                        responseCode = "-1";
                        message = "Số tiền không đúng";
                    }
                }
                saveTran(tran,
                        message,
                        responseCode,
                        megaPay.getResponseDescription(responseCode),
                        tranNo,
                        DateUtils.formatDateYYYYMMDDHHMMSS(now),
                        now);

                createLog(tran, gson.toJson(params), response.getBody(), url);
            }else {
                tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
                transactionService.save(tran);
            }
        } else {
            saveTran(tran,
                    megapayRes.getResultMsg(),
                    megapayRes.getResultCd(),
                    megaPay.getResponseDescription(megapayRes.getResultCd()),
                    "",
                    DateUtils.formatDateYYYYMMDDHHMMSS(now),
                    now);

            createLog(tran, gson.toJson(params), response.getBody(), url);
        }

    }

    private void requestTransactionOnePay(String url, Transaction tran, MerchantGateway mg, Date now) throws UnsupportedEncodingException {
        MultiValueMap<String, String> params = new LinkedMultiValueMap<String, String>();
        params.add(OnePayConstant.COMMAND, OnePayConstant.COMMAND_VALUE_QUERY);
        params.add(OnePayConstant.VERSION, OnePayConstant.VERSION_VALUE_QUERY);
        params.add(OnePayConstant.TRANSACTION_REF, tran.getTxnRef());
        params.add(OnePayConstant.MERCHANT, getParamByKey(mg.getParams(), OnePayConstant.MERCHANT));
        params.add(OnePayConstant.ACCESSCODE, getParamByKey(mg.getParams(), OnePayConstant.ACCESSCODE));
        params.add(OnePayConstant.USER, getParamByKey(mg.getParams(), OnePayConstant.USER));
        params.add(OnePayConstant.PASSWORD, getParamByKey(mg.getParams(), OnePayConstant.PASSWORD));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(params, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        Map<String, String> responseFields = StringQueryUtils.getFields(response.getBody());

        //check amount
        String amount = responseFields.get(OnePayConstant.ORDER_AMOUNT) == null ? "000" : responseFields.get(OnePayConstant.ORDER_AMOUNT);
        if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount.substring(0, amount.length() - 2)))) {
            responseFields.put(OnePayConstant.RESPONSE, "-1");
            responseFields.put(OnePayConstant.MESSAGE, "Số tiền không đúng");
        }
        saveTran(tran,
                responseFields.get(OnePayConstant.MESSAGE),
                responseFields.get(OnePayConstant.RESPONSE),
                onePay.getResponseDescription(responseFields.get(OnePayConstant.RESPONSE)),
                responseFields.get(OnePayConstant.TRANSACTION_NO),
                DateUtils.formatDateYYYYMMDDHHMMSS(now),
                now);

        createLog(tran, gson.toJson(params), response.getBody(), url);
    }

    private boolean callIpn(Transaction tran) {
        try {
            MerchantProject mp = tran.getMerchantProject();
            Gateway g = tran.getGateway();
            UpdateTransactionRequest request = getRequestParam(tran, mp, g);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            String strRequest = gson.toJson(request);
            HttpEntity<UpdateTransactionRequest> httpEntity = new HttpEntity<UpdateTransactionRequest>(request, headers);
            ResponseEntity<String> result = restTemplate.postForEntity(mp.getIpnLink(), httpEntity, String.class);
            createLog(tran, strRequest, result.getBody(), mp.getIpnLink());
            Res res = gson.fromJson(result.getBody(), Res.class);
            LOGGER.info("Transaction order info is: " + tran.getOrderInfo());
            LOGGER.info("Response is: " + res.getCode() + " " + res.getCode().equals(ProcessConstant.SUCCESS_0));
            if (res.getCode().equals(ProcessConstant.SUCCESS_0)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return false;
        }
    }

    private UpdateTransactionRequest getRequestParam(Transaction tran, MerchantProject mp, Gateway g) {
        UpdateTransactionRequest result = new UpdateTransactionRequest();
        result.setAmount(tran.getAmount());
        result.setCode(fomartTransactionCode(tran.getResponseCode()));
        result.setCurrencyCode(ProcessConstant.CURRENCY_CODE_VALUE);
        result.setGatewayCode(g.getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(tran.getTransactionDate());
        result.setMessage(tran.getResponseMessage());
        result.setOrderInfo(tran.getOrderInfo());
        result.setTransactionNo(tran.getTransactionNo());
        result.setTxnRef(tran.getTxnRef());
        return result;
    }

    private void createLog(Transaction tran, String req, String res, String url) {
        EsApiLog log = new EsApiLog();
        MerchantProject mp = tran.getMerchantProject();
        //all log
        log.setMethod(HttpMethod.POST.name());
        log.setUrl(url);
        log.setCreateDate(new Date());
        log.setType(EsApiLog.OUT);
        log.setMerchantName(mp.getMerchant().getName());
        log.setProjectId(mp.getProjectId());
        log.setMerchantId(String.valueOf(mp.getMerchant().getId()));
        log.setProjectCode(mp.getProject().getCode());
        log.setResponse(res);
        log.setRequest(req);
        log.setTxnRef(tran.getTxnRef());
        log.setGatewayCode(tran.getGateway().getCode());
        esApiLogService.save(log);
    }

    private String fomartTransactionCode(String responseCode) {
        if (ProcessConstant.SUCCESS_00.equals(responseCode)) {
            return ProcessConstant.SUCCESS_0;
        }
        return responseCode;
    }

    private String getParamByKey(List<MerchantGatewaysetting> params, String key) {
        for (MerchantGatewaysetting param : params) {
            if (key.equalsIgnoreCase(param.getParameter().trim())) {
                return param.getValue();
            }
        }
        return null;
    }

    private void saveTran(Transaction tran, String message, String responseCode, String responseMessage, String tranNo, String tranDate, Date now) {
        tran.setStatus(Transaction.STATUS_GATEWAY_RESPONDED);
        tran.setNotes(message);
        tran.setResponseCode(responseCode);
        tran.setResponseMessage(responseMessage);
        tran.setTransactionNo(tranNo);
        tran.setTransactionDate(tranDate);
        tran.setUpdatedDate(now);
        LOGGER.info("Update count query 4");
        tran.setCountQuery(tran.getCountQuery() + ProcessConstant.INCR1_COUNT);
        transactionService.save(tran);
    }

    private void updateProjectTran(Transaction tran) {
        for (int i = 1; i <= maxCountUpdates; i++) {
            tran.setCountUpdates(i);
            if (callIpn(tran)) {
                LOGGER.info("Update project tran 1 ");
                tran.setStatus(Transaction.STATUS_PROJECT_UPDATED);
                LOGGER.info("Transaction save ok " + tran.getOrderInfo());
                transactionService.save(tran);
                return;
            } else {
                LOGGER.info("Update project tran 2");
                tran.setStatus(Transaction.STATUS_PROJECT_UPDATE_FAIL);
            }
        }
        LOGGER.info("Transaction save fail " + tran.getOrderInfo());
        transactionService.save(tran);
        sendMailUpdate(tran);
    }

    private MerchantGatewaysetting setMerchantGatewaysetting(String parameter, String value, int type) {
        MerchantGatewaysetting param = new MerchantGatewaysetting();
        param.setParameter(parameter);
        param.setValue(value);
        param.setType(type);
        return param;
    }

    private void sendMailUpdate(Transaction tran) {

    }

    private void sendMailQuery(Transaction tran) {

    }

    private Map<String, String> convertMultiValueMapToMap(MultiValueMap<String, String> queryParameters) {
        Map<String, String> fields = new HashMap<String, String>();
        List fieldNames = new ArrayList(queryParameters.keySet());
        Iterator itr = fieldNames.iterator();
        StringBuffer buf = new StringBuffer();
        while (itr.hasNext()) {
            String fieldName = (String) itr.next();
            String fieldValue = (String) queryParameters.getFirst(fieldName);
            fields.put(fieldName, fieldValue);
        }
        return fields;
    }

    private Map<String, String> convertObjectToMap(Object object) {
        ObjectMapper oMapper = new ObjectMapper();
        return oMapper.convertValue(object, Map.class);
    }

    private Map<String, String> convertJsonToMap(String json) {
        ObjectMapper oMapper = new ObjectMapper();
        try {
            return oMapper.readValue(json, Map.class);
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<String, String>();
        }
    }
}
