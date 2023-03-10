/*
* LaporanPenambahanUserNotification.java
*	This class provide service for daily, weekly and monthly notification
*	for users table
*
* Version 1.0
*
* Copyright : Irzan Maulana, Backend Team OGYA
*/

package com.ogya.lokakarya.notification.usermanagement;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.transaction.Transactional;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamSource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.ogya.lokakarya.configuration.usermanagement.UsersColumnProperties;
import com.ogya.lokakarya.entity.usermanagement.Users;
import com.ogya.lokakarya.repository.usermanagement.UsersRepository;
import com.ogya.lokakarya.util.ExportData;
import com.ogya.lokakarya.wrapper.usermanagement.NotificationWrapper;

@Service
@Transactional
public class LaporanPenambahanUserNotification {
	@Autowired
	UsersRepository usersRepository;

	@Autowired
	private JavaMailSender javaMailSender;

	@Autowired
	UsersColumnProperties laporanPenambahanUserConfigurationProperties;
	
	@Value("${cron.daily}")
	private String dailyCron;

	@Value("${cron.monthly}")
	private String monthlyCron;

	@Value("${cron.weekly}")
	private String weeklyCron;

	/* array receiver and cc email for notification service */
	private String[] receiver = { "maulanairzan5@gmail.com" };
	private String[] cc = { "taerakim.21@gmail.com", "eonjejjeumilkka@gmail.com" };

	private static final long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

	@Scheduled(cron ="${cron.daily}")
	/* run daily notification everyday at 7 am */
	public void DailyNotification() throws Exception {
		Date date = FindPrevDay(new Date());

		SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("in", "ID"));
		String day = format.format(date);

		NotificationWrapper description = new NotificationWrapper();
		description.setReceiver(receiver);
		description.setCc(cc);
		description.setSubject("Laporan Penambahan User Harian");
		description.setTopHeader("Laporan Penambahan User Harian");
		description.setBotHeader("Hari : " + day);
		description.setTitlePdf("Laporan Penambahan User Harian " + day);
		description.setFileName("LaporanPenambahanUserHarian(" + day + ")");

