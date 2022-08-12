package com.rstn.iws.webservice.repository;

import com.rstn.iws.webservice.config.Config;
import com.rstn.iws.webservice.model.UserAuth;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserAuthRepository {
    private static final Logger logger = LogManager.getLogger(CaptureRepository.class);

    @Autowired
    private Config config;

    public List<UserAuth> getListUser() {
        logger.info("Start get list user login.");
        List<UserAuth> userAuths = new ArrayList<>();
        try {
            String[] users = config.getUsername().split(";");
            logger.info("Size list user login: " + users.length);
            String[] password = config.getPassword().split(";");
            if (password.length > 0 && users.length > 0 && !users[0].isEmpty() && !password[0].isEmpty()) {
                for (int i = 0; i < users.length; i++) {
                    for (int j = 0; j < password.length; j++) {
                        if (i == j) {
                            UserAuth userAuth = new UserAuth();
                            userAuth.setUsername(users[i]);
                            userAuth.setPassword(password[j]);
                            userAuths.add(userAuth);
                        }
                    }
                }
            }
            if (users[0].isEmpty() || password[0].isEmpty()) {
                logger.info("UserPassword isEmpty.Please configure the UserPassword to login to the system");
            }
            logger.info("Get list user login Success with size users: " + userAuths.size());
            return userAuths;
        } catch (Exception e) {
            logger.info("Get list user login Not Success with size users: " + userAuths.size());
            e.printStackTrace();
            return userAuths;
        }
    }
}
