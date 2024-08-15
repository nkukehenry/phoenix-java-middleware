package com.interswitchug.phoenix.api.middleware.utils;


import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.*;

@Component
public class AuthUtils {

	public static HashMap<String, String> generateInterswitchAuth(String httpMethod, String resourceUrl,
																  String additionalParameters,String authToken,String terminalKey) throws Exception {
		return generateInterswitchAuth( httpMethod,  resourceUrl,
				additionalParameters, authToken, terminalKey,"");
	}



	public static HashMap<String, String> generateInterswitchAuth(String httpMethod, String resourceUrl,
																  String additionalParameters,String authToken,String terminalKey,String privateKey) throws Exception {
		HashMap<String, String> interswitchAuth = new HashMap<>();

		TimeZone ugTimeZone = TimeZone.getTimeZone("Africa/Kampala");

		Calendar calendar = Calendar.getInstance(ugTimeZone);
		long timestamp = calendar.getTimeInMillis() / 1000;

		UUID uuid = UUID.randomUUID();
		String nonce = uuid.toString().replace("-", "");

		String clientIdBase64 =  org.apache.commons.codec.binary.Base64.encodeBase64String(Constants.CLIENT_ID.getBytes());
		String authorization = Constants.AUTHORIZATION_REALM + " " + clientIdBase64;

		resourceUrl = resourceUrl.replace("http://", "https://");//for localhost tests
		String encodedResourceUrl = URLEncoder.encode(resourceUrl, Constants.ISO_8859_1);

		String signatureCipher = httpMethod + "&" + encodedResourceUrl + "&" + timestamp + "&" + nonce + "&"
				+ Constants.CLIENT_ID + "&" + Constants.CLIENT_SECRET;

		if (additionalParameters != null && !"".equals(additionalParameters))
			signatureCipher = signatureCipher + "&" + additionalParameters;

		System.out.println("signatureCipher "+ signatureCipher);

		interswitchAuth.put(Constants.AUTHORIZATION, authorization.trim());
		interswitchAuth.put(Constants.TIMESTAMP, String.valueOf(timestamp));
		interswitchAuth.put(Constants.NONCE, nonce);

		if(privateKey.isEmpty())
			interswitchAuth.put(Constants.SIGNATURE,CryptoUtils.signWithPrivateKey(signatureCipher));
		else {
			System.out.println("signed with private "+ privateKey);
			interswitchAuth.put(Constants.SIGNATURE,CryptoUtils.signWithPrivateKey(signatureCipher,privateKey));
		}

		if(!terminalKey.isBlank())
			authToken = CryptoUtils.encrypt(authToken,terminalKey);
		else
			authToken = "";

		interswitchAuth.put(Constants.AUTH_TOKEN,authToken);

		System.out.println("terminal key "+terminalKey);
		System.out.println("OutGoing Headers");

		for (Map.Entry<String, String> entry : interswitchAuth.entrySet()) {
			System.out.println(entry.getKey() + "-" + entry.getValue());
		}

		return interswitchAuth;
	}


}
