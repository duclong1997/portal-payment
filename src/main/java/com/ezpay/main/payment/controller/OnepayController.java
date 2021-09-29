package com.ezpay.main.payment.controller;

import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.payment.facade.OnepayFacade;
import com.ezpay.main.payment.utils.PaymentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(PaymentPath.TRANSACTION_API)
public class OnepayController {
    @Autowired
    private OnepayFacade onepayfacade;

    @GetMapping(PaymentPath.ONEPAY_UPDATE_API)
    public String updateOnepay(@RequestParam Map<String, String> params, HttpServletRequest httpReq) {
        return onepayfacade.updateOnepay(params, StringKeyUtils.getIp(httpReq));
    }

}
