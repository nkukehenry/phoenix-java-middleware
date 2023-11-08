package com.interswitchug.phoenix.api.middleware.services;

import com.interswitchug.phoenix.api.middleware.dto.*;
import com.interswitchug.phoenix.api.middleware.utils.*;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Map;

@Service
public class KeyExchangeService {


	public SystemResponse<KeyExchangeResponse> doKeyExchange() throws Exception {

		String endpointUrl = Constants.ROOT_LINK + "client/doKeyExchange";

		EllipticCurveUtils curveUtils = new EllipticCurveUtils("ECDH");
		KeyPair pair = curveUtils.generateKeypair();
		String privateKey = curveUtils.getPrivateKey(pair);
		String publicKey = curveUtils.getPublicKey(pair);

		KeyExchangeRequest request = new KeyExchangeRequest();
		request.setTerminalId(Constants.TERMINAL_ID);
		request.setSerialId(Constants.MY_SERIAL_ID);
		request.setRequestReference(java.util.UUID.randomUUID().toString());
		request.setAppVersion(Constants.APP_VERSION);
		String passwordHash = UtilMethods.hash512(Constants.ACCOUNT_PWD) + request.getRequestReference()
				+ Constants.MY_SERIAL_ID;
		request.setPassword(CryptoUtils.signWithPrivateKey(passwordHash));
		request.setClientSessionPublicKey(publicKey);

		Map<String, String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, endpointUrl, "", "",
				"");
		String json = JSONDataTransform.marshall(request);

		String response = HttpUtil.postHTTPRequest(endpointUrl, headers, json);
		SystemResponse<KeyExchangeResponse> keyxchangeResponse = UtilMethods.unMarshallSystemResponseObject(response,
				KeyExchangeResponse.class);
		if (keyxchangeResponse.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			String clearServerSessionKey = CryptoUtils
					.decryptWithPrivate(keyxchangeResponse.getResponse().getServerSessionPublicKey());
			String terminalkey = new EllipticCurveUtils("ECDH").doECDH(privateKey,clearServerSessionKey);
			keyxchangeResponse.getResponse().setTerminalKey(terminalkey);

			if (! keyxchangeResponse.getResponse().getAuthToken().isEmpty())
				keyxchangeResponse.getResponse()
						.setAuthToken(CryptoUtils.decryptWithPrivate(keyxchangeResponse.getResponse().getAuthToken()));

			return keyxchangeResponse;
		} else {
			keyxchangeResponse.setResponseMessage(keyxchangeResponse.getResponseMessage() + " during Key Exchange");
			return keyxchangeResponse;
		}
	}

}
