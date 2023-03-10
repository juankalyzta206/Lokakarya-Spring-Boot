package com.ogya.lokakarya.controller.telepon;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ogya.lokakarya.entity.telepon.TransaksiTelkom;
import com.ogya.lokakarya.service.telepon.TransaksiTelkomService;
import com.ogya.lokakarya.util.DataResponse;
import com.ogya.lokakarya.util.DataResponsePagination;
import com.ogya.lokakarya.util.PagingRequestWrapper;
import com.ogya.lokakarya.wrapper.telepon.TransaksiTelkomWrapper;

@RestController
@RequestMapping(value = "/transaksitelkom")
@CrossOrigin(origins = "*")
public class TransaksiTelkomController {
	@Autowired
	TransaksiTelkomService transaksiTelkomService;

	@GetMapping(path = "/findAllPlan")
	public List<TransaksiTelkomWrapper> findAllPlan() {
		return transaksiTelkomService.findAll();
	}

	@GetMapping(path = "/findAllSortByMonth")
	public List<TransaksiTelkomWrapper> findAllSortByMonth() {
		return transaksiTelkomService.findAllSortByMonthAndYear();
	}

	@GetMapping(path = "/findStatus1")
	public List<TransaksiTelkomWrapper> findStatus1() {
		return transaksiTelkomService.findAllStatus1();
	}

	@PostMapping(path = "/")
	public DataResponse<TransaksiTelkomWrapper> save(@RequestBody TransaksiTelkomWrapper wrapper) {
		return new DataResponse<TransaksiTelkomWrapper>(transaksiTelkomService.save(wrapper));
	}

	@PutMapping(path = "/")
	public DataResponse<TransaksiTelkomWrapper> update(@RequestBody TransaksiTelkomWrapper wrapper) {
		return new DataResponse<TransaksiTelkomWrapper>(transaksiTelkomService.save(wrapper));
	}

	@DeleteMapping(path = "/deleteById")
	public void delete(@RequestParam("id") Long transaksiTelkomId) {
		transaksiTelkomService.deleteById(transaksiTelkomId);
	}

	@GetMapping(path = "/sumAll")
	public Long sumAll() {
		return transaksiTelkomService.sumAll();
	}

	@GetMapping(path = "/findAllWithPagination")
	public DataResponsePagination<TransaksiTelkomWrapper, TransaksiTelkom> findAllWithPagination(
			@RequestParam("page") int page, @RequestParam("size") int size) {
		return new DataResponsePagination<TransaksiTelkomWrapper, TransaksiTelkom>(
				transaksiTelkomService.findAllWithPagination(page, size));
	}

	@RequestMapping(value = "/exportToPdfALL", method = RequestMethod.GET)
	public void exportToPdf(HttpServletResponse response) throws Exception {
		transaksiTelkomService.ExportToPdf(response);
	}

	@RequestMapping(value = "/findAllWithPaginationAndFilter", method = RequestMethod.POST)
	public DataResponsePagination<TransaksiTelkomWrapper, TransaksiTelkom> findAllWithPaginationAndFilter(
			@RequestBody(required = true) PagingRequestWrapper request) {
		return new DataResponsePagination<TransaksiTelkomWrapper, TransaksiTelkom>(
				transaksiTelkomService.ListWithPaging(request));
	}

	@GetMapping("/download")
	public void exportToExcel(HttpServletResponse response) throws Exception {
		response.setContentType("application/octet-stream");
		DateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
		String currentDateTime = dateFormatter.format(new Date());

		String headerKey = "Content-Disposition";
		String headerValue = "attachment; filename=users_" + currentDateTime + ".xlsx";
		response.setHeader(headerKey, headerValue);

		List<TransaksiTelkom> listUsers = transaksiTelkomService.findAllStatus1NoWrapper();
		transaksiTelkomService.ExportToExcelParam(listUsers, response);
	}
	
	@GetMapping("/downloadPdf")
	public void exportToPdfParam(HttpServletResponse response) throws Exception {
		

		List<TransaksiTelkom> listUsers = transaksiTelkomService.findAllStatus1NoWrapper();
		transaksiTelkomService.ExportToPdfParam(listUsers, response);
	}
}
