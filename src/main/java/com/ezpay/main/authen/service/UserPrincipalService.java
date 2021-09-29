package com.ezpay.main.authen.service;

import com.ezpay.core.entity.MerchantProject;
import com.ezpay.main.authen.TokenProvider;
import com.ezpay.main.authen.constant.AuthenConstan;
import com.ezpay.main.authen.entity.UserPrincipal;
import com.ezpay.main.redis.service.MerchantProjectCacheService;
import com.ezpay.main.payment.service.MerchantProjectService;
import org.apache.commons.codec.digest.DigestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class UserPrincipalService implements UserDetailsService {
    private final static Logger LOGGER = LoggerFactory.getLogger(UserPrincipalService.class);


    @Autowired
    private MerchantProjectService merchantProjectService;
    @Autowired
    private MerchantProjectCacheService merchantProjectCacheService;
    @Autowired
    private TokenProvider tokenProvider;

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Map<String, String> params = tokenProvider.getParams(s);
        MerchantProject merchantProject = merchantProjectCacheService.getValue(params.get(AuthenConstan.CONNECT_ID));
        if (merchantProject == null) {
            Optional<MerchantProject> opMp = merchantProjectService.getByConnectId(params.get(AuthenConstan.CONNECT_ID));
            if (opMp.isPresent()) {
                merchantProject = opMp.get();
                merchantProjectCacheService.putMap(merchantProject.getConnectId(), merchantProject);
            } else {
                throw new UsernameNotFoundException("connectId is not exist");
            }
        }
        if (checkSignature(params, merchantProject)) {
            throw new UsernameNotFoundException("Signature incorrect");
        }
        return new UserPrincipal(merchantProject.getConnectId(), merchantProject.getConnectKey(), merchantProject.isActive(), Collections.emptyList());
    }

    private boolean checkSignature(Map<String, String> fields, MerchantProject merchantProject) {
        if (!merchantProject.isActive()) {
            return true;
        }
        String singer = DigestUtils.sha256Hex(merchantProject.getConnectId() + fields.get(AuthenConstan.TIMESTAMP) + merchantProject.getConnectKey());
        if (singer.equalsIgnoreCase(fields.get(AuthenConstan.SIGNATURE))) {
            return false;
        }
        return true;
    }

}
