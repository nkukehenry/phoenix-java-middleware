package com.interswitchug.phoenix.api.middleware;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.security.Security;

@SpringBootApplication
public class PhoenixSimulatorApplication {

	public static void main(String[] args) {

		Security.addProvider(new BouncyCastleProvider());

		SpringApplication.run(PhoenixSimulatorApplication.class, args);
	}

}
