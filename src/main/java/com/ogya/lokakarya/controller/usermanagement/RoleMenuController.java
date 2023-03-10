package com.ogya.lokakarya.controller.usermanagement;

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

import com.ogya.lokakarya.entity.usermanagement.RoleMenu;
import com.ogya.lokakarya.service.usermanagement.RoleMenuService;
import com.ogya.lokakarya.util.DataResponse;
import com.ogya.lokakarya.util.DataResponseList;
import com.ogya.lokakarya.util.DataResponsePagination;
import com.ogya.lokakarya.util.PagingRequestWrapper;
import com.ogya.lokakarya.wrapper.usermanagement.RoleMenuWrapper;

@RestController
@RequestMapping(value = "/roleMenu")
@CrossOrigin(origins = "*")
public class RoleMenuController {
	@Autowired
	RoleMenuService roleMenuService;

	// findAllPagination
	@GetMapping(path = "/findAllWithPagination")
	public DataResponsePagination<RoleMenuWrapper, RoleMenu> findAllWithPagination(@RequestParam("page") int page,
			@RequestParam("size") int size) {
		return new DataResponsePagination<RoleMenuWrapper, RoleMenu>(roleMenuService.findAllWithPagination(page, size));
	}

	@GetMapping(path = "/findAllPlan")
	public List<RoleMenuWrapper> findAllPlan() {
		return roleMenuService.findAll();
	}

	@GetMapping(path = "/findAll")
	public DataResponseList<RoleMenuWrapper> findAll() {
		return new DataResponseList<RoleMenuWrapper>(roleMenuService.findAll());
	}

	@PostMapping(path = "/")
	public DataResponse<RoleMenuWrapper> save(@RequestBody RoleMenuWrapper wrapper) {
		return new DataResponse<RoleMenuWrapper>(roleMenuService.save(wrapper));
	}

	@PutMapping(path = "/")
	public DataResponse<RoleMenuWrapper> update(@RequestBody RoleMenuWrapper wrapper) {
		return new DataResponse<RoleMenuWrapper>(roleMenuService.save(wrapper));
	}

	@DeleteMapping(path = "/deleteById")
	public void deleteId(@RequestParam("id") Long roleRoleMenuId) {
		roleMenuService.delete(roleRoleMenuId);
	}

	@RequestMapping(value = "/exportToPdfALL", method = RequestMethod.GET)
	public void exportToPdf(HttpServletResponse response) throws Exception {
		roleMenuService.ExportToPdf(response);
	}

	@PostMapping(value = "/findAllWithPaginationAndFilter")
	public DataResponsePagination<RoleMenuWrapper, RoleMenu> findAllWithPaginationAndFilter(
			@RequestBody(required = true) PagingRequestWrapper request) {
		return new DataResponsePagination<RoleMenuWrapper, RoleMenu>(roleMenuService.ListWithPaging(request));
	}

}
