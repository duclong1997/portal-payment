package com.ezpay.main.payment.controller;

import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.payment.facade.VnpayFacade;
import com.ezpay.main.payment.model.req.UpdateQrcodeVnpayRequest;
import com.ezpay.main.payment.model.res.UpdateVnpayResponse;
import com.ezpay.main.payment.utils.PaymentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(PaymentPath.TRANSACTION_API)
public class VnpayController {
    @Autowired
    private VnpayFacade vnpayFacade;

    @PostMapping(PaymentPath.QRCODE_VNPAY_UPDATE_API)
    public Res updateQrcodeVnpay(@RequestBody UpdateQrcodeVnpayRequest req, HttpServletRequest httpReq) {
        return vnpayFacade.updateQrcodeVnpay(req, StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(PaymentPath.VNPAY_UPDATE_API)
    public UpdateVnpayResponse updateVnpay(@RequestParam Map<String, String> params, HttpServletRequest httpReq) {
        return vnpayFacade.updateVnpay(params, StringKeyUtils.getIp(httpReq));
    }
}
