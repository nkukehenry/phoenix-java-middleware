package com.interswitchug.phoenix.simulator.controllers;

import com.interswitchug.phoenix.simulator.dto.ClientRegistrationDetail;
import com.interswitchug.phoenix.simulator.dto.KeyExchangeResponse;
import com.interswitchug.phoenix.simulator.dto.SystemResponse;
import com.interswitchug.phoenix.simulator.services.BaseService;
import com.interswitchug.phoenix.simulator.services.KeyExchangeService;
import com.interswitchug.phoenix.simulator.services.RegistrationService;
import com.interswitchug.phoenix.simulator.utils.AuthUtils;
import com.interswitchug.phoenix.simulator.utils.Constants;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("isw/auth")
public class AuthController {

    private RegistrationService registrationService;
    private KeyExchangeService keyExchangeService;

    public AuthController(RegistrationService registrationService, KeyExchangeService keyExchangeService) {

        this.registrationService = registrationService;
        this.keyExchangeService  = keyExchangeService;

    }

    @GetMapping("/generateKeys")
    public Map<String, String> clientRegistration() throws Exception {

        return registrationService.generateKeys();
    }

    @PostMapping("/registerClient")
    public String clientRegistration(@RequestBody ClientRegistrationDetail registrationDetail) throws Exception {
        return registrationService.doRegistration(registrationDetail);
    }

    @GetMapping("/keyExchange")
    public SystemResponse<KeyExchangeResponse> keyExchange() throws Exception {
        return keyExchangeService.doKeyExchange();
    }

    @GetMapping("/test")
    public String test() throws Exception {
        return Constants.MY_SERIAL_ID;
    }


}
