package com.ogya.lokakarya;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

import com.ogya.lokakarya.bankadm.service.TransaksiNasabahService;
import com.ogya.lokakarya.exercise.feign.nasabah.request.SetorFeignRequest;
import com.ogya.lokakarya.exercise.feign.nasabah.request.TarikFeignRequest;
import com.ogya.lokakarya.exercise.feign.nasabah.response.NasabahFeignResponse;
import com.ogya.lokakarya.exercise.feign.nasabah.response.NoRekeningFeignResponse;
import com.ogya.lokakarya.exercise.feign.nasabah.services.NasabahFeignService;
import com.ogya.lokakarya.exercise.feign.transfer.request.TransferFeignRequest;
import com.ogya.lokakarya.exercise.feign.transfer.response.TransferFeignResponse;
import com.ogya.lokakarya.exercise.feign.transfer.response.ValidateRekeningFeignResponse;
import com.ogya.lokakarya.exercise.feign.transfer.services.TransferFeignService;
import com.ogya.lokakarya.usermanagement.feign.request.UsersFeignRequest;
import com.ogya.lokakarya.usermanagement.feign.response.UsersFeignResponse;
import com.ogya.lokakarya.usermanagement.feign.services.UsersFeignServices;

@EnableFeignClients
@SpringBootApplication
public class LokakaryaApplication implements CommandLineRunner {
	@Autowired
	UsersFeignServices usersFeignServices;
	@Autowired
	TransferFeignService transferService;
	@Autowired
	TransaksiNasabahService transaksiNasabahService;
	
	@Autowired
	private NasabahFeignService nasabahFeignService;

	public static void main(String[] args) {
		SpringApplication.run(LokakaryaApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		String noTelepon = "0857";
		String noRekeningPengirim = "1234";
		String nama = "Irzan Maulana";
		String email = "maulanairzan5@gmail.com";
		String noRekPenerima = "99999";
		Long nominal = 200000L;

		UsersFeignRequest request = new UsersFeignRequest();
		request.setAlamat(noTelepon);
		request.setEmail(email);
		request.setNama(nama);
		request.setTelpon(noRekeningPengirim);

		System.out.println("");
		System.out.println("Call user role record");
		UsersFeignResponse usersFeignResponse = usersFeignServices.callUserRoleRecord(request);
		System.out.println("Program Name : " + usersFeignResponse.getProgramName());
		System.out.println("Success : " + usersFeignResponse.getSuccess());

		System.out.println("");
		TransferFeignRequest transferRequest = new TransferFeignRequest();
		transferRequest.setJumlahTranfer((long) 11111);
		transferRequest.setNoRekeningPenerima(noRekPenerima);
		transferRequest.setNoRekeningPengirim(noRekeningPengirim);
		System.out.println("Call transfer");

		ValidateRekeningFeignResponse rekValidatePengirim = transferService.callValidateRekening(noRekeningPengirim);
		ValidateRekeningFeignResponse rekValidatePenerima = transferService.callValidateRekening(noRekPenerima);
		if (rekValidatePengirim.getRegistered() == true && rekValidatePenerima.getRegistered() == true) {
			TransferFeignResponse transferResponse = transferService.callTransfer(transferRequest);
			System.out.println("Reference Number : " + transferResponse.getReferenceNumber());
			System.out.println("Success : " + transferResponse.getSuccess());
		}
		
		System.out.println(" ");
		System.out.println("=========================");
		System.out.println("CEK NO REKENING");
		NoRekeningFeignResponse validatedNoRekening = nasabahFeignService.cekNoRekening(noRekeningPengirim);
		System.out.println("Registered?: "+validatedNoRekening.getRegistered());
		System.out.println(" ");
		System.out.println("=========================");
		System.out.println("SETOR");
		SetorFeignRequest setorReq = new SetorFeignRequest();
		setorReq.setNoRekening(noRekeningPengirim);
		setorReq.setSetoran(nominal);
		if (validatedNoRekening.getRegistered() == true) {
			NasabahFeignResponse setorRespon = nasabahFeignService.callSetor(setorReq);
			System.out.println("Status: "+setorRespon.getSuccess());
			System.out.println("No Referensi: "+setorRespon.getReferenceNumber());
		} else {
			System.out.println("No Rekening tidak valid.");
		}
		System.out.println(" ");
		System.out.println("=========================");
		System.out.println("TARIK");
		TarikFeignRequest tarikReq = new TarikFeignRequest();
		tarikReq.setNoRekening(noRekeningPengirim);
		tarikReq.setTarikan(nominal);
		if (validatedNoRekening.getRegistered() == true) {
			NasabahFeignResponse tarikRespon = nasabahFeignService.callTarik(tarikReq);
			System.out.println("Status: "+tarikRespon.getSuccess());
			System.out.println("No Referensi: "+tarikRespon.getReferenceNumber());
		} else {
			System.out.println("No Rekening tidak valid.");
		}
		System.out.println(" ");
	}

}
