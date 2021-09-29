package com.ezpay.main.authen;

import com.ezpay.core.utils.DateUtils;
import com.ezpay.main.authen.constant.AuthenConstan;
import com.ezpay.main.authen.entity.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class TokenProvider {
    private final static Logger LOGGER = LoggerFactory.getLogger(TokenProvider.class);

    @Value("${app.security.token-exp}")
    private int tokenExp;

    public Map<String, String> getParams(String token) throws UsernameNotFoundException {
        if (!StringUtils.hasText(token)) {
            throw new UsernameNotFoundException("Token is empty!");
        }
        LOGGER.info("Token: " + token);
        String[] values = getTokenSubData(token);
        if (values.length != 3) {
            throw new UsernameNotFoundException("Length of token is incorrect!");
        }
        if (!token.contains(AuthenConstan.TIMESTAMP)) {
            throw new UsernameNotFoundException("Parameters Timestamp is not exist!");
        }
        if (!token.contains(AuthenConstan.CONNECT_ID)) {
            throw new UsernameNotFoundException("Parameters ConnectId is not exist!");
        }
        if (!token.contains(AuthenConstan.SIGNATURE)) {
            throw new UsernameNotFoundException("Parameters Signature is not exist!");
        }
        Map<String, String> fields = new HashMap<String, String>();
        for (String item : values) {
            String[] param = item.split("=");
            if (param.length != 2) {
                throw new UsernameNotFoundException("Parameters is incorrect");
            }
            fields.put(param[0], param[1]);
        }
        long timeConnect = Long.parseLong(fields.get(AuthenConstan.TIMESTAMP)) * 1000;
        long timeCurrent = System.currentTimeMillis();
        LOGGER.info("Timestamp: "+ timeConnect + " >>> " + timeCurrent);
        if (timeCurrent - timeConnect >= -tokenExp && timeCurrent - timeConnect <= tokenExp) {
            return fields;
        }
        throw new UsernameNotFoundException("Out of date");
    }

    public String[] getTokenSubData(String token) {
        try {
            return token.split(",");
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            return new String[]{};
        }
    }

    public String getConnectId() {
        UserPrincipal principal = (UserPrincipal) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return principal.getUsername();
    }
}
