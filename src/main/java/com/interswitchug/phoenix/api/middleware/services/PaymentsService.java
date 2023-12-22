package com.interswitchug.phoenix.api.middleware.services;

import com.interswitchug.phoenix.api.middleware.dto.*;
import com.interswitchug.phoenix.api.middleware.utils.AuthUtils;
import com.interswitchug.phoenix.api.middleware.utils.Constants;
import com.interswitchug.phoenix.api.middleware.utils.CryptoUtils;
import com.interswitchug.phoenix.api.middleware.utils.HttpUtil;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class PaymentsService {


	private KeyExchangeService keyExchangeService;
	public PaymentsService(KeyExchangeService keyExchangeService) {

		this.keyExchangeService = keyExchangeService;
	}

	public String validateCustomer(PaymentRequest request) throws Exception {

		String endpointUrl =  Constants.ROOT_LINK + "sente/customerValidation";
		request.setTerminalId(Constants.TERMINAL_ID);

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, endpointUrl, "",
					exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());

			if(request.getOtp() != null)
				request.setOtp(CryptoUtils.encrypt(request.getOtp(),exchangeKeys.getResponse().getTerminalKey()));

			String jsonString = JSONDataTransform.marshall(request);
			return HttpUtil.postHTTPRequest(endpointUrl, headers, jsonString);
		}
		else{
			return JSONDataTransform.marshall(exchangeKeys);
		}

	}

	public String makePayment(PaymentRequest request) throws Exception {

		String endpointUrl = Constants.ROOT_LINK + "sente/xpayment";

        request.setTerminalId(Constants.TERMINAL_ID);
		String additionalData = request.getAmount()+"&"
		+request.getTerminalId()+"&"
				+request.getRequestReference()+"&"
		+ request.getCustomerId()+"&" +request.getPaymentCode();

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {

			String authToken   = exchangeKeys.getResponse().getAuthToken();
			String sessionKey  = exchangeKeys.getResponse().getTerminalKey();

			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.POST_REQUEST, endpointUrl, additionalData,
					authToken,sessionKey);

			return  HttpUtil.postHTTPRequest(endpointUrl, headers, JSONDataTransform.marshall(request));
		}
		else{
			return JSONDataTransform.marshall(exchangeKeys);
		}

	}

	public String fetchBalance() throws Exception {

		    String endpointUrl =  Constants.ROOT_LINK +  "sente/accountBalance";
			String request = endpointUrl +"?terminalId="+ Constants.TERMINAL_ID + "&requestReference="+java.util.UUID.randomUUID();

			SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

			if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
				Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.GET_REQUEST, request, "",exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());
				return HttpUtil.getHTTPRequest(request, headers);
			}
			else {
				return "Cannot Continue with balance Check,Key Exchange failed";
			}
	}

	public String checkStatus(String requestReference) throws Exception {

		String endpointUrl =  Constants.ROOT_LINK +  "sente/status";
		String request = endpointUrl +"?terminalId="+ Constants.TERMINAL_ID + "&requestReference="+requestReference;

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.GET_REQUEST, request, "",exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());
			return HttpUtil.getHTTPRequest(request, headers);
		}
		else {
			return "Cannot Continue with balance Check,Key Exchange failed";
		}
	}

	public String getCategories() throws Exception {

		String endpointUrl =  Constants.BILLERS_ROOT +  "categories-by-client/"+Constants.TERMINAL_ID+"/"+ Constants.TERMINAL_ID;

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.GET_REQUEST, endpointUrl, "",exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());
			return HttpUtil.getHTTPRequest(endpointUrl, headers);
		}
		else {
			return "Cannot Fetch Categories,Key Exchange failed";
		}
	}

	public String getCategoryBillers(String categoryId) throws Exception {

		String endpointUrl =  Constants.BILLERS_ROOT +  "biller-by-category/"+categoryId;

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.GET_REQUEST, endpointUrl, "",exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());
			return HttpUtil.getHTTPRequest(endpointUrl, headers);
		}
		else {
			return "Cannot Fetch Billers,Key Exchange failed";
		}
	}

	public String getBillerItems(String billerId) throws Exception {

		String endpointUrl =  Constants.BILLERS_ROOT +  "items/biller-id/"+billerId;

		SystemResponse<KeyExchangeResponse> exchangeKeys = keyExchangeService.doKeyExchange();

		if(exchangeKeys.getResponseCode().equals(PhoenixResponseCodes.APPROVED.CODE)) {
			Map<String,String> headers = AuthUtils.generateInterswitchAuth(Constants.GET_REQUEST, endpointUrl, "",exchangeKeys.getResponse().getAuthToken(),exchangeKeys.getResponse().getTerminalKey());
			return HttpUtil.getHTTPRequest(endpointUrl, headers);
		}
		else {
			return "Cannot Fetch Biller Items,Key Exchange failed";
		}
	}
}
