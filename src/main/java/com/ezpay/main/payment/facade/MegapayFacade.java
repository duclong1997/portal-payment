package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.entity.Transaction;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.constant.GatewayConstant;
import com.ezpay.core.gateway.constant.MegaPayConstant;
import com.ezpay.core.utils.DateUtils;
import com.ezpay.main.payment.exception.MegapayUpdateException;
import com.ezpay.main.payment.model.req.UpdateMegaPayRequest;
import com.ezpay.main.payment.model.res.UpdateMegapayResponse;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.payment.utils.PaymentPath;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Optional;

@Service
public class MegapayFacade extends PaymentFacade {

    @Autowired
    @Qualifier("megaPay")
    private Payment megaPay;

    @Autowired
    public MegapayFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional
    public UpdateMegapayResponse updateMegapay(UpdateMegaPayRequest megaPayRequest, String ip) {
        EsApiLog log = new EsApiLog();
        UpdateMegapayResponse res;
        try {
            Date now = new Date();
            LOGGER.info("update megapay: " + megaPayRequest.toString());

            //add log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.MEGAPAY_UPDATE_API);
            log.setRequest(gson.toJson(megaPayRequest));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.MEGAPAY_CODE);

            //get transaction
            Transaction tran = getTran(megaPayRequest.getMerTrxId());

            //get encode key
            MerchantGateway mg = tran.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgs = optMcgs.get();

            //ass log
            log.setGatewayCode(mg.getGateway().getCode());
            log.setTxnRef(tran.getTxnRef());

            // parse gson to hashmap
            HashMap<String, String> fields = gson.fromJson(gson.toJson(megaPayRequest), HashMap.class);

            //update transaction
            if (megaPay.checkFields(fields, mcgs.getValue())) {

                // check amount
                String amount = fields.get(MegaPayConstant.AMOUNT);
                if (Math.round(tran.getAmount()) != Double.parseDouble(amount)) {
                    // Sai số tiền
                    throw new MegapayUpdateException("OR_134");
                }

                if (tran.getResponseCode() != null && tran.getTransactionNo() != null) {
                    // Giao dịch đã tồn tại. Xin hãy tạo giao dịch mới
                    throw new MegapayUpdateException("DC_103");
                }

                // check status
                if (!fields.get(MegaPayConstant.STATUS).equalsIgnoreCase(MegaPayConstant.STATUS_PAYMENT)) {
                    // Khách hàng hủy giao dịch
                    saveTran(tran,
                            fields.get(MegaPayConstant.RESULT_MSG),
                            "PG_ER5",
                            megaPay.getResponseDescription("PG_ER5"),
                            fields.get(MegaPayConstant.TRX_ID),
                            DateUtils.formatDateYYYYMMDDHHMMSS(now),
                            now);
                } else {
                    saveTran(tran,
                            fields.get(MegaPayConstant.RESULT_MSG),
                            fields.get(MegaPayConstant.RESULT_CD),
                            megaPay.getResponseDescription(fields.get(MegaPayConstant.RESULT_CD)),
                            fields.get(MegaPayConstant.TRX_ID),
                            DateUtils.formatDateYYYYMMDDHHMMSS(now),
                            now);
                }
                res = new UpdateMegapayResponse(megaPayRequest.getResultCd(), megaPayRequest.getResultMsg());

            } else {
                throw new MegapayUpdateException("DC_101");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateMegapayResponse(e.getMessage(), megaPay.getResponseDescription(e.getMessage()));
        }
        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }
}
