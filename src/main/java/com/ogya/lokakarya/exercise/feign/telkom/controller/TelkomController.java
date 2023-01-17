package com.ogya.lokakarya.exercise.feign.telkom.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ogya.lokakarya.exercise.feign.telkom.request.BayarRequest;
import com.ogya.lokakarya.exercise.feign.telkom.response.BayarResponse;
import com.ogya.lokakarya.exercise.feign.telkom.services.TelkomFeignServices;

@RestController
@RequestMapping(value = "/telkom")
@CrossOrigin(origins = "*")
public class TelkomController {
	@Autowired
	TelkomFeignServices telkomServices;
	@PostMapping(value = "/bayar-exercize")
	public BayarResponse callLaporan(@RequestBody(required = true) BayarRequest request) {
		BayarResponse bayar = telkomServices.callBayarTelkom(request);
		return bayar;
	}
}