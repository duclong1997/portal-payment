package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.entity.Transaction;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.GatewayConstant;
import com.ezpay.core.gateway.constant.OnePayConstant;
import com.ezpay.core.utils.DateUtils;
import com.ezpay.main.payment.exception.TransactionAmoutInvalidException;
import com.ezpay.main.payment.exception.TransactionConfirmedException;
import com.ezpay.main.payment.exception.WrongSignatureException;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.payment.utils.PaymentKey;
import com.ezpay.main.payment.utils.PaymentPath;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Map;
import java.util.Optional;

@Service
public class OnepayFacade extends PaymentFacade {
    @Autowired
    @Qualifier("onePay")
    private Payment onePay;

    @Autowired
    public OnepayFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional
    public String updateOnepay(Map<String, String> fields, String ip) {
        EsApiLog log = new EsApiLog();
        String res;
        try {
            Date now = new Date();
            LOGGER.info("updateOnepay param: " + fields.toString());

            //add log
            log.setMethod(HttpMethod.GET.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.ONEPAY_UPDATE_API);
            log.setRequest(gson.toJson(fields));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.ONEPAY);

            //get transaction
            Transaction tran = getTran(fields.get(OnePayConstant.TRANSACTION_REF));

            //get secretKey
            MerchantGateway mg = tran.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgs = optMcgs.get();

            //ass log
            log.setGatewayCode(mg.getGateway().getCode());
            log.setTxnRef(tran.getTxnRef());

            //update transaction
            if (onePay.checkFields(fields, mcgs.getValue())) {

                // check amount
                String amount = fields.get(OnePayConstant.ORDER_AMOUNT);
                if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount.substring(0, amount.length() - 2)))) {
                    throw new TransactionAmoutInvalidException();
                }

                if (tran.getResponseCode() != null && tran.getTransactionNo() != null) {
                    throw new TransactionConfirmedException();
                }
                saveTran(tran,
                        fields.get(OnePayConstant.MESSAGE),
                        fields.get(OnePayConstant.RESPONSE),
                        onePay.getResponseDescription(fields.get(OnePayConstant.RESPONSE)),
                        fields.get(OnePayConstant.TRANSACTION_NO),
                        DateUtils.formatDateYYYYMMDDHHMMSS(now),
                        now);
                res = PaymentKey.UPDATE_ONEPAY_SUCCESS;
            } else {
                throw new WrongSignatureException();
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = PaymentKey.UPDATE_ONEPAY_ERROR;
        }

        log.setResponse(res);
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }
}
