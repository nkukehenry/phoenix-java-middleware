package com.interswitchug.phoenix.simulator.services;

import com.interswitchug.phoenix.simulator.dto.*;
import com.interswitchug.phoenix.simulator.utils.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class RegistrationService extends  BaseService {


	/**
	 * @return Map<String,String>
	 * @throws SystemApiException
	 *
	 * This method generates for you a key pair, the private and public keys.
	 * Use the generated values to update your public key and private key in application.properties
	 */
	public Map<String,String>  generateKeys() throws SystemApiException {

	 KeyPair pair = CryptoUtils.generateKeyPair();
     String privateKey = Base64.encodeBase64String(pair.getPrivate().getEncoded());
     String publicKey =  Base64.encodeBase64String(pair.getPublic().getEncoded());
	 Map keyPair = new HashMap();
	 keyPair.put("publicKey", publicKey);
	 keyPair.put("privateKey", privateKey);

	 return  keyPair;

	}

	/**
	 * @return Map<String,String>
	 * @throws SystemApiException
	 *
	 * This method attempts to do client registration for you given the required data
	 * Make sure you generated the keys first, from the method above (generateKeys)
	 * Use the Client Secret that will be returned if  complete registration is successful
	 * as your new CLIENT_SECRET in constants
	 *
	 */
	 public String doRegistration(ClientRegistrationDetail registrationDetail) throws Exception {

	     String privateKey = Constants.PRIKEY;
         String publicKey  = Constants.PUBKEY;


		 System.out.println("privateKey IS: " + privateKey);
		 System.out.println("publicKey IS: " + publicKey);
		 System.out.println("URL"+ Constants.ROOT_LINK);

		 System.out.println(" private key {} "+ privateKey);
		 System.out.println(" public key  {} " + publicKey);
		 System.out.println(" public key  {} " + registrationDetail);
		
		EllipticCurveUtils curveUtils = new EllipticCurveUtils("ECDH");
		KeyPair keyPair = curveUtils.generateKeypair();
		String curvePrivateKey = curveUtils.getPrivateKey(keyPair);
		String curvePublicKey  = curveUtils.getPublicKey(keyPair);
		
		 String resonse = clientRegistrationRequest(publicKey,curvePublicKey,privateKey,registrationDetail);
		
		 SystemResponse<ClientRegistrationResponse> registrationResponse = UtilMethods.unMarshallSystemResponseObject(resonse,  ClientRegistrationResponse.class);

		 if(!registrationResponse.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			 //If it failed, show message
			 return  registrationResponse.getResponseMessage();
		 }
		 else {

			 //Registration was successful, extract needed values to continue to complete registration

			 String decryptedSessionKey = CryptoUtils.decryptWithPrivate(registrationResponse.getResponse().getServerSessionPublicKey(),privateKey);
			 String terminalKey = curveUtils.doECDH(curvePrivateKey,decryptedSessionKey);

			 System.out.println("==============sessionKey/terminalKey==============");
			 System.out.println("sessionKey: {} "+terminalKey);

			   String authToken =  CryptoUtils.decryptWithPrivate(registrationResponse.getResponse().getAuthToken(),privateKey);
			  // LOG.info(" authToken {} " , authToken);
			   String transactionReference = registrationResponse.getResponse().getTransactionReference();
			   String otp = "";

			   String finalResponse =  completeRegistration(terminalKey,authToken,transactionReference, otp,privateKey);
			   SystemResponse<LoginResponse> response = UtilMethods.unMarshallSystemResponseObject(finalResponse,  LoginResponse.class);

			   if(response.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {

				   //Complete registration was successful, extract returned new Secret
				   if(response.getResponse().getClientSecret() != null  && response.getResponse().getClientSecret().length() > 5) {

					   String clientSecret = CryptoUtils.decryptWithPrivate(response.getResponse().getClientSecret() ,privateKey);
					   System.out.println("New ClientSecret: " +clientSecret);
						//return the New secret
					   return "Sucessful, New Client Secret: " + clientSecret;
				   }

			   }else {
				   //FAiled
				   //LOG.info("finalResponse: {}", response.getResponseMessage());
				   return response.getResponseMessage();
			   }

			   return "Registration Failed";
		 }

	 }
	 
	 private String clientRegistrationRequest(String publicKey,String clientSessionPublicKey,String privateKey,ClientRegistrationDetail setup) throws Exception{

		 String registrationEndpointUrl = Constants.ROOT_LINK + "client/clientRegistration";

		 setup.setSerialId(Constants.MY_SERIAL_ID);
		  setup.setTerminalId(Constants.TERMINAL_ID);
		  setup.setPublicKey(publicKey);
		  setup.setGprsCoordinate("");
		  setup.setClientSessionPublicKey(clientSessionPublicKey);

		 System.out.println("Request: "+setup);

		  Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, registrationEndpointUrl,"","","",privateKey);
		  String json= JSONDataTransform.marshall(setup);
		 System.out.println("Request json: "+json);
		 return HttpUtil.postHTTPRequest( registrationEndpointUrl, headers, json);
	  }
	 
	 private String completeRegistration(String terminalKey,String authToken,String transactionReference,String otp,String privateKey) throws Exception{

		 String registrationCompletionEndpointUrl = Constants.ROOT_LINK   + "client/completeClientRegistration";

		 CompleteClientRegistration completeReg= new CompleteClientRegistration();

		  String passwordHash = UtilMethods.hash512(Constants.ACCOUNT_PWD);
		  completeReg.setTerminalId(Constants.TERMINAL_ID);
		  completeReg.setSerialId(Constants.MY_SERIAL_ID);
		  completeReg.setOtp(CryptoUtils.encrypt(otp,terminalKey));
		  completeReg.setRequestReference(java.util.UUID.randomUUID().toString());
		  completeReg.setPassword(CryptoUtils.encrypt(passwordHash,terminalKey));
		  completeReg.setTransactionReference(transactionReference);
		  completeReg.setAppVersion(Constants.APP_VERSION);
		  completeReg.setGprsCoordinate("");

		  Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, registrationCompletionEndpointUrl,
				  "",authToken,terminalKey,privateKey);
		  String json= JSONDataTransform.marshall(completeReg);

		  return HttpUtil.postHTTPRequest( registrationCompletionEndpointUrl, headers, json);
	 }


}
