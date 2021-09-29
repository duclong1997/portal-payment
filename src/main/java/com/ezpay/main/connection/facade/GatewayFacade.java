package com.ezpay.main.connection.facade;

import com.ezpay.core.entity.*;
import com.ezpay.core.facade.BaseFacade;
import com.ezpay.core.model.Res;
import com.ezpay.main.authen.TokenProvider;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewayService;
import com.ezpay.main.payment.service.MerchantGatewaysettingService;
import com.ezpay.main.payment.service.MerchantProjectService;
import com.ezpay.main.connection.exception.GatewayIsNotExistException;
import com.ezpay.main.connection.exception.MerchantNotActivatedGatewayException;
import com.ezpay.main.connection.model.res.GatewayResponse;
import com.ezpay.main.connection.model.res.GatewaysettingResponse;
import com.ezpay.main.connection.model.MerchantGatewayModel;
import com.ezpay.main.connection.model.MerchantGatewaysettingModel;
import com.ezpay.main.connection.service.GatewayService;
import com.ezpay.main.connection.utils.ConnectionKey;
import com.ezpay.main.connection.utils.ConnectionPath;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class GatewayFacade extends BaseFacade {
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private GatewayService gatewayService;
    @Autowired
    private MerchantProjectService merchantProjectService;
    @Autowired
    private MerchantGatewayService merchantGatewayService;
    @Autowired
    private MerchantGatewaysettingService merchantGatewaysettingService;
    @Autowired
    private Gson gson;
    @Autowired
    private EsApiLogService esApiLogService;

    @Transactional(readOnly = true)
    public Res getAll(String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject mp = optMp.get();

            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.REGISTER_API);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setMerchantName(mp.getMerchant().getName());
            log.setProjectId(mp.getProjectId());
            log.setMerchantId(String.valueOf(mp.getMerchant().getId()));
            log.setProjectCode(mp.getProject().getCode());

            List<Gateway> lstGateway = gatewayService.findAll();
            List<GatewayResponse> lstRes = new ArrayList<>();
            List<GatewaysettingResponse> params;
            for (Gateway g : lstGateway) {
                if (g.isActive()) {
                    params = new ArrayList<>();
                    for (Gatewaysetting gt : g.getParams()) {
                        params.add(new GatewaysettingResponse(gt.getParameter(), gt.getType()));
                    }
                    lstRes.add(new GatewayResponse(g.getCode(), g.getName(), params));
                }
            }
            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL, lstRes);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional(readOnly = true)
    public Res getConfig(String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject mp = optMp.get();

            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.REGISTER_API);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setMerchantName(mp.getMerchant().getName());
            log.setProjectId(mp.getProjectId());
            log.setMerchantId(String.valueOf(mp.getMerchant().getId()));
            log.setProjectCode(mp.getProject().getCode());

            List<MerchantGatewayModel> lstRes = new ArrayList<>();
            List<MerchantGatewaysettingModel> params;
            for (MerchantGateway mg : mp.getMerchantGateways()) {
                params = new ArrayList<>();
                for (MerchantGatewaysetting mgs : mg.getParams()) {
                    params.add(new MerchantGatewaysettingModel(mgs.getParameter(), mgs.getValue(), mgs.getType()));
                }
                lstRes.add(new MerchantGatewayModel(mg.getGateway().getCode(), mg.getGateway().getName(), mg.isActive(), params));
            }
            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL, lstRes);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional
    public Res update(MerchantGatewayModel req, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject mp = optMp.get();

            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.REGISTER_API);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setMerchantName(mp.getMerchant().getName());
            log.setProjectId(mp.getProjectId());
            log.setMerchantId(String.valueOf(mp.getMerchant().getId()));
            log.setProjectCode(mp.getProject().getCode());
            log.setRequest(gson.toJson(req));

            Optional<MerchantGateway> otpMg = merchantGatewayService.getByMerchantAndGateway(mp.getId(), req.getCode());
            MerchantGateway mg = null;
            if (otpMg.isPresent()) {
                mg = otpMg.get();
                if (!mg.isActive()) {
                    mg.setActive(true);
                    merchantGatewayService.save(mg);
                }
                List<MerchantGatewaysetting> lstMgs = mg.getParams();
                for (MerchantGatewaysettingModel paramReq : req.getParams()) {
                    boolean fg = true;
                    for (MerchantGatewaysetting mgt : lstMgs) {
                        if (mgt.getParameter().equalsIgnoreCase(paramReq.getKey())) {
                            mgt.setValue(paramReq.getValue());
                            merchantGatewaysettingService.save(mgt);
                            fg = false;
                            break;
                        }
                    }
                    if (fg) {
                        MerchantGatewaysetting mgs = new MerchantGatewaysetting();
                        mgs.setGateway(mg);
                        mgs.setParameter(paramReq.getKey());
                        mgs.setValue(paramReq.getValue());
                        mgs.setType(paramReq.getType());
                        merchantGatewaysettingService.save(mgs);
                    }
                }
            } else {
                mg = new MerchantGateway();
                Optional<Gateway> optG = gatewayService.findById(req.getCode());
                if (!optG.isPresent()) {
                    throw new GatewayIsNotExistException();
                }
                mg.setGateway(optG.get());
                mg.setActive(true);
                mg.setMerchantProject(mp);
                merchantGatewayService.save(mg);

                for (MerchantGatewaysettingModel paramReq : req.getParams()) {
                    MerchantGatewaysetting mgs = new MerchantGatewaysetting();
                    mgs.setGateway(mg);
                    mgs.setParameter(paramReq.getKey());
                    mgs.setValue(paramReq.getValue());
                    mgs.setType(paramReq.getType());
                    merchantGatewaysettingService.save(mgs);
                }
            }

            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL);
        } catch (GatewayIsNotExistException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional
    public Res deactive(String code, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            MerchantProject mp = optMp.get();

            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.REGISTER_API);
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);
            log.setMerchantName(mp.getMerchant().getName());
            log.setProjectId(mp.getProjectId());
            log.setMerchantId(String.valueOf(mp.getMerchant().getId()));
            log.setProjectCode(mp.getProject().getCode());
            log.setRequest(code);

            Optional<MerchantGateway> otpMg = merchantGatewayService.getByMerchantAndGateway(mp.getId(), code);

            if (!otpMg.isPresent()) {
                throw new MerchantNotActivatedGatewayException();
            }

            MerchantGateway mg = otpMg.get();
            if (!mg.isActive()) {
                throw new MerchantNotActivatedGatewayException();
            }

            mg.setActive(false);
            merchantGatewayService.save(mg);

            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL);

        } catch (MerchantNotActivatedGatewayException e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, e.getMessage());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }
}
