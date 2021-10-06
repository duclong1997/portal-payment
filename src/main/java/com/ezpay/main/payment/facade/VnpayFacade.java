package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.EsApiLog;
import com.ezpay.core.entity.MerchantGateway;
import com.ezpay.core.entity.MerchantGatewaysetting;
import com.ezpay.core.entity.Transaction;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.QRCode;
import com.ezpay.core.gateway.constant.GatewayConstant;
import com.ezpay.core.gateway.constant.VNPayConstant;
import com.ezpay.core.model.Res;
import com.ezpay.main.payment.exception.TransactionAmoutInvalidException;
import com.ezpay.main.payment.exception.TransactionConfirmedException;
import com.ezpay.main.payment.exception.TransactionIsNotExistException;
import com.ezpay.main.payment.exception.WrongSignatureException;
import com.ezpay.main.payment.model.req.UpdateQrcodeVnpayRequest;
import com.ezpay.main.payment.model.res.QrcodeVnpayAmountResponse;
import com.ezpay.main.payment.model.res.QrcodeVnpayTxnidResponse;
import com.ezpay.main.payment.model.res.UpdateVnpayResponse;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.TransactionService;
import com.ezpay.main.payment.utils.PaymentCode;
import com.ezpay.main.payment.utils.PaymentConstant;
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
public class VnpayFacade extends PaymentFacade {
    @Autowired
    @Qualifier("vnPay")
    private Payment vnPay;
    @Autowired
    @Qualifier("qrCodeVNPay")
    private QRCode qrcodeVnpay;

    @Autowired
    public VnpayFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional
    public Res updateQrcodeVnpay(UpdateQrcodeVnpayRequest req, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();
            String strReq = gson.toJson(req);
            LOGGER.info("request json:" + strReq);

            //luu log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.QRCODE_VNPAY_UPDATE_API);
            log.setRequest(strReq);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.QR_VNPAY_CODE);

            Optional<Transaction> optTst = transactionService.getByTxnRef(req.getTxnId());
            if (!optTst.isPresent()) {
                throw new TransactionIsNotExistException();
            }
            Transaction transaction = optTst.get();

            //ass log
            log.setTxnRef(transaction.getTxnRef());

            MerchantGateway mg = transaction.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgs = optMcgs.get();

