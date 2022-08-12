package com.rstn.iws.webservice.config;

import com.rstn.iws.webservice.constant.Constant;
import com.rstn.iws.webservice.model.UserAuth;
import com.rstn.iws.webservice.repository.CaptureRepository;
import com.rstn.iws.webservice.repository.UserAuthRepository;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableWebSecurity
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter {
    private static final Logger logger = LogManager.getLogger(CaptureRepository.class);
    @Autowired
    Config config;

    @Autowired
    JASYPTConfig jasyptConfig;

    @Autowired
    UserAuthRepository userAuthRepository;

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.csrf().disable()
                .authorizeRequests().anyRequest().authenticated()
                .and().httpBasic();

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder authentication)
            throws Exception {
        logger.info("Start encoder password to login system");
        PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
        List<UserAuth> userAuths = userAuthRepository.getListUser();
        if (userAuths.size() > 0) {
            for (UserAuth user : userAuths) {
                authentication.inMemoryAuthentication()
                        .withUser(user.getUsername())
                        .password(encoder.encode(user.getPassword()))
                        .authorities(Constant.USER_ROLE);
            }
        }
        if (userAuths.size() <= 0) {
            logger.info("Please configure the user to login to the system");
        }
        logger.info("Finished encrypting system password)");
    }

}