package com.interswitchug.phoenix.simulator.config;


import com.interswitchug.phoenix.simulator.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Config {
    @Value("${app.terminal_id}")
    public  String terminalId;
    @Value("${app.client_id}")
    public  String clientId;
    @Value("${app.client_secret}")
    public  String clientSecret;
    @Value("${app.password}")
    public  String password;
    @Value("${app.serial_id}")
    public  String serialId;
    @Value("${app.public_key}")
    public  String publicKey;
    @Value("${app.privateKey}")
    public  String privateKey;
    @Value("${app.base_url}")
    public  String baseUrl;
    @Value("${app.version}")
    public  String appVersion;

    public Config() {

        Constants.CLIENT_SECRET = clientSecret;
        Constants.CLIENT_ID = clientId;
        Constants.ROOT_LINK = baseUrl;
        Constants.SANDBOX_ROUTE = baseUrl;
        Constants.PRIKEY = privateKey;
        Constants.PUBKEY = publicKey;
        Constants.TERMINAL_ID=terminalId;
        Constants.ACCOUNT_PWD = password;
        Constants.APP_VERSION = appVersion;
        Constants.MY_SERIAL_ID = serialId;

    }

}
