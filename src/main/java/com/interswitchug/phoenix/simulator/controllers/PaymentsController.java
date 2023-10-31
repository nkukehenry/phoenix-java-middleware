package com.interswitchug.phoenix.simulator.controllers;


import com.interswitchug.phoenix.simulator.dto.PaymentRequest;
import com.interswitchug.phoenix.simulator.services.PaymentsService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("isw/payments")
public class PaymentsController {

    private PaymentsService paymentsService;

    public PaymentsController(PaymentsService paymentsService) {
        this.paymentsService = paymentsService;
    }

    @PostMapping("/validation")
    public String validateCustomer(@RequestBody PaymentRequest request) throws Exception {

        return paymentsService.validateCustomer(request);
    }

    @PostMapping("/pay")
    public String doPayment(@RequestBody PaymentRequest registrationDetail) throws Exception {

        return paymentsService.makePayment(registrationDetail);
    }

}
