package com.ezpay.main.connection.controller;

import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.connection.facade.ConnectionFacade;
import com.ezpay.main.connection.model.req.RegisterRequest;
import com.ezpay.main.connection.utils.ConnectionPath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

@RestController
@RequestMapping(ConnectionPath.CONNECTION_API)
public class ConnectionController {

    @Autowired
    private ConnectionFacade connectionFacade;

    @PostMapping(value = ConnectionPath.REGISTER_API, produces = "application/json;charset=UTF-8")
    public Res register(@RequestBody RegisterRequest req, HttpServletRequest httpReq) {
        return connectionFacade.register(req, StringKeyUtils.getIp(httpReq));
    }

    @GetMapping(value = ConnectionPath.DETAIL_API, produces = "application/json;charset=UTF-8")
    public Res getDetail(HttpServletRequest httpReq) {
        return connectionFacade.getDetail(StringKeyUtils.getIp(httpReq));
    }

    @PutMapping(value = ConnectionPath.UPDATE_API, produces = "application/json;charset=UTF-8")
    public Res update(@RequestBody RegisterRequest req, HttpServletRequest httpReq) {
        return connectionFacade.update(req, StringKeyUtils.getIp(httpReq));
    }

}
