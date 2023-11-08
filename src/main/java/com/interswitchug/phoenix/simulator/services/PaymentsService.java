package com.interswitchug.phoenix.simulator.services;

import com.interswitchug.phoenix.simulator.dto.*;
import com.interswitchug.phoenix.simulator.utils.AuthUtils;
import com.interswitchug.phoenix.simulator.utils.Constants;
import com.interswitchug.phoenix.simulator.utils.HttpUtil;
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

			String jsonString = JSONDataTransform.marshall(request);

			return HttpUtil.postHTTPRequest(endpointUrl, headers, jsonString);
		}
		else{
			return "Cannot Continue with inquiry,Key Exchange failed";
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

			return "Cannot Continue with payment,Key Exchange failed";
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
				return "Cannot Continue with payment,Key Exchange failed";
			}
	}
}
