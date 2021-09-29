package com.ezpay.main.payment.controller;

import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.payment.facade.TransactionFacade;
import com.ezpay.main.payment.model.req.CreateRequest;
import com.ezpay.main.payment.utils.PaymentPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping(PaymentPath.TRANSACTION_API)
public class TransactionController {
    @Autowired
    private TransactionFacade transactionFacade;

    @PostMapping(value = PaymentPath.CREATE_API, produces = "application/json;charset=UTF-8")
    public Res createTransaction(@RequestBody CreateRequest req, HttpServletRequest httpReq) {
        return transactionFacade.createTransaction(req, StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(value = PaymentPath.QUERY_API, produces = "application/json;charset=UTF-8")
    public Res queryTransaction(@RequestParam Map<String, String> params, HttpServletRequest httpReq){
        return transactionFacade.queryTransaction(params, StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(value = PaymentPath.CHECK_API, produces = "application/json;charset=UTF-8")
    public Res checkTransaction(@PathVariable("code") String orderInfo, HttpServletRequest httpReq){
        return transactionFacade.checkTransaction(orderInfo, StringKeyUtils.getIp(httpReq));
    }
}
