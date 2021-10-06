package com.ezpay.main.connection.facade;

import com.ezpay.core.entity.*;
import com.ezpay.core.facade.BaseFacade;
import com.ezpay.core.model.Res;
import com.ezpay.core.utils.StringKeyUtils;
import com.ezpay.main.authen.TokenProvider;
import com.ezpay.main.connection.exception.MerchantIsExistException;
import com.ezpay.main.connection.exception.MerchantProjectIsNotExistException;
import com.ezpay.main.connection.exception.ProjectIsNotExistException;
import com.ezpay.main.connection.model.req.RegisterRequest;
import com.ezpay.main.connection.model.res.DetailMerchantResponse;
import com.ezpay.main.connection.model.res.RegisterResponse;
import com.ezpay.main.connection.service.GatewayService;
import com.ezpay.main.connection.service.MerchantService;
import com.ezpay.main.connection.service.ProjectService;
import com.ezpay.main.connection.utils.ConnectionConstant;
import com.ezpay.main.connection.utils.ConnectionKey;
import com.ezpay.main.connection.utils.ConnectionPath;
import com.ezpay.main.payment.service.EsApiLogService;
import com.ezpay.main.payment.service.MerchantGatewayService;
import com.ezpay.main.payment.service.MerchantProjectService;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class ConnectionFacade extends BaseFacade {
    @Autowired
    private MerchantService merchantService;
    @Autowired
    private MerchantProjectService merchantProjectService;
    @Autowired
    private ProjectService projectService;
    @Autowired
    private TokenProvider tokenProvider;
    @Autowired
    private Gson gson;
    @Autowired
    private EsApiLogService esApiLogService;

    @Autowired
    private MerchantGatewayService merchantGatewayService;

    @Autowired
    private GatewayService gatewayService;

    @Transactional()
    public Res register(RegisterRequest req, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            Date now = new Date();

            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.REGISTER_API);
            log.setRequest(gson.toJson(req));
            log.setCreateDate(now);
            log.setType(EsApiLog.IN);
            log.setIp(ip);

            Merchant merchant = null;
            if (StringUtils.hasText(req.getTaxCode())) {
                Optional<Merchant> optM = merchantService.getByTaxCode(req.getTaxCode());
                if (optM.isPresent()) {
                    merchant = optM.get();
                }
            }

            Optional<Project> optP = projectService.findById(req.getCode());
            if (!optP.isPresent()) {
                throw new ProjectIsNotExistException();
            }
            Project p = optP.get();
            if (!p.isActive()) {
                throw new ProjectIsNotExistException();
            }

            if (merchant != null) {
                List<MerchantProject> lstMerchantProject = merchant.getMerchantProjects();
                if (lstMerchantProject != null && !lstMerchantProject.isEmpty()) {
                    for (MerchantProject mp : lstMerchantProject) {
                        if (mp.getProject().getCode().equals(req.getCode()) && mp.getProjectId().equals(req.getId())) {
                            throw new MerchantIsExistException();
                        }
                    }
                }
            } else {
                merchant = new Merchant();
                merchant.setTaxCode(req.getTaxCode());
                merchant.setName(req.getName());
                merchant.setActive(true);
                merchant.setAddress(req.getAddress());
                merchant.setCity(req.getCity());
                merchant.setCountry(req.getCountry());
                merchant.setDistrict(req.getDistrict());
                merchant.setCreatedDate(new Date());
                merchant.setUpdatedDate(new Date());
                merchantService.save(merchant);
            }

            MerchantProject merchantProject = new MerchantProject();
            merchantProject.setConnectKey(StringKeyUtils.generatedKey());
            merchantProject.setConnectId(StringKeyUtils.generatedUid());
            merchantProject.setActive(true);
            merchantProject.setCreatedDate(new Date());
            merchantProject.setUpdatedDate(new Date());
            merchantProject.setProjectId(req.getId());
            merchantProject.setIpnLink(req.getIpnLink());
            merchantProject.setProject(p);
            merchantProject.setMerchant(merchant);
            merchantProject = merchantProjectService.saveAndFlush(merchantProject);

            // merchant gateway
            List<MerchantGateway> merchantGateways = new ArrayList<>();
            List<Gateway> gateways = gatewayService.findAll();
            for (Gateway gateway : gateways) {
                MerchantGateway merchantGateway = new MerchantGateway();
                merchantGateway.setActive(false);
                merchantGateway.setGateway(gateway);
                merchantGateway.setMerchantProject(merchantProject);
                merchantGateways.add(merchantGateway);
            }

            merchantGatewayService.saveAll(merchantGateways);

            //add log
            log.setMerchantName(merchantProject.getMerchant().getName());
            log.setProjectId(merchantProject.getProjectId());
            log.setMerchantId(String.valueOf(merchantProject.getMerchant().getId()));
            log.setProjectCode(merchantProject.getProject().getCode());

            RegisterResponse registerResponse = new RegisterResponse(merchantProject.getConnectId(), merchantProject.getConnectKey());

            res = new Res(ConnectionKey.SUCCESSFUL, registerResponse);
        } catch (ProjectIsNotExistException ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.PROJECT_IS_NOT_EXIST);
        } catch (MerchantIsExistException ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.MERCHANT_IS_EXIST);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional()
    public Res getDetail(String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.DETAIL_API);
            log.setCreateDate(new Date());
            log.setType(EsApiLog.IN);
            log.setIp(ip);

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            if (!optMp.isPresent()) {
                throw new MerchantProjectIsNotExistException();
            }
            MerchantProject merchantProject = optMp.get();
            Project project = merchantProject.getProject();
            Merchant merchant = merchantProject.getMerchant();

            DetailMerchantResponse detailMerchantResponse = new DetailMerchantResponse();
            detailMerchantResponse.setId(merchantProject.getProjectId());
            detailMerchantResponse.setCode(project.getCode());
            detailMerchantResponse.setName(merchant.getName());
            detailMerchantResponse.setAddress(merchant.getAddress());
            detailMerchantResponse.setDistrict(merchant.getDistrict());
            detailMerchantResponse.setCity(merchant.getCity());
            detailMerchantResponse.setCountry(merchant.getCountry());
            detailMerchantResponse.setTaxCode(merchant.getTaxCode());
            detailMerchantResponse.setIpnLink(merchantProject.getIpnLink());
            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL, detailMerchantResponse);

            //add log
            log.setMerchantName(merchantProject.getMerchant().getName());
            log.setProjectId(merchantProject.getProjectId());
            log.setMerchantId(String.valueOf(merchantProject.getMerchant().getId()));
            log.setProjectCode(merchantProject.getProject().getCode());

        } catch (MerchantProjectIsNotExistException ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.MERCHANT_PROJECT_IS_NOT_EXIST);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }

    @Transactional()
    public Res update(RegisterRequest req, String ip) {
        EsApiLog log = new EsApiLog();
        Res res;
        try {
            //all log
            log.setMethod(HttpMethod.POST.name());
            log.setUrl(ConnectionPath.CONNECTION_API + ConnectionPath.UPDATE_API);
            log.setCreateDate(new Date());
            log.setType(EsApiLog.IN);
            log.setRequest(gson.toJson(req));
            log.setIp(ip);

            Optional<MerchantProject> optMp = merchantProjectService.getByConnectId(tokenProvider.getConnectId());
            if (!optMp.isPresent()) {
                throw new MerchantProjectIsNotExistException();
            }
            MerchantProject merchantProject = optMp.get();
            Merchant merchant = merchantProject.getMerchant();

            if (!req.getTaxCode().equals(merchant.getTaxCode())) {
                Optional<Merchant> optM = merchantService.getByTaxCode(req.getTaxCode());
                if (optM.isPresent() && optM.get().getId() != merchant.getId()) {
                    throw new MerchantIsExistException();
                }
            }

            if (merchant.getId() != ConnectionConstant.EZ) {
                merchant.setTaxCode(req.getTaxCode());
                merchant.setName(req.getName());
                merchant.setAddress(req.getAddress());
                merchant.setCity(req.getCity());
                merchant.setCountry(req.getCountry());
                merchant.setDistrict(req.getDistrict());
                merchant.setUpdatedDate(new Date());
                merchantService.save(merchant);
            }

            merchantProject.setIpnLink(req.getIpnLink());
            merchantProjectService.save(merchantProject);

            //add log
            log.setMerchantName(merchantProject.getMerchant().getName());
            log.setProjectId(merchantProject.getProjectId());
            log.setMerchantId(String.valueOf(merchantProject.getMerchant().getId()));
            log.setProjectCode(merchantProject.getProject().getCode());

            res = new Res(Res.CODE_SUCCESS, ConnectionKey.SUCCESSFUL);
        } catch (MerchantProjectIsNotExistException ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.MERCHANT_PROJECT_IS_NOT_EXIST);
        } catch (MerchantIsExistException ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.MERCHANT_IS_EXIST);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            res = new Res(Res.CODE_FAILED, ConnectionKey.ERROR);
        }

        log.setResponse(gson.toJson(res));
        esApiLogService.save(log);
        LOGGER.info("done");
        return res;
    }
}
