package com.ezpay.main.config;

import com.ezpay.main.authen.ErrorHandler;
import com.ezpay.main.authen.filter.JWTTokenFilter;
import com.ezpay.main.authen.service.UserPrincipalService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.security.config.http.SessionCreationPolicy.STATELESS;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private UserPrincipalService userService;
    @Autowired
    private ErrorHandler errorHandler;

    @Bean
    @Override
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JWTTokenFilter jwtTokenFilter() {
        return new JWTTokenFilter();
    }

    @Override
    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
        authenticationManagerBuilder //
                .userDetailsService(userService) //
                .passwordEncoder(passwordEncoder());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http //

                .cors() //
                .and() //
                .csrf() //
                .disable() //
                .exceptionHandling() //
                .authenticationEntryPoint(errorHandler) //
                .and() //
                .sessionManagement() //
                .sessionCreationPolicy(STATELESS);
//                .and() //
//                .authorizeRequests() //
//                .antMatchers("/connection/register",
//                        "/transaction/qrcode-vnpay-update",
//                        "/transaction/vnpay-update",
//                        "/transaction/onepay-update",
//                        "/transaction/viettelpay-update",
//                        "/transaction/viettelpay-verify-data",
//                        "/transaction/viettelpay-query-trans"
//                );
//				.permitAll() //
//				.anyRequest() //
//				.authenticated() //

//		http.addFilterBefore( //
//				authenticationFilter(), //
//				UsernamePasswordAuthenticationFilter.class //
//		).authorizeRequests().anyRequest().permitAll();

        http.addFilterBefore( //
                jwtTokenFilter(), //
                UsernamePasswordAuthenticationFilter.class //
        ).authorizeRequests().anyRequest().permitAll();

    }
}
