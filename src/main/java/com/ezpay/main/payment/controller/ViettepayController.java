package com.ezpay.main.payment.controller;

import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.payment.facade.ViettelPayFacade;
import com.ezpay.main.payment.model.res.QueryViettelPayReponse;
import com.ezpay.main.payment.model.res.UpdateViettelPayResponse;
import com.ezpay.main.payment.model.res.VerifyViettelPayReponse;
import com.ezpay.main.payment.utils.PaymentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(PaymentPath.TRANSACTION_API)
public class ViettepayController {
    @Autowired
    private ViettelPayFacade viettelPayFacade;

    @PostMapping(value = PaymentPath.VIETTELPAY_UPDATE_API, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public UpdateViettelPayResponse updateViettelpay(@RequestBody MultiValueMap<String, String> params, HttpServletRequest httpReq) {
        return viettelPayFacade.updateViettelpay(params, StringKeyUtils.getIp(httpReq));
    }

    @PostMapping(value = PaymentPath.VIETTELPAY_VERIFY_API, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public VerifyViettelPayReponse verifyViettelpay(@RequestBody MultiValueMap<String, String> params, HttpServletRequest httpReq) {
        return viettelPayFacade.verifyViettelpay(params, StringKeyUtils.getIp(httpReq));
    }

    @PostMapping(value = PaymentPath.VIETTELPAY_QUERY_API, consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public QueryViettelPayReponse queryViettelpay(@RequestBody MultiValueMap<String, String> params, HttpServletRequest httpReq) {
        return viettelPayFacade.queryViettelpay(params, StringKeyUtils.getIp(httpReq));
    }
}
