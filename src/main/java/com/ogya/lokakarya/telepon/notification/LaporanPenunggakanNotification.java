package com.ogya.lokakarya.telepon.notification;

import java.io.ByteArrayOutputStream;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;



import com.ogya.lokakarya.telepon.entity.TransaksiTelkom;
import com.ogya.lokakarya.telepon.helper.LaporanPenunggakanExcelExporter;
import com.ogya.lokakarya.telepon.repository.MasterPelangganRepository;
import com.ogya.lokakarya.telepon.repository.TransaksiTelkomRepository;
import com.ogya.lokakarya.telepon.service.TransaksiTelkomService;
import com.ogya.lokakarya.telepon.wrapper.TransaksiTelkomWrapper;


@Service
@Transactional
public class LaporanPenunggakanNotification {
	@Autowired
	MasterPelangganRepository masterPelangganRepository;
	@Autowired
	TransaksiTelkomRepository transaksiTelkomRepository;
	@Autowired
	private JavaMailSender javaMailSender;
	@Autowired
	TransaksiTelkomService transaksiTelkomService;

	
	//Pdf
	//setiap tanggal 1 jam 7
	@Scheduled(cron = "0 0 7 1 * ?")
	public void sendEmailDay() throws Exception{
			MimeMessage mailMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, - 1);
			Date tanggal = cal.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
			String bulan = dateFormat.format(tanggal);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy");
			String tahun = dateFormat1.format(tanggal);
			
			
			SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", new Locale("in", "ID"));
			String dateString = format.format(tanggal);
			
			List<TransaksiTelkom> data = transaksiTelkomRepository.laporanPenunggakanMonthly(bulan,tahun);
			String title = "Laporan Penunggakan bulan  " + dateString;
						
			helper.setTo("usernamemeeting@gmail.com");
			helper.setCc("haha1hihi2huhu3@gmail.com");
			helper.setSubject("Laporan Penunggakan bulan " + dateString);
			helper.setText("Laporan penunggakan "+ dateString, true);
			
			ByteArrayOutputStream pdf = transaksiTelkomService.ExportToPdfParam(data, title);
			helper.addAttachment(title + ".pdf", new ByteArrayResource(pdf.toByteArray()));
			javaMailSender.send(mailMessage);
			System.out.println("Email send");
	}
	
	//Excel
	//setiap tanggal 1 jam 7
	@Scheduled(cron = "0 12 * * * ?")
	public void sendEmailDayExcel() throws Exception{
			MimeMessage mailMessage = javaMailSender.createMimeMessage();

			MimeMessageHelper helper = new MimeMessageHelper(mailMessage, true);
			
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.DATE, - 1);
			Date tanggal = cal.getTime();
			SimpleDateFormat dateFormat = new SimpleDateFormat("MM");
			String bulan = dateFormat.format(tanggal);
			SimpleDateFormat dateFormat1 = new SimpleDateFormat("yyyy");
			String tahun = dateFormat1.format(tanggal);
			
			
			SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", new Locale("in", "ID"));
			String dateString = format.format(tanggal);
			
			List<TransaksiTelkom> data = transaksiTelkomRepository.laporanPenunggakanMonthly(bulan,tahun);
			String title = "Laporan Penunggakan bulan  " + dateString;
			//List<TransaksiTelkomWrapper> listUsers = transaksiTelkomService.findAllStatus1();			
			helper.setTo("usernamemeeting@gmail.com");
			helper.setCc("haha1hihi2huhu3@gmail.com");
			helper.setSubject("Laporan Penunggakan bulan " + dateString);
			helper.setText("Laporan penunggakan "+ dateString, true);
			
			//ByteArrayOutputStream pdf = transaksiTelkomService.ExportToPdfParam(data, title);
			LaporanPenunggakanExcelExporter excelExporter = new LaporanPenunggakanExcelExporter(data);
			ByteArrayOutputStream excel = excelExporter.export();
			
			helper.addAttachment(title + ".xlsx", new ByteArrayResource(excel.toByteArray()));
			javaMailSender.send(mailMessage);
			System.out.println("Email send");
	}
	
}
