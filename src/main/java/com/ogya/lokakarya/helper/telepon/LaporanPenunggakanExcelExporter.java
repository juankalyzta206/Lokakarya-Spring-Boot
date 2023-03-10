package com.ogya.lokakarya.helper.telepon;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.text.NumberFormat;

import java.util.List;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;

import com.ogya.lokakarya.configuration.telepon.LaporanPenunggakanConfigurationProperties;
import com.ogya.lokakarya.entity.telepon.TransaksiTelkom;
import com.ogya.lokakarya.util.CurrencyData;

public class LaporanPenunggakanExcelExporter {
	@Autowired
	LaporanPenunggakanConfigurationProperties laporanPenunggakanConfigurationProperties;
	private XSSFWorkbook workbook;
	private XSSFSheet sheet;
	private List<TransaksiTelkom> listUsers;

	public LaporanPenunggakanExcelExporter(List<TransaksiTelkom> listUsers) {
		this.listUsers = listUsers;
		workbook = new XSSFWorkbook();
	}

	private void writeHeaderLine() throws IOException {

		sheet = workbook.createSheet("Laporan Penunggakan");

		Row row = sheet.createRow(0);

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setBold(true);
		font.setFontHeight(16);
		style.setFont(font);
		// System.out.println(laporanPenunggakanConfigurationProperties.getColumn());

		List<String> column2 = laporanPenunggakanConfigurationProperties.getColumn();
		int i = 0;
		for (String columnName : column2) {
			i++;

			createCell(row, i, columnName, style);
		}

//	        createCell(row, 0, "ID Transaksi", style);      
//	        createCell(row, 1, "Nama", style);       
//	        createCell(row, 2, "Bulan Tagihan", style);    
//	        createCell(row, 3, "Tahun Tagihan", style);
//	        createCell(row, 4, "Nominal", style);
//	        createCell(row, 5, "Status", style);

	}

	private void createCell(Row row, int columnCount, Object value, CellStyle style) {
		sheet.autoSizeColumn(columnCount);
		Cell cell = row.createCell(columnCount);
		if (value instanceof Long) {
			cell.setCellValue((Long) value);
		} else if (value instanceof Boolean) {
			cell.setCellValue((Boolean) value);
		} else if (value instanceof Integer) {
			cell.setCellValue((Integer) value);
		} else if (value instanceof Byte) {
			cell.setCellValue((Byte) value);
		} else {
			cell.setCellValue((String) value);
		}
		cell.setCellStyle(style);
	}

	private void writeDataLines() {
		int rowCount = 1;

		CellStyle style = workbook.createCellStyle();
		XSSFFont font = workbook.createFont();
		font.setFontHeight(14);
		style.setFont(font);

		for (TransaksiTelkom entity : listUsers) {
			Row row = sheet.createRow(rowCount++);
			int columnCount = 0;

			createCell(row, columnCount++, entity.getIdTransaksi(), style);
			createCell(row, columnCount++, entity.getIdPelanggan().getNama(), style);
			createCell(row, columnCount++, entity.getBulanTagihan(), style);
			createCell(row, columnCount++, entity.getTahunTagihan(), style);
			NumberFormat numberFormat = NumberFormat.getCurrencyInstance(new Locale("in", "ID"));
			CurrencyData currencyNominal = new CurrencyData();
			currencyNominal.setValue(entity.getUang());
			createCell(row, columnCount++, String.valueOf(numberFormat.format(currencyNominal.getValue())), style);
			createCell(row, columnCount++, "Belum lunas", style);

		}
	}

	public void export(HttpServletResponse response) throws IOException {
		writeHeaderLine();
		writeDataLines();

		ServletOutputStream outputStream = response.getOutputStream();
		workbook.write(outputStream);
		workbook.close();

		outputStream.close();

	}

	public ByteArrayOutputStream export() throws IOException {
		writeHeaderLine();
		writeDataLines();

		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		workbook.write(outputStream);
		workbook.close();

		return outputStream;

	}
}
