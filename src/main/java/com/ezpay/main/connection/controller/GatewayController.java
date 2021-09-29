package com.ezpay.main.connection.controller;

import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.connection.facade.GatewayFacade;
import com.ezpay.main.connection.model.MerchantGatewayModel;
import com.ezpay.main.connection.utils.ConnectionPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ConnectionPath.GATEWAY_API)
public class GatewayController {
    @Autowired
    private GatewayFacade gatewayFacade;

    @GetMapping(value = ConnectionPath.GET_ALL_API, produces = "application/json;charset=UTF-8")
    public Res getAll(HttpServletRequest httpReq){
        return gatewayFacade.getAll(StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(value = ConnectionPath.GET_CONFIG_API, produces = "application/json;charset=UTF-8")
    public Res getConfig(HttpServletRequest httpReq){
        return gatewayFacade.getConfig(StringKeyUtils.getIp(httpReq));
    }

    @PostMapping(value = ConnectionPath.UPDATE_API, produces = "application/json;charset=UTF-8")
    public Res update(@RequestBody MerchantGatewayModel req, HttpServletRequest httpReq){
        return gatewayFacade.update(req, StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(value = ConnectionPath.DEACTIVE_API, produces = "application/json;charset=UTF-8")
    public Res deactive(@PathVariable("code") String code ,HttpServletRequest httpReq){
        return gatewayFacade.deactive(code, StringKeyUtils.getIp(httpReq));
    }
}
