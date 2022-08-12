package com.rstn.iws.webservice.config;

import com.rstn.iws.webservice.constant.Constant;
import org.apache.commons.lang.StringUtils;
import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.PooledPBEStringEncryptor;
import org.jasypt.encryption.pbe.config.SimpleStringPBEConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JASYPTConfig {

    @Bean(name = "encryptorBean")
    public StringEncryptor stringEncryptor() {
        String jasyptPass = System.getenv(Constant.IWS_CAPTURE_PWD_SECRET);
        if (StringUtils.isEmpty(jasyptPass)) {
            jasyptPass = Constant.JASYPT_PASS;
        }
        PooledPBEStringEncryptor encryptor = new PooledPBEStringEncryptor();
        SimpleStringPBEConfig config = new SimpleStringPBEConfig();
        config.setPassword(jasyptPass);
        config.setAlgorithm(Constant.JASYPT_ALGORITHM);
        config.setKeyObtentionIterations("1000");
        config.setPoolSize("1");
        config.setProviderName(Constant.JASYPT_NAME_PROVIDER);
        config.setSaltGeneratorClassName(Constant.JASYPT_GENERATORCLASSNAME);
        config.setStringOutputType(Constant.JASYPT_TYPE);
        encryptor.setConfig(config);
        return encryptor;
    }
}
