package com.ezpay.main.payment.facade;

import com.ezpay.core.entity.*;
import com.ezpay.core.gateway.Payment;
import com.ezpay.core.gateway.QRCode;
import com.ezpay.core.gateway.constant.*;
import com.ezpay.core.gateway.model.res.QrcodeVnpayResponse;
import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.core.utils.ZXingHelper;
import com.ezpay.main.authen.TokenProvider;
import com.ezpay.main.payment.exception.*;
import com.ezpay.main.payment.model.req.CreateRequest;
import com.ezpay.main.payment.model.res.QueryTransactionResponse;
import com.ezpay.main.payment.model.res.TransactionResponse;
import com.ezpay.main.payment.service.*;
import com.ezpay.main.payment.utils.PaymentConstant;
import com.ezpay.main.payment.utils.PaymentKey;
import com.ezpay.main.payment.utils.PaymentPath;
import com.ezpay.main.process.utils.ProcessConstant;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class TransactionFacade extends PaymentFacade {
    //cong thanh toan
    @Value("${payment.port.onepay-dom}")
    private String onepayDomUrl;
    @Value("${payment.port.onepay-inter}")
    private String onepayInterUrl;
    @Value("${payment.port.vnpay}")
    private String vnpayUrl;
    @Value("${payment.port.viettelpay}")
    private String viettelPayUrl;
    @Value("${payment.port.vnpay-qrcode}")
    private String qrcodeVnpayUrl;
    @Value("${payment.port.megapay}")
    private String megapayUrl;
    @Value("${ipnlink.update.megapay}")
    private String IPN_LINK_MEGAPAY;

    //sevices
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private MerchantProjectService merchantProjectService;
    @Autowired
    private MerchantGatewayService merchantGatewayService;

    //gateway
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
    @Qualifier("megaPay")
    private Payment megaPay;
    @Autowired
    @Qualifier("qrCodeVNPay")
    private QRCode qrcodeVnpay;
    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    public TransactionFacade(TransactionService transactionService, EsApiLogService esApiLogService, MerchantGatewaysettingService merchantGatewaysettingService, Gson gson) {
        super(transactionService, esApiLogService, merchantGatewaysettingService, gson);
    }

    @Transactional(rollbackFor = {GatewayNotConfiguredException.class, Exception.class})
    public Res createTransaction(CreateRequest req, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            //add log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.CREATE_API);
            log.setRequest(gson.toJson(req));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);

            if (Double.parseDouble(req.getAmount()) < 1 || Double.parseDouble(req.getAmount()) > 10000000) {
                throw new AmountInvalidException();
            }

            //get merchant
            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject merchantProject = optMp.get();
            Optional<MerchantGateway> optMg = merchantGatewayService.getByMerchantAndGateway(merchantProject.getId(), req.getGatewayCode());
            if (!optMg.isPresent()) {
                throw new GatewayNotConfiguredException();
            }
            MerchantGateway mg = optMg.get();

            if (!mg.isActive()) {
                throw new GatewayNotConfiguredException();
            }

            //check transaction
            Optional<Transaction> optT = transactionService.getTransaction(merchantProject.getId(), req.getOrderInfo());
            if (optT.isPresent()) {
//                Transaction t = optT.get();
//                if (PaymentConstant.RESPONSE_CODE_SUCCESS_ONEPAY.equals(t.getResponseCode())
//                        || PaymentConstant.RESPONSE_CODE_SUCCESS_VNPAY.equals(t.getResponseCode())) {
//                    throw new TransactionMadeSuccessException();
//                } else {
//                    throw new TransactionMadeFailedException();
//                }
                throw new TransactionAlreadyExistsException();
            }

            //add log
            log.setMerchantName(merchantProject.getMerchant().getName());
            log.setProjectId(merchantProject.getProjectId());
            log.setMerchantId(String.valueOf(merchantProject.getMerchant().getId()));
            log.setProjectCode(merchantProject.getProject().getCode());

            //gen txnRef
            String txnRef = (req.getGatewayCode().endsWith(PaymentConstant.QR_CODE) || PaymentConstant.VIETTELPAY.equalsIgnoreCase(req.getGatewayCode()) || PaymentConstant.MEGAPAY.equalsIgnoreCase(req.getGatewayCode())) ?
                    StringKeyUtils.generatedtxnRef2() : StringKeyUtils.generatedtxnRef();

            //save transaction
            Transaction tran = createTran(req, mg, merchantProject, now, txnRef);
            transactionService.save(tran);

            //add log
            log.setTxnRef(txnRef);

            //get link gateway
            List<MerchantGatewaysetting> params = new ArrayList<>();
            String strRes = "";
            Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
            MerchantGatewaysetting pramKey = optMcgs.get();
            TransactionResponse transactionResponse;
            switch (req.getGatewayCode()) {
                case GatewayConstant.ONEPAY_DOM_CODE: //onepay noi dia
                    params.addAll(getParamOnepayDom(req, mg.getParams(), txnRef));
                    strRes = onePay.getPaymentLink(params, onepayDomUrl);
                    transactionResponse = new TransactionResponse(TransactionResponse.URL, TransactionResponse.GET, strRes);
                    break;
                case GatewayConstant.ONEPAY_INTER_CODE: //onepay quoc te
                    params.addAll(getParamOnepayInter(req, mg.getParams(), txnRef));
                    strRes = onePay.getPaymentLink(params, onepayInterUrl);
                    transactionResponse = new TransactionResponse(TransactionResponse.URL, TransactionResponse.GET, strRes);
                    break;
                case GatewayConstant.VNPAY_CODE: //vnpay
                    params.addAll(getParamVnpay(req, mg.getParams(), txnRef, now));
                    strRes = vnPay.getPaymentLink(params, vnpayUrl);
                    transactionResponse = new TransactionResponse(TransactionResponse.URL, TransactionResponse.GET, strRes);
                    break;
                case GatewayConstant.VIETTEL_CODE: //viettelpay
                    params.addAll(getParamViettelPay(req, mg.getParams(), txnRef));
                    strRes = viettelPay.getPaymentLink(params, viettelPayUrl);
                    transactionResponse = new TransactionResponse(TransactionResponse.URL, TransactionResponse.GET, strRes);
                    break;
                case GatewayConstant.QR_VNPAY_CODE: //vnpay qrcode
                    params.addAll(getParamQrcodeVnpay(req, mg.getParams(), txnRef));
                    String strReq = qrcodeVnpay.getQRCode(params);

                    //get qrcode form vnpay
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.TEXT_PLAIN);
                    HttpEntity<String> request = new HttpEntity<>(strReq, headers);
                    ResponseEntity<String> result = restTemplate.postForEntity(qrcodeVnpayUrl, request, String.class);

                    //check data and return qrcode
                    QrcodeVnpayResponse qrcodeReponse = gson.fromJson(result.getBody(), QrcodeVnpayResponse.class);
                    if (qrcodeVnpay.checkQRCode(qrcodeReponse, pramKey.getValue(), qrcodeVnpayUrl)) {
                        transactionService.save(tran);
                        strRes = ZXingHelper.getQRCodeImage(qrcodeReponse.getData(), PaymentConstant.WIDTH_QRCODE, PaymentConstant.HEIGTH_QRCODE);
                        transactionResponse = new TransactionResponse(TransactionResponse.QR_CODE, null, strRes);
                        break;
                    }

                    //loi signature
                    throw new WrongSignatureException();
                case GatewayConstant.QR_VIETTEL_CODE: //viettelpay qrcode
                    String data = createQrcodeViettelPay(req, mg.getParams(), txnRef);
                    transactionService.save(tran);
                    strRes = ZXingHelper.getQRCodeImage(data, PaymentConstant.WIDTH_QRCODE, PaymentConstant.HEIGTH_QRCODE);
                    transactionResponse = new TransactionResponse(TransactionResponse.QR_CODE, null, strRes);
                    break;
                case GatewayConstant.MEGAPAY_CODE: //VNPT
                    // todo
                    // edit txnRef
                    for (MerchantGatewaysetting param : mg.getParams()) {
                        if (MegaPayConstant.MERID.equals(param.getParameter())) {
                            txnRef = param.getValue() + txnRef;
                            break;
                        }
                    }
                    params.addAll(getParamMegaPay(req, mg.getParams(), txnRef, now, IPN_LINK_MEGAPAY));
                    tran.setTxnRef(txnRef);
                    transactionService.save(tran);
                    strRes = megaPay.getPaymentLink(params, megapayUrl);
                    transactionResponse = new TransactionResponse(TransactionResponse.URL, TransactionResponse.POST, strRes);
                    break;
                default:
                    throw new GatewayNotConfiguredException();
            }

            res = new Res(Res.CODE_SUCCESS, PaymentKey.SUCCESSFUL, transactionResponse);

        } catch (AmountInvalidException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (TransactionAlreadyExistsException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (GatewayNotConfiguredException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, PaymentKey.FAILED);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional(readOnly = true)
    public Res queryTransaction(Map<String, String> params, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Map<String, String> fields = decoderParam(params);
            LOGGER.info("queryTransaction param: " + fields.toString());

            Date now = new Date();

            //add log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.QUERY_API);
            log.setRequest(gson.toJson(fields));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);

            //check param
            QueryTransactionResponse dataRes = new QueryTransactionResponse();
            if (StringUtils.hasText(fields.get(OnePayConstant.TRANSACTION_REF))) { //onepay
                Transaction tran = getTran(fields.get(OnePayConstant.TRANSACTION_REF));

                if (tran == null) {
                    throw new TransactionNotFoundException();
                }
                // check amount
                String amount = fields.get(OnePayConstant.ORDER_AMOUNT);
                if (tran.getAmount() != Double.parseDouble(amount.substring(0, amount.length() - 2))) {
                    throw new AmountInvalidException();
                }

                MerchantGatewaysetting mcgs = getMerchantGatewaysetting(tran, log);

                if (onePay.checkFields(fields, mcgs.getValue())) {
                    //tra ket qua
                    dataRes = parseQueryTransactionOnepay(fields, tran);
                } else {
                    throw new TransactionWrongSignatureException();
                }
            } else if (StringUtils.hasText(fields.get(VNPayConstant.TRANSACTION_REF))) { //vnpay
                Transaction tran = getTran(fields.get(VNPayConstant.TRANSACTION_REF));

                if (tran == null) {
                    throw new TransactionNotFoundException();
                }

                // check amount
                String amount = fields.get(VNPayConstant.ORDER_AMOUNT);
                if (Math.round(tran.getAmount()) != Math.round(Double.parseDouble(amount.substring(0, amount.length() - 2)))) {
                    throw new AmountInvalidException();
                }

                MerchantGatewaysetting mcgs = getMerchantGatewaysetting(tran, log);

                if (vnPay.checkFields(fields, mcgs.getValue())) {
                    //tra ket qua
                    dataRes = parseQueryTransactionVNpay(fields, tran);
                } else {
                    throw new TransactionWrongSignatureException();
                }
            } else if (StringUtils.hasText(fields.get(ViettelPayConstant.ORDER_ID))) { //viettel pay
                Transaction tran = getTran(fields.get(ViettelPayConstant.ORDER_ID));

                if (tran == null) {
                    throw new TransactionNotFoundException();
                }

                // check amount
                String amount = fields.get(ViettelPayConstant.TRANS_AMOUNT);
                if (tran.getAmount() != Double.parseDouble(amount)) {
                    throw new AmountInvalidException();
                }
                MerchantGatewaysetting mcgs = getMerchantGatewaysetting(tran, log);
                fields.put(ViettelPayConstant.ACCESS_CODE, getValueParam(tran.getMerchantGateway().getParams(), ViettelPayConstant.ACCESS_CODE));
                if (viettelPay.checkFields(fields, mcgs.getValue())) {
                    //tra ket qua
                    dataRes = parseQueryTransactionViettelpay(fields, tran);
                } else {
                    throw new TransactionWrongSignatureException();
                }
            } else if (StringUtils.hasText(fields.get(MegaPayConstant.MERTRX_ID))) { // Mega pay
                Transaction tran = getTran(fields.get(MegaPayConstant.MERTRX_ID));

                if (tran == null) {
                    throw new TransactionNotFoundException();
                }

                // check amount
                String amount = fields.get(MegaPayConstant.AMOUNT);
                if (tran.getAmount() != Double.parseDouble(amount)) {
                    throw new AmountInvalidException();
                }

                MerchantGatewaysetting mcgs = getMerchantGatewaysetting(tran, log);
                // get encode key
                fields.put(MegaPayConstant.ENCODE_KEY, getValueParam(tran.getMerchantGateway().getParams(), MegaPayConstant.ENCODE_KEY));
                if (megaPay.checkFields(fields, mcgs.getValue())) {
                    //tra ket qua
                    dataRes = parseQueryTransactionMegapay(fields, tran);
                } else {
                    throw new TransactionWrongSignatureException();
                }
            } else {
                throw new TransactionNotFoundException();
            }

            res = new Res(Res.CODE_SUCCESS, PaymentKey.SUCCESSFUL, dataRes);
        } catch (AmountInvalidException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (TransactionNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (TransactionWrongSignatureException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (QueryTansactionException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, PaymentKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    public Res checkTransaction(String orderInfo, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            //add log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(PaymentPath.TRANSACTION_API + PaymentPath.QUERY_API);
            log.setRequest(orderInfo);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);

            //get merchant
            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject merchantProject = optMp.get();

            Optional<Transaction> optTran = transactionService.getTransactionUpdated(merchantProject.getId(), orderInfo);
            if (!optTran.isPresent()) {
                throw new TransactionNotFoundException();
            }
            QueryTransactionResponse dataRes = parseFindByOrderInfor(optTran.get());

            res = new Res(Res.CODE_SUCCESS, PaymentKey.SUCCESSFUL, dataRes);
        } catch (TransactionNotFoundException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, PaymentKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    //==================================================================================================================

    private MerchantGatewaysetting getMerchantGatewaysetting(Transaction tran, EsApiLog log) {
        MerchantProject merchantProject = tran.getMerchantProject();
        MerchantGateway mg = tran.getMerchantGateway();
        Optional<MerchantGatewaysetting> optMcgs = merchantGatewaysettingService.getKeyByMerchantGateway(mg.getId());
        MerchantGatewaysetting mcgs = optMcgs.get();

        //add log
        log.setMerchantName(merchantProject.getMerchant().getName());
        log.setProjectId(merchantProject.getProjectId());
        log.setMerchantId(String.valueOf(merchantProject.getMerchant().getId()));
        log.setProjectCode(merchantProject.getProject().getCode());
        log.setGatewayCode(mg.getGateway().getCode());
        log.setTxnRef(tran.getTxnRef());
        return mcgs;
    }

    private QueryTransactionResponse parseQueryTransactionMegapay(Map<String, String> params, Transaction tran) {
        QueryTransactionResponse result = new QueryTransactionResponse();
        result.setAmount(Double.parseDouble(params.get(MegaPayConstant.AMOUNT)));
        result.setCode(params.get(MegaPayConstant.RESULT_CD));
        result.setCurrencyCode(params.get(MegaPayConstant.CURRENCY));
        result.setGatewayCode(tran.getGateway().getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(tran.getTransactionDate());
        result.setMessage(megaPay.getResponseDescription(params.get(MegaPayConstant.RESULT_CD)));
        result.setOrderInfo(tran.getOrderInfo());
        result.setTransactionNo(params.get(MegaPayConstant.TRX_ID));
        result.setTxnRef(tran.getTxnRef());
        return result;
    }

    private QueryTransactionResponse parseQueryTransactionViettelpay(Map<String, String> params, Transaction tran) {
        QueryTransactionResponse result = new QueryTransactionResponse();
        result.setAmount(Double.parseDouble(params.get(ViettelPayConstant.TRANS_AMOUNT)));
        result.setCode(params.get(ViettelPayConstant.ERROR_CODE));
        result.setCurrencyCode(ProcessConstant.CURRENCY_CODE_VALUE);
        result.setGatewayCode(tran.getGateway().getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(tran.getTransactionDate());
        result.setMessage(viettelPay.getResponseDescription(params.get(ViettelPayConstant.ERROR_CODE)));
        result.setOrderInfo(tran.getOrderInfo());
        result.setTransactionNo(params.get(ViettelPayConstant.VT_TRANSACTION_ID));
        result.setTxnRef(tran.getTxnRef());
        return result;
    }

    private QueryTransactionResponse parseQueryTransactionVNpay(Map<String, String> params, Transaction tran) {
        QueryTransactionResponse result = new QueryTransactionResponse();
        result.setAmount(Double.parseDouble(params.get(VNPayConstant.ORDER_AMOUNT)));
        result.setCode(params.get(VNPayConstant.RESPONSE));
        result.setCurrencyCode(ProcessConstant.CURRENCY_CODE_VALUE);
        result.setGatewayCode(tran.getGateway().getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(tran.getTransactionDate());
        result.setMessage((params.get(VNPayConstant.MESSAGE) == null) ? vnPay.getResponseDescription(params.get(VNPayConstant.RESPONSE)) : params.get(VNPayConstant.MESSAGE));
        result.setOrderInfo(tran.getOrderInfo());
        result.setTransactionNo(params.get(VNPayConstant.TRANSACTION_NO));
        result.setTxnRef(tran.getTxnRef());
        return result;
    }


    private QueryTransactionResponse parseQueryTransactionOnepay(Map<String, String> params, Transaction tran) {
        QueryTransactionResponse result = new QueryTransactionResponse();
        result.setAmount(Double.parseDouble(params.get(OnePayConstant.ORDER_AMOUNT)));
        result.setCode(params.get(OnePayConstant.RESPONSE));
        result.setCurrencyCode(ProcessConstant.CURRENCY_CODE_VALUE);
        result.setGatewayCode(tran.getGateway().getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(tran.getTransactionDate());
        result.setMessage(onePay.getResponseDescription(params.get(OnePayConstant.RESPONSE)));
        result.setOrderInfo(tran.getOrderInfo());
        result.setTransactionNo(params.get(OnePayConstant.TRANSACTION_NO));
        result.setTxnRef(tran.getTxnRef());
        return result;
    }

    private QueryTransactionResponse parseFindByOrderInfor(Transaction transaction) {
        QueryTransactionResponse result = new QueryTransactionResponse();
        result.setAmount(transaction.getAmount());
        result.setCode(transaction.getResponseCode());
        result.setCurrencyCode(ProcessConstant.CURRENCY_CODE_VALUE);
        result.setGatewayCode(transaction.getGateway().getCode());
        result.setLocale(ProcessConstant.LOCALE_VALUE);
        result.setPayDate(transaction.getTransactionDate());
        result.setMessage(transaction.getResponseMessage());
        result.setOrderInfo(transaction.getOrderInfo());
        result.setTransactionNo(transaction.getTransactionNo());
        result.setTxnRef(transaction.getTxnRef());
        return result;
    }

    private Map<String, String> decoderParam(Map<String, String> params) {
        Map<String, String> fields = new HashMap<>();
        params.forEach((k, v) -> {
            try {
                fields.put(k, URLDecoder.decode(v, StandardCharsets.UTF_8.toString()));
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        });
        return fields;
    }

}
