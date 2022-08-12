package com.rstn.iws.webservice.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@Data
public class Config {
    @Value("${newSubmissionDir}")
    public String newSubmissionDir;

    @Value("${additionalDir}")
    public String additionalDir;

    @Value("${fillingOnlyDir}")
    public String fillingOnlyDir;

    @Value("${serverNewSubmissionDir}")
    public String serverNewSubmissionDir;

    @Value("${serverAdditionalDir}")
    public String serverAdditionalDir;

    @Value("${serverFillingOnlyDir}")
    public String serverFillingOnlyDir;

    @Value("${user.login}")
    public String username;

    @Value("${user.password}")
    public String password;

    @Value("${user.login.server}")
    public String usernameServer;

    @Value("${user.password.login.server}")
    public String passwordServer;


}
