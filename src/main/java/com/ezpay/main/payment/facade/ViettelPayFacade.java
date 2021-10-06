package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.entity.Transaction;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.GatewayConstant;
import com.ezpay.core.gateway.constant.ViettelPayConstant;
import com.ezpay.core.utils.DateUtils;
import com.ezpay.main.payment.exception.TransactionIsNotExistException;
import com.ezpay.main.payment.model.res.QueryViettelPayReponse;
import com.ezpay.main.payment.model.res.UpdateViettelPayResponse;
import com.ezpay.main.payment.model.res.VerifyViettelPayReponse;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.payment.utils.PaymentCode;
import com.ezpay.main.payment.utils.PaymentKey;
import com.ezpay.main.payment.utils.PaymentPath;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class ViettelPayFacade extends PaymentFacade {
    @Autowired
    @Qualifier("viettelPay")
    private Payment viettelPay;

    @Autowired
    public ViettelPayFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional
    public UpdateViettelPayResponse updateViettelpay(MultiValueMap<String, String> params, String ip) {
        UpdateViettelPayResponse res;
        EsApiLog log = new EsApiLog();
        try {
            Date now = new Date();
            LOGGER.info("verifyViettelpay params: " + params.toString());

            //luu log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.VIETTELPAY_UPDATE_API);
            log.setRequest(gson.toJson(params));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.VIETTEL_CODE);

            Map<String, String> fields = new HashMap<String, String>();

            List fieldNames = new ArrayList(params.keySet());
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) params.getFirst(fieldName);
                fields.put(URLDecoder.decode(fieldName, StandardCharsets.UTF_8.name()), URLDecoder.decode(fieldValue, StandardCharsets.UTF_8.name()));
            }

            //get transaction
            Transaction tran = getTran(fields.get(ViettelPayConstant.ORDER_ID));

            //ass log
            log.setTxnRef(tran.getTxnRef());

            //get key
            MerchantGateway mg = tran.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgs = optMcgs.get();
            fields.put(ViettelPayConstant.ACCESS_CODE, getValueParam(mg.getParams(), ViettelPayConstant.ACCESS_CODE));

            String check_sum;

            //update transaction
            if (viettelPay.checkFields(fields, mcgs.getValue())) {
                saveTran(tran,
                        fields.get(ViettelPayConstant.PAYMENT_STATUS),
                        fields.get(ViettelPayConstant.ERROR_CODE),
                        viettelPay.getResponseDescription(fields.get(ViettelPayConstant.ERROR_CODE)),
                        fields.get(ViettelPayConstant.VT_TRANSACTION_ID),
                        DateUtils.formatDateYYYYMMDDHHMMSS(now),
                        now);

                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.CODE_SUCCESS);
                check_sum = viettelPay.hashKey(fields, mcgs.getValue());
                LOGGER.info("verifyViettelpay success");
                res = new UpdateViettelPayResponse(PaymentCode.CODE_SUCCESS,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        tran.getOrderInfo(),
                        getValueParam(mg.getParams(), ViettelPayConstant.RETURN_URL),
                        tran.getTxnRef(),
                        PaymentKey.STRING_EMPTY,
                        check_sum);
            } else {
                LOGGER.info("verifyViettelpay fail check_sum");

                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.CODE_ERROR);
                check_sum = viettelPay.hashKey(fields, mcgs.getValue());
                res = new UpdateViettelPayResponse(PaymentCode.CODE_ERROR,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        tran.getOrderInfo(),
                        getValueParam(mg.getParams(), ViettelPayConstant.RETURN_URL),
                        tran.getTxnRef(),
                        PaymentKey.STRING_EMPTY,
                        check_sum);
            }

        } catch (TransactionIsNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateViettelPayResponse(PaymentCode.VNPAY_CODE_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateViettelPayResponse(PaymentCode.VNPAY_CODE_ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional(readOnly = true)
    public VerifyViettelPayReponse verifyViettelpay(MultiValueMap<String, String> params, String ip) {
        EsApiLog log = new EsApiLog();
        VerifyViettelPayReponse res;
        try {
            Date now = new Date();
            LOGGER.info("verifyViettelpay params: " + params.toString());

            //add log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.VIETTELPAY_VERIFY_API);
            log.setRequest(gson.toJson(params));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.VIETTEL_CODE);

            Map<String, String> fields = new HashMap<String, String>();
            List fieldNames = new ArrayList(params.keySet());
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) params.getFirst(fieldName);
                fields.put(URLDecoder.decode(fieldName, StandardCharsets.UTF_8.name()), URLDecoder.decode(fieldValue, StandardCharsets.UTF_8.name()));
            }

            //get transaction
            Transaction transaction = getTran(fields.get(ViettelPayConstant.ORDER_ID));

            //ass log
            log.setTxnRef(transaction.getTxnRef());

            //get key
            MerchantGateway mg = transaction.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgsKey = optMcgs.get();
            fields.put(ViettelPayConstant.ACCESS_CODE, getValueParam(mg.getParams(), ViettelPayConstant.ACCESS_CODE));
            fields.put(ViettelPayConstant.TRANS_AMOUNT, String.format("%.0f", transaction.getAmount()));

            String check_sum = URLDecoder.decode(viettelPay.hashKey(fields, mcgsKey.getValue()), StandardCharsets.UTF_8.name());

            LOGGER.info("check_sum request: " + fields.get(ViettelPayConstant.CHECK_SUM) + " | check_sum create: " + check_sum);
            if (check_sum.equals(fields.get(ViettelPayConstant.CHECK_SUM))) {
                LOGGER.info("verifyViettelpay success");
                //check_sum resul
                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.CODE_SUCCESS);
                check_sum = viettelPay.hashKey(fields, mcgsKey.getValue());
                res = new VerifyViettelPayReponse(fields.get(ViettelPayConstant.BILLCODE),
                        PaymentCode.CODE_SUCCESS,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        fields.get(ViettelPayConstant.ORDER_ID),
                        fields.get(ViettelPayConstant.TRANS_AMOUNT),
                        check_sum
                );
            } else {
                LOGGER.info("verifyViettelpay fail check_sum");

                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.WRONG_SIGNATURE_CODE);
                check_sum = viettelPay.hashKey(fields, mcgsKey.getValue());
                res = new VerifyViettelPayReponse(fields.get(ViettelPayConstant.BILLCODE),
                        PaymentCode.WRONG_SIGNATURE_CODE,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        fields.get(ViettelPayConstant.ORDER_ID),
                        fields.get(ViettelPayConstant.TRANS_AMOUNT),
                        check_sum);
            }

        } catch (TransactionIsNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            res = new VerifyViettelPayReponse(PaymentCode.CODE_ERROR);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new VerifyViettelPayReponse(PaymentCode.CODE_TRANSACTION_PAYMENTED);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional(readOnly = true)
    public QueryViettelPayReponse queryViettelpay(MultiValueMap<String, String> params, String ip) {
        EsApiLog log = new EsApiLog();
        QueryViettelPayReponse res;
        try {
            Date now = new Date();
            LOGGER.info("queryViettelpay params: " + params.toString());

            //luu log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.VIETTELPAY_QUERY_API);
            log.setRequest(gson.toJson(params));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.VIETTEL_CODE);

            Map<String, String> fields = new HashMap<String, String>();

            List fieldNames = new ArrayList(params.keySet());
            Iterator itr = fieldNames.iterator();
            StringBuffer buf = new StringBuffer();
            while (itr.hasNext()) {
                String fieldName = (String) itr.next();
                String fieldValue = (String) params.getFirst(fieldName);
                fields.put(URLDecoder.decode(fieldName, StandardCharsets.UTF_8.name()), URLDecoder.decode(fieldValue, StandardCharsets.UTF_8.name()));
            }

            //get transaction
            Transaction transaction = getTran(fields.get(ViettelPayConstant.ORDER_ID));

            //ass log
            log.setTxnRef(transaction.getTxnRef());

            //get key
            MerchantGateway mg = transaction.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgsKey = optMcgs.get();
            fields.put(ViettelPayConstant.ACCESS_CODE, getValueParam(mg.getParams(), ViettelPayConstant.ACCESS_CODE));

            String check_sum = URLDecoder.decode(viettelPay.hashKey(fields, mcgsKey.getValue()), StandardCharsets.UTF_8.name());

            if (check_sum.equals(fields.get(ViettelPayConstant.CHECK_SUM))) {
                LOGGER.info("verifyViettelpay success");

                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.CODE_SUCCESS);
                check_sum = viettelPay.hashKey(fields, mcgsKey.getValue());
                res = new QueryViettelPayReponse(PaymentCode.CODE_SUCCESS,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        transaction.getTxnRef(),
                        getValueParam(mg.getParams(), ViettelPayConstant.RETURN_URL),
                        transaction.getOrderInfo(),
                        PaymentKey.STRING_EMPTY,
                        check_sum);
            } else {
                LOGGER.info("verifyViettelpay fail check_sum");

                fields.put(ViettelPayConstant.ERROR_CODE, PaymentCode.CODE_ERROR);
                check_sum = viettelPay.hashKey(fields, mcgsKey.getValue());
                res = new QueryViettelPayReponse(PaymentCode.CODE_ERROR,
                        fields.get(ViettelPayConstant.MERCHANT_CODE),
                        transaction.getTxnRef(),
                        getValueParam(mg.getParams(), ViettelPayConstant.RETURN_URL),
                        transaction.getOrderInfo(),
                        PaymentKey.STRING_EMPTY,
                        check_sum);
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new QueryViettelPayReponse(PaymentCode.CODE_ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }


}
