package com.interswitchug.phoenix.simulator.utils;

import com.interswitchug.phoenix.simulator.dto.PhoenixResponseCodes;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.bouncycastle.util.encoders.Hex;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.security.*;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;

public class CryptoUtils {

	public static String encrypt(String plaintext, String terminalKey) throws SystemApiException {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			byte[] message = plaintext.getBytes(StandardCharsets.UTF_8);
			byte[] iv = Hex.decode(UtilMethods.randomBytesHexEncoded(16));
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
			Cipher cipher = Cipher.getInstance(Constants.AES_CBC_PKCS7_PADDING, "BC");
			byte[] keyBytes = Base64.decodeBase64(terminalKey);
			SecretKey secretKey = new SecretKeySpec(keyBytes, Constants.AES_CBC_PKCS7_PADDING);
			cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivParameterSpec);
			byte[] secret = cipher.doFinal(message);
			ByteArrayOutputStream b = new ByteArrayOutputStream(); // concatnate IV with cipher text
			b.write(iv);
			b.write(secret);
			return Base64.encodeBase64String(b.toByteArray());
		} catch (Exception e) {
			e.printStackTrace();
		//	LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to encrypt object");
		}
	}

	public static String decrypt(byte[] encryptedValue, String terminalKey) throws SystemApiException {
		try {
			byte[] secretKeyBytes = Base64.decodeBase64(terminalKey);
			SecretKey secretKey = new SecretKeySpec(secretKeyBytes, Constants.AES_CBC_PKCS7_PADDING);
			byte[] iVbytes = Arrays.copyOfRange(encryptedValue, 0, 16);// remove the iv
			encryptedValue = Arrays.copyOfRange(encryptedValue, 16, encryptedValue.length);
			IvParameterSpec ivParameterSpec = new IvParameterSpec(iVbytes);
			Cipher cipher = Cipher.getInstance(Constants.AES_CBC_PKCS7_PADDING, "BC");
			cipher.init(Cipher.DECRYPT_MODE, secretKey, ivParameterSpec);
			byte[] clear = cipher.doFinal(encryptedValue);
			return new String(clear);
		} catch (Exception e) {
			e.printStackTrace();
		//	LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to decrypt object");
		}
	}

	public static String decryptWithPrivate(String plaintext) throws SystemApiException {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			byte[] message = Base64.decodeBase64(plaintext);
			Cipher cipher = Cipher.getInstance(Constants.RSA_NONE_OAEPWithSHA256AndMGF1Padding, "BC");
			cipher.init(Cipher.DECRYPT_MODE, getRSAPrivate());
			byte[] secret = cipher.doFinal(message);
			return new String(secret);
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to decryptWithPrivate ");
		}
	}

	//	/https://stackoverflow.com/questions/56489992/how-do-i-use-bouncy-castle-in-net-to-encrypt-using-rsa-ecb-oaepwithsha256andmgf
	public static String decryptWithPrivate(byte[] message, PrivateKey privateKey) throws SystemApiException {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			Cipher cipher = Cipher.getInstance(Constants.RSA_NONE_OAEPWithSHA256AndMGF1Padding, "BC");

			OAEPParameterSpec parameterSpec = new OAEPParameterSpec("SHA-256", "MGF1", MGF1ParameterSpec.SHA256,
					PSource.PSpecified.DEFAULT);

			cipher.init(Cipher.DECRYPT_MODE, privateKey,parameterSpec);
			byte[] secret = cipher.doFinal(message);
			return new String(secret);
		} catch (Exception e) {
			e.printStackTrace();
		//	LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to decryptWithPrivate ");
		}
	}

	public static void main(String[] args) throws SystemApiException {
		String plaintext = "ZbqQ9WRrFa/Rzyac35fIdQ8vIMUZ8spJQpEslGazDGXLEMgiuJmTP0+9CSJ08CwpJywvXWfvrQwZXBakpLb/Ujz0s2X91AWA/XAFDIwRqnZMoIPkrzaOMrsO1nsCqWOEg2yqoak7LVIqrS/54sVJBC6S0RF6OIkZg6AzsydqNjJBcrHDD+PzCz+ixSN+ezCZZZdt/rNcI6JGuKYhxG1c9G2WAV6Fs7HsFxxsk30P4vfpr1kf1JasoWxO1WtECm2xpy2Hume6n31Soyz/QCwPhzWvJcTHZNT/fDo8MxUeRzAf/Vs0w9L+AKf482aGOUTSEbaThlqNz2ApKbD07p6fSg==";
		String privateKey = "MIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCVk9Ks6yvpUd1w0NXB/BJD3W6kbr95wiv8GscdjW5ETTbNo9ezXBHbiWZ2cqFK/NIpHE5MQhAlhcFJ/OF6Wh2Mwc2Q/fSFXoL3sWuXPBdw77QpzIcLsujNM0XOuwyPIgSSorBSz99sskVvzS6k3GtSgngmM8Fkh4fzq0afG6UUGL6kHrSsMI7aEXE7tgFWlaHnyFwWGA5f4MYN2xTutSxw1KM01W8feWAUXJ9hTXsV+ZZ+6TPJ/ECZuH8h66j/mXFoYHVzIAkZv4h4btrYvAYSapPSUDmTJd0wU2u6sEyF6sXZYwYHrLKit7OZh/K7v9+OLX62L5NaUeB1c2UPowhpAgMBAAECggEADR48HCWP4f7nsII64lrCG1OV5Yk/n7poyHZsv2nCTEPdVOb+qYtJ7FATkrsg7DEExCLoYbBq4W+RRnTi2CvZFZmATCwJlZIhYv8baYBqqeFL6AfjpsytCs9pWuUgB9mQPxqnjiEO/jsa8zWH1rZCGf1j84MXKJg+Ux99VReZQGF8lglFwlKaczolWOCDhugzK+pdUYLnGZdTf0Oinch95W0jt4YeGu/lZF/Tl9VsNnk5lvVIrUJnMC5gIIvF3cdifYLwh8JOoBtJnHzV2/5vCUMisC7CtyRun15u2FZvgkbUSEUeb0Ih6hrxE1B8vLwlQjj6SXGqbpDLAZxxjvppZQKBgQDd9XruaQGwJv6TYS3sVx/xBr5/AZMQivIZKg9VWfSSZPy1GpF5d+4F/bh/r7IugU7oSYB1aVC3pe+RjCtBUgcHtJbSbtDIq5kCFsLwBRic+BfLRJhOoDgTIE3dAkpk3lDmOhqnNPjpsn2lj6GO5WyYRIVoUJeRPUT4vYaAN4gAwwKBgQCshILpx1ikvvRBzMow2KIX9/9ISwLz3M/OKhqABNqvyQwJCNP8+K5UqEqZqK7hNvcUDFWQMgDtc98BjqExpDjkBzEKoT7UPU8c/tj2Fon2Tr00wZ30ohy62oqxO+lg4AwyegeQM26uXgJMPAqkofs9BvSCjOjiG9Z7Ufu/cQ5/YwKBgFcELy6acjP6BaIH3jYirf4TM8mc92fr4R/mcl52xsBB4SSrBs9FKkalApjka4l53lKIxljBcgidgD9iYW7ZVrI5pUtYcHfLdk/wVno8tTqMtN9WxBNHEtTEvGHhzQKgebzYmKxmwxLzi/jFR4Q7A4jqjBaqSdGB4LFXtAw+OQ57AoGAcTB0EFyJzXW4Ht/1nPzVxTIinVUR99xdv5+iSXJl5UXcjqTrKaQkBUlxwMWLEBsXG2FIVlZog2Mt4wCoxEBCT2Jki9vltC0Rli2jPjrlg+R/gXizYUln3jT7IHP1mGkHt6qXAcatDNyDV29hcEkS5SSGEo3PUmL/Y2QT77SG1Y8CgYB7agTKWOVsgrhySCQQKKo74Lw3LnFdZYwEobEPxIxnezuiV6Mv2Os9uWRfOod/BBJR7QAJLvtAaQRKPF3MVzxW0oAUT1pzMAAmWO8RQxajlZmvO62b2f8Q/2JUX6+lrtL/Subja7z73snnrLVrwJVJuAgzcngjJ0C9nAbU5F0Ycg==";
		String rr = decryptWithPrivate(plaintext,  privateKey);
		System.out.println(rr);
	}
	public static String decryptWithPrivate(String plaintext, String privateKey) throws SystemApiException {
		byte[] message = Base64.decodeBase64(plaintext);
		return decryptWithPrivate(message, getRSAPrivate(privateKey));
	}

	public static String encryptWithPrivate(String plaintext) throws SystemApiException {
		try {
			Security.addProvider(new org.bouncycastle.jce.provider.BouncyCastleProvider());
			byte[] message = plaintext.getBytes(StandardCharsets.UTF_8);
			Cipher cipher = Cipher.getInstance(Constants.RSA_NONE_OAEPWithSHA256AndMGF1Padding, "BC");
			cipher.init(Cipher.ENCRYPT_MODE, getRSAPrivate());
			byte[] secret = cipher.doFinal(message);
			return Base64.encodeBase64String(secret);
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to encryptWithPrivate ");
		}
	}

	public static PrivateKey getRSAPrivate() throws SystemApiException {
		return getRSAPrivate(Constants.PRIKEY.trim());
	}

	public static PrivateKey getRSAPrivate(String privateKey) throws SystemApiException {
		try {
			byte[] keyBytes = Base64.decodeBase64(privateKey.trim().getBytes());
			PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(keyBytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(spec);
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to getRSAPrivate ");
		}
	}

	public static String signWithPrivateKey(String data) throws SystemApiException {
		return signWithPrivateKey(data, getRSAPrivate());
	}

	public static String signWithPrivateKey(String data, String privateKey) throws SystemApiException {
		return signWithPrivateKey(data, getRSAPrivate(privateKey));
	}

	public static String signWithPrivateKey(String data, PrivateKey privateKey) throws SystemApiException {
		try {
			if (data.equals(""))
				return "";
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initSign(privateKey);
			sign.update(data.getBytes(StandardCharsets.UTF_8));
			return new String(Base64.encodeBase64(sign.sign()), StandardCharsets.UTF_8);
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to signWithPrivateKey ");
		}
	}

	public static boolean verifySignature(String signature, String message) throws SystemApiException {
		RSAPublicKey pubKey = getPublicKey(Constants.PUBKEY);
		return verifySignature(signature, message, pubKey);
	}

	public static boolean verifySignature(String signature, String message, String publicKey)
			throws SystemApiException {
		RSAPublicKey pubKey = getPublicKey(publicKey);
		return verifySignature(signature, message, pubKey);
	}

	public static boolean verifySignature(String signature, String message, RSAPublicKey pubKey)
			throws SystemApiException {
		try {
			Signature sign = Signature.getInstance("SHA256withRSA");
			sign.initVerify(pubKey);
			sign.update(message.getBytes(StandardCharsets.UTF_8));
			return sign.verify(Base64.decodeBase64(signature.getBytes(StandardCharsets.UTF_8)));
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to verifySignature ");
		}
	}

	public static KeyPair generateKeyPair() throws SystemApiException {
		try {
			KeyPairGenerator kpg = KeyPairGenerator.getInstance("RSA");
			kpg.initialize(2048);
			return kpg.generateKeyPair();
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to generateKeyPair ");
		}
	}

	public static RSAPublicKey getPublicKey(String publicKeyContent) throws SystemApiException {
		try {
			KeyFactory kf = KeyFactory.getInstance("RSA");
			X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyContent));
			return (RSAPublicKey) kf.generatePublic(keySpecX509);
		} catch (Exception e) {
			e.printStackTrace();
			//LOG.error("Exception trace {} ", ExceptionUtils.getStackTrace(e));
			throw new SystemApiException(PhoenixResponseCodes.INTERNAL_ERROR.CODE, "Failure to getPublicKey ");
		}
	}
}