		List<Users> dailyData = usersRepository.newUsersDaily();
		List<InputStreamSource> attachments = new ArrayList<>();
		List<String> attachmentsName = new ArrayList<>();
		attachments.add(ExportToPdfNotification(dailyData, description));
		attachmentsName.add(description.getFileName() + ".pdf");
		attachments.add(WriteExcelToEmail(dailyData, description));
		attachmentsName.add(description.getFileName() + ".xls");
		SendEmailWithAttachment(attachments, attachmentsName, description);

	}

	@Scheduled(cron = "${cron.monthly}") 
	/* run monthly notification every date 1 of the month at 7 am */
	public void MonthlyNotification() throws Exception {
		Date date = FindPrevDay(new Date());

		SimpleDateFormat format = new SimpleDateFormat("MMMM yyyy", new Locale("in", "ID"));
		String month = format.format(date);

		NotificationWrapper description = new NotificationWrapper();
		description.setReceiver(receiver);
		description.setCc(cc);
		description.setSubject("Laporan Penambahan User Bulanan");
		description.setTopHeader("Laporan Penambahan User Bulanan");
		description.setBotHeader("Bulan : " + month);
		description.setTitlePdf("Laporan Penambahan User Bulanan(" + month + ")");
		description.setFileName("LaporanPenambahanUserBulanan(" + month + ")");

		List<Users> monthlyData = usersRepository.newUsersMonthly();
		List<InputStreamSource> attachments = new ArrayList<>();
		List<String> attachmentsName = new ArrayList<>();
		attachments.add(ExportToPdfNotification(monthlyData, description));
		attachmentsName.add(description.getFileName() + ".pdf");
		attachments.add(WriteExcelToEmail(monthlyData, description));
		attachmentsName.add(description.getFileName() + ".xls");
		SendEmailWithAttachment(attachments, attachmentsName, description);
	}

	@Scheduled(cron = "${cron.weekly}")
	/* run weekly notification every monday 7 am */
	public void WeeklyNotification() throws Exception {
		Date date = FindPrevDay(new Date());
		Calendar calendar = new GregorianCalendar();
		calendar.setTime(date);

		SimpleDateFormat format = new SimpleDateFormat("EEEE, dd/MM/yyyy", new Locale("in", "ID"));
		String weekEnd = format.format(date);

		Calendar calendarWeekStart = new GregorianCalendar();
		calendarWeekStart.setTime(date);
		calendarWeekStart.add(Calendar.DATE, -6);
		Date dateStart = calendarWeekStart.getTime();
		String weekStart = format.format(dateStart);

		NotificationWrapper description = new NotificationWrapper();
		description.setReceiver(receiver);
		description.setCc(cc);
		description.setSubject("Laporan Penambahan User Mingguan");
		description.setTopHeader("Laporan Penambahan User Mingguan");
		description.setBotHeader("Hari : " + weekStart + " - " + weekEnd);
		description.setTitlePdf("Laporan Penambahan User Mingguan(" + weekStart + " - " + weekEnd + ")");
		description.setFileName("LaporanPenambahanUserMingguan(" + weekStart + " - " + weekEnd + ")");

		List<Users> weeklyData = usersRepository.newUsersWeekly();
		List<InputStreamSource> attachments = new ArrayList<>();
		List<String> attachmentsName = new ArrayList<>();
		attachments.add(ExportToPdfNotification(weeklyData, description));
		attachmentsName.add(description.getFileName() + ".pdf");
		attachments.add(WriteExcelToEmail(weeklyData, description));
		attachmentsName.add(description.getFileName() + ".xls");
		SendEmailWithAttachment(attachments, attachmentsName, description);
	}

	private static Date FindPrevDay(Date date) {
		
		return new Date(date.getTime() - MILLIS_IN_A_DAY);
	}

	public void SendEmailWithAttachment(List<InputStreamSource> data, List<String> fileNames,
			NotificationWrapper description) {
		/* service for send email with attachment */
		MimeMessage mimeMessage = javaMailSender.createMimeMessage();
		try {
			MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
			mimeMessageHelper.setTo(description.getReceiver());
			mimeMessageHelper.setCc(description.getCc());
			mimeMessageHelper.setSubject(description.getSubject());

			StringBuffer buffer = new StringBuffer();
			buffer.append("<h3>" + description.getTopHeader() + "</h3>");
			buffer.append("<h5>" + description.getBotHeader() + "</h5>");
			String body = buffer.toString();
			mimeMessageHelper.setText(body, true);

			for (int i = 0; i < data.size(); i++) {
				String fileName = fileNames.get(i);
				mimeMessageHelper.addAttachment(fileName, data.get(i));
			}
			javaMailSender.send(mimeMessage);
			System.out.println("Email sent");
		} catch (MessagingException e) {
			System.err.print("Failed send email");
			e.printStackTrace();
		}
	}

	public PdfPCell Align(String title) {
		PdfPCell cell = new PdfPCell(new Phrase(title));
		cell.setHorizontalAlignment(PdfPCell.ALIGN_CENTER);
		cell.setVerticalAlignment(PdfPCell.ALIGN_CENTER);
		return cell;
	}

	public InputStreamSource ExportToPdfNotification(List<Users> data, NotificationWrapper description)
			throws Exception {
		List<String> columnNames = laporanPenambahanUserConfigurationProperties.getColumn();
		int columnLength = columnNames.size();

		/* Create a new iText PDF document */
		Document pdfDoc = new Document(PageSize.A4.rotate());
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		PdfWriter pdfWriter = PdfWriter.getInstance(pdfDoc, baos);
		pdfDoc.open();

		Paragraph title = new Paragraph(description.getTitlePdf(), new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD));
		title.setAlignment(Element.ALIGN_CENTER);
		pdfDoc.add(title);

		/* Add the generation date */
		pdfDoc.add(new Paragraph(
				"Report generated on: " + new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date())));

		/* Create a table */
		PdfPTable pdfTable = new PdfPTable(columnLength);

		pdfTable.setWidthPercentage(100);
		pdfTable.setSpacingBefore(10f);
		pdfTable.setSpacingAfter(10f);

		/* Iterate through the data and add it to the table */
		ExportData<Users> parsing = new ExportData<Users>();
		pdfTable = parsing.exportPdf(columnNames, data, pdfTable);

		/* Add the table to the pdf document */
		pdfDoc.add(pdfTable);

		pdfDoc.close();
		pdfWriter.close();
		byte[] bytes = baos.toByteArray();
		InputStreamSource attachmentSource = new ByteArrayResource(bytes);
		return attachmentSource;
	}

	public InputStreamSource WriteExcelToEmail(List<Users> data, NotificationWrapper description) throws IOException {
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("Users");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		/* Create the header row */
		Row headerRow = sheet.createRow(0);
		List<String> columnNames = laporanPenambahanUserConfigurationProperties.getColumn();
		int columnLength = columnNames.size();

		for (int i = 0; i < columnLength; i++) {
			Cell cell = headerRow.createCell(i);
			cell.setCellValue(columnNames.get(i));
		}

		/* Iterate through the data and add it to the sheet */
		ExportData<Users> parsing = new ExportData<Users>();
		sheet = parsing.exportExcel(columnNames, data, sheet);
		

		/* Write the workbook to the output file */
		workbook.write(baos);
		baos.flush();
		baos.close();
		byte[] bytes = baos.toByteArray();
		InputStreamSource attachmentSource = new ByteArrayResource(bytes);
		workbook.close();
		return attachmentSource;
	}
}
