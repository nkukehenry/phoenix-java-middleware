package com.interswitchug.phoenix.simulator.config;


import com.interswitchug.phoenix.simulator.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {


    @Value("${app.terminal_id}")
    public  String terminalId;
    @Value("${app.client_id}")
    public   String clientId;
    @Value("${app.client_secret}")
    public  String clientSecret;
    @Value("${app.password}")
    public  String password;
    @Value("${app.serial_id}")
    public  String serialId;
    @Value("${app.public_key}")
    public  String publicKey;
    @Value("${app.private_key}")
    public  String privateKey;
    @Value("${app.base_url}")
    public  String baseUrl;
    @Value("${app.version}")
    public  String appVersion;


    @Bean
    public Constants getServiceConstants(){

        Constants constants = new Constants();

        constants.CLIENT_SECRET = clientSecret;
        constants.CLIENT_ID = clientId;
        constants.ROOT_LINK = baseUrl;
        constants.SANDBOX_ROUTE = baseUrl;
        constants.PRIKEY = privateKey;
        constants.PUBKEY = publicKey;
        constants.TERMINAL_ID=terminalId;
        constants.ACCOUNT_PWD = password;
        constants.APP_VERSION = appVersion;
        constants.MY_SERIAL_ID = serialId;

        return constants;
    }
}
