package com.ezpay.main.payment.controller;

import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.payment.facade.MegapayFacade;
import com.ezpay.main.payment.model.req.UpdateMegaPayRequest;
import com.ezpay.main.payment.model.res.UpdateMegapayResponse;
import com.ezpay.main.payment.utils.PaymentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(PaymentPath.TRANSACTION_API)
public class MegapayController {

    @Autowired
    private MegapayFacade megapayFacade;

    @PostMapping(value = PaymentPath.MEGAPAY_UPDATE_API, consumes = MediaType.APPLICATION_JSON_VALUE)
    public UpdateMegapayResponse updateMegapay(@RequestBody UpdateMegaPayRequest megaPayRequest, HttpServletRequest request) {
        return megapayFacade.updateMegapay(megaPayRequest, StringKeyUtils.getIp(request));
    }
}
