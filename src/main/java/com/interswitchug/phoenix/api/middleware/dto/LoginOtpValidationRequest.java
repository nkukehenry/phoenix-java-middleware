package com.interswitchug.phoenix.api.middleware.dto;

public class LoginOtpValidationRequest extends ClientTerminalRequest{

	private String otp;
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
}