            //check data
            Map<String, String> fields = getFields(req);
            if (qrcodeVnpay.checkPayment(fields, mcgs.getValue())) {
                QrcodeVnpayTxnidResponse qrTRes = new QrcodeVnpayTxnidResponse(req.getTxnId());

                if (PaymentConstant.RESPONSE_CODE_SUCCESS_VNPAY.equals(transaction.getResponseCode())) {
                    LOGGER.info(PaymentCode.CODE_TRANSACTION_PAYMENTED + " - " + PaymentKey.TRANSACTION_PAYMENTED);
                    res = new Res(PaymentCode.CODE_TRANSACTION_PAYMENTED, PaymentKey.TRANSACTION_PAYMENTED, qrTRes);
                } else if (Math.round(transaction.getAmount()) != Math.round(Double.parseDouble(req.getAmount()))) {
                    LOGGER.info(PaymentCode.VNPAY_CODE_AMOUNT_IS_INCORRECT + " - amount = " + req.getAmount() + " - " + PaymentKey.AMOUNT_IS_INCORRECT);
                    QrcodeVnpayAmountResponse qrARes = new QrcodeVnpayAmountResponse(String.valueOf(transaction.getAmount()));
                    res = new Res(PaymentCode.VNPAY_CODE_AMOUNT_IS_INCORRECT, PaymentKey.AMOUNT_IS_INCORRECT, qrARes);
                } else {
                    //update transaction
                    transaction.setStatus(Transaction.STATUS_GATEWAY_RESPONDED);
                    transaction.setNotes(req.getMessage());
                    transaction.setResponseCode(req.getCode());
                    transaction.setResponseMessage(qrcodeVnpay.getResponseDescription(req.getCode()));
                    transaction.setTransactionNo(req.getQrTrace());
                    transaction.setTransactionDate(req.getPayDate());
                    transaction.setUpdatedDate(now);
                    transactionService.save(transaction);

                    res = new Res(PaymentCode.CODE_SUCCESS, PaymentKey.VNPAY_MESSAGE_QR_SUCCESS, qrTRes);
                }
            } else {
                throw new WrongSignatureException();
            }
        } catch (TransactionIsNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(PaymentCode.VNPAY_CODE_CREATE, PaymentKey.VNPAY_MESSAGE_CREATE);
        } catch (WrongSignatureException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(PaymentCode.QRCODE_VNPAY_CODE_WRONG_SIGNATURE, PaymentKey.QRCODE_VNPAY_MESSAGE_WRONG_SIGNATURE);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(PaymentCode.VNPAY_CODE_ERROR, PaymentKey.VNPAY_MESSAGE_ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional
    public UpdateVnpayResponse updateVnpay(Map<String, String> fields, String ip) {
        UpdateVnpayResponse res;
        EsApiLog log = new EsApiLog();
        try {
            Date now = new Date();
            LOGGER.info("updateVnpay param: " + fields.toString());

            //add log
            log.setMethod(HttpMethod.GET.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.VNPAY_UPDATE_API);
            log.setRequest(gson.toJson(fields));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setGatewayCode(GatewayConstant.VNPAY_CODE);

            //get transaction
//            Map<String, String> fields = StringQueryUtils.getFields(req);
            Transaction tran = getTran(fields.get(VNPayConstant.TRANSACTION_REF));

            //ass log
            log.setTxnRef(tran.getTxnRef());

            //get key
            MerchantGateway mg = tran.getMerchantGateway();
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting mcgs = optMcgs.get();


            //update transaction
            if (vnPay.checkFields(fields, mcgs.getValue())) {
                String amount = fields.get(VNPayConstant.ORDER_AMOUNT);
                if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount.substring(0, amount.length() - 2)))) {
                    throw new TransactionAmoutInvalidException();
                }

                if (tran.getResponseCode() != null && tran.getTransactionNo() != null) {
                    throw new TransactionConfirmedException();
                }

                saveTran(tran,
                        fields.get(VNPayConstant.MESSAGE),
                        fields.get(VNPayConstant.RESPONSE),
                        vnPay.getResponseDescription(fields.get(VNPayConstant.RESPONSE)),
                        fields.get(VNPayConstant.TRANSACTION_NO),
                        fields.get(VNPayConstant.PAY_DATE),
                        now);

                res = new UpdateVnpayResponse(PaymentCode.CODE_SUCCESS, PaymentKey.VNPAY_MESSAGE_SUCCESS);
            } else {
                throw new WrongSignatureException();
            }
        } catch (TransactionConfirmedException e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateVnpayResponse(PaymentCode.VNPAY_CODE_TRANSACTION_CONFIRMED, PaymentKey.TRANSACTION_CONFIRMED);
        } catch (TransactionAmoutInvalidException e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateVnpayResponse(PaymentCode.VNPAY_CODE_AMOUNT_INVALID, PaymentKey.AMOUNT_INVALID);
        } catch (WrongSignatureException e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateVnpayResponse(PaymentCode.VNPAY_CODE_WRONG_SIGNATURE, PaymentKey.VNPAY_MESSAGE_WRONG_SIGNATURE);
        } catch (TransactionIsNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateVnpayResponse(PaymentCode.VNPAY_CODE_TXNREF_EMPTY, PaymentKey.VNPAY_MESSAGE_TXNREF_EMPTY);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new UpdateVnpayResponse(PaymentCode.VNPAY_CODE_ERROR, PaymentKey.VNPAY_MESSAGE_ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

}
