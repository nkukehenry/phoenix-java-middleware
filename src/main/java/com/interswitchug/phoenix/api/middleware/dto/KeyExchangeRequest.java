package com.interswitchug.phoenix.api.middleware.dto;

public class KeyExchangeRequest extends ClientTerminalRequest {
	protected String password;
	private String clientSessionPublicKey;
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getClientSessionPublicKey() {
		return clientSessionPublicKey;
	}
	public void setClientSessionPublicKey(String clientSessionPublicKey) {
		this.clientSessionPublicKey = clientSessionPublicKey;
	}
	
	
}
