package com.interswitchug.phoenix.api.middleware.utils;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.interswitchug.phoenix.api.middleware.dto.PhoenixResponseCodes;
import com.interswitchug.phoenix.api.middleware.dto.SystemResponse;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

public class UtilMethods {
    public static String hash512(String plainText) throws SystemApiException {
    	try {
	        MessageDigest digest = MessageDigest.getInstance("SHA-512");
	        byte[] hash = digest.digest(plainText.trim().getBytes(StandardCharsets.UTF_8));
	        return Base64.getEncoder().encodeToString(hash);
    	}catch(Exception e) {
    		e.printStackTrace();
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE , "Failure to hash512 object");
		}
    }
    
    public static <T> SystemResponse<T> unMarshallSystemResponseObject(String response,Class<T> theClass) throws SystemApiException{
		 try
			{
				ObjectMapper mapper = new ObjectMapper();
				JavaType type = mapper.getTypeFactory().constructParametricType(SystemResponse.class, theClass);
				return  mapper.readValue(response, type);
			}catch(Exception e) {
				e.printStackTrace();
				throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE , "Failure to unmarshall json string from systemresponse object");
			}

	 }
    
    public static String randomBytesHexEncoded(int count) {
		try {
			SecureRandom sr = SecureRandom.getInstance("SHA1PRNG");
			byte[] bytes = new byte[count];
	        sr.nextBytes(bytes);
			return Hex.toHexString(bytes);
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
			return null;
		}
	}

	
	public static String isEmptyString(String str,String defaultReturn)
	{
	     return (isEmptyString(str))?defaultReturn:str;
	}
	
	public static boolean isEmptyString(String str)
	{
	    return (Objects.isNull(str)) || (str == null) || (str.equalsIgnoreCase("")) || (str.equalsIgnoreCase("null"));
	}
}
