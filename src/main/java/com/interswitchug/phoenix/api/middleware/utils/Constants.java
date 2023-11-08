package com.interswitchug.phoenix.api.middleware.utils;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Constants {

	public static String PRIKEY="";
    public static String PUBKEY="";
    public static String TERMINAL_ID="";
    public static  String CLIENT_ID =  "";
    public static  String CLIENT_SECRET = "";
	public static String MY_SERIAL_ID="";
	public static String ACCOUNT_PWD="";
    public static  String APP_VERSION="";
    public static  String SANDBOX_ROUTE="";
    public static  String ROOT_LINK= SANDBOX_ROUTE;
	public static final String TIMESTAMP = "Timestamp";
    public static final String NONCE = "Nonce";
    public static final String SIGNATURE = "Signature";
    public static final String AUTHORIZATION = "Authorization";
    public static final String AUTHORIZATION_REALM = "InterswitchAuth";
    public static final String ISO_8859_1 = "ISO-8859-1";
    public static final String AUTH_TOKEN="AuthToken";
    public static String POST_REQUEST = "POST";
    public static String GET_REQUEST  = "GET";
    public static final String SIGNATURE_METHOD_VALUE="SHA-256";
    public static final String AES_CBC_PKCS7_PADDING = "AES/CBC/PKCS7Padding";
    public static final String RSA_NONE_OAEPWithSHA256AndMGF1Padding = "RSA/NONE/OAEPWithSHA256AndMGF1Padding";

}

