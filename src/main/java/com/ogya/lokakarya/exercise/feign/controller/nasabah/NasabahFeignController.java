package com.ogya.lokakarya.exercise.feign.controller.nasabah;

import java.io.IOException;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.itextpdf.text.DocumentException;
import com.ogya.lokakarya.exercise.feign.request.nasabah.SetorFeignRequest;
import com.ogya.lokakarya.exercise.feign.request.nasabah.TarikFeignRequest;
import com.ogya.lokakarya.exercise.feign.response.nasabah.NasabahFeignResponse;
import com.ogya.lokakarya.exercise.feign.response.nasabah.NoRekeningFeignResponse;
import com.ogya.lokakarya.exercise.feign.services.nasabah.NasabahFeignService;
import com.ogya.lokakarya.service.bankadm.TransaksiNasabahService;
import com.ogya.lokakarya.util.DataResponse;
import com.ogya.lokakarya.util.DataResponseFeign;
import com.ogya.lokakarya.wrapper.bankadm.SetorAmbilWrapper;

@RestController
@RequestMapping(value = "/nasabahWebService")
@CrossOrigin(origins = "*")
public class NasabahFeignController {

	@Autowired
	private NasabahFeignService nasabahFeignService;
	@Autowired
	private TransaksiNasabahService transaksiNasabahService;
	
	@PostMapping(value = "/callSetor")
	public NasabahFeignResponse callSetor(@RequestBody SetorFeignRequest request) {
		NasabahFeignResponse response = nasabahFeignService.callSetor(request);
		return response;
	}
	
	@PostMapping(value = "/callTarik")
	public NasabahFeignResponse callTarik(@RequestBody TarikFeignRequest request) {
		NasabahFeignResponse response = nasabahFeignService.callTarik(request);
		return response;
	}
	
	@GetMapping(value = "/cekNoRekening/{norek}")
	public NoRekeningFeignResponse cekNoRekening(@RequestParam("norek") String noRekening) {
		NoRekeningFeignResponse response = nasabahFeignService.cekNoRekening(noRekening);
		return response;
	}
	
	@PostMapping(value = "/setorFeign")
	public DataResponseFeign<SetorAmbilWrapper> setorFeign(@RequestParam("no-rekening") Long noRekening, @RequestParam("nominal") Long nominal) {
		return transaksiNasabahService.setorFeign(noRekening, nominal);
	}
	
	@PostMapping(value = "/tarikFeign")
	public DataResponseFeign<SetorAmbilWrapper> tarikFeign(@RequestParam("no-rekening") Long noRekening, @RequestParam("nominal") Long nominal) {
		return transaksiNasabahService.tarikFeign(noRekening, nominal);
	}
	
	@PostMapping(path = "/emailSetor")
	public DataResponse<SetorAmbilWrapper> emailSetor(@RequestParam("no-rekening") Long noRekening,
			@RequestParam("Nominal") Long nominal) throws MessagingException, IOException, DocumentException, Exception {
		return new DataResponse<SetorAmbilWrapper>(transaksiNasabahService.sendBuktiSetor(noRekening, nominal));
	}
}
