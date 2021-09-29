package com.ezpay.main.authen.filter;

import com.ezpay.main.authen.service.UserPrincipalService;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class JWTTokenFilter extends OncePerRequestFilter {
    private final static Logger LOGGER = LoggerFactory.getLogger(JWTTokenFilter.class);

    @Autowired
    private UserPrincipalService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            LOGGER.info("HttpServletRequest: " + request.getServletPath());
            String path = request.getServletPath();
            if (path.startsWith("/connection/register")
                    || path.startsWith("/transaction/qrcode-vnpay-update")
                    || path.startsWith("/transaction/vnpay-update")
                    || path.startsWith("/transaction/onepay-update")
                    || path.startsWith("/transaction/viettelpay-update")
                    || path.startsWith("/transaction/viettelpay-verify-data")
                    || path.startsWith("/transaction/viettelpay-query-trans")
                    || path.startsWith("/transaction/megapay-update")
                    || path.startsWith("/transaction/query")
            ) {
                filterChain.doFilter(request, response);
                return;
            }
            String token = getTokenFromRequest(request);
            UserDetails userDetails = userService.loadUserByUsername(token);
            UsernamePasswordAuthenticationToken authentication = //
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (Exception e) {
            setResponse(response, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized", e.getMessage());
            LOGGER.error(e.getMessage(), e);
        }
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("EZPAY ")) {
            return bearerToken.substring(6, bearerToken.length());
        }
        return null;
    }

    private void setResponse(HttpServletResponse response, int sc, String error, String message) {
        response.setHeader("Content-Type", "application/json;charset=UTF-8");
        response.setStatus(sc);
        try {
            response.getWriter().write(errorResponse(sc, "Unauthorized", message));
            response.getWriter().close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String errorResponse(int sc, String error, String message) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS Z");
        Map<String, String> m = new HashMap<String, String>();
        m.put("timestamp", df.format(new Date()));
        m.put("status", String.valueOf(sc));
        m.put("error", error);
        m.put("message", message);
        return new JSONObject(m).toString();
    }

}
