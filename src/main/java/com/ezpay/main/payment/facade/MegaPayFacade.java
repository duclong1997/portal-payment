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
import com.ezpay.main.payment.model.res.UpdateMegaPayResponse;
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
public class MegaPayFacade extends PaymentFacade {

    @Autowired
    @Qualifier("megaPay")
    private Payment megaPay;

    @Autowired
    public MegaPayFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional
    public UpdateMegaPayResponse updateMegaPay(UpdateMegaPayRequest megaPayRequest, String ip) {
        EsApiLog log = new EsApiLog();
        UpdateMegaPayResponse res;
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
            if (optMcgs.isPresent()) {
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
                        // Sai s??? ti???n
                        throw new MegapayUpdateException("OR_134");
                    }

                    if (tran.getResponseCode() != null && tran.getTransactionNo() != null) {
                        // Giao d???ch ???? t???n t???i. Xin h??y t???o giao d???ch m???i
                        throw new MegapayUpdateException("DC_103");
                    }

                    saveTran(tran,
                            fields.get(MegaPayConstant.RESULT_MSG),
                            fields.get(MegaPayConstant.RESULT_CD),
                            megaPay.getResponseDescription(fields.get(MegaPayConstant.RESULT_CD)),
                            fields.get(MegaPayConstant.TRX_ID),
                            DateUtils.formatDateYYYYMMDDHHMMSS(now),
                            now);

                    res = new UpdateMegaPayResponse(megaPayRequest.getResultCd(), megaPayRequest.getResultMsg());

                } else {
                    throw new MegapayUpdateException("DC_101");
                }
            } else {
                throw new MegapayUpdateException("FL_902");
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateMegaPayResponse(e.getMessage(), megaPay.getResponseDescription(e.getMessage()));
        }
        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }
}
